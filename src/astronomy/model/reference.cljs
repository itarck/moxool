(ns astronomy.model.reference
  (:require
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.three.matrix4 :as m4]))

;; 一个参考系有几个因素
;; 1. 坐标系中心：中心也分为静止，还是跟随某个天体旋转
;; 2. 坐标系姿态：姿态分为按天球静止，还是跟随某个天体旋转


(def schema {:reference/name {:db/unique :db.unique/identity}
             :reference/clock {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
             :reference/track-position {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
             :reference/track-rotation {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

(comment

  (def ref-1
    #:reference {:name "黄道天球参考系-太阳中心"
                 :center {:type :static
                          :position [0 0 0]}
                 :orientation  {:type :static
                                :quaternion [0 0 0 1]}})

  (def ref-2
    #:reference {:name "赤道天球参考系-地球中心"
                 :clock [:clock/name "default"]
                 :center {:type :dynamic
                          :track-position [:planet/name "earth"]}
                 :orientation {:type :static
                               :quaternion [0 0 0 1]}})

  (def ref-3
    #:reference {:name "地球参考系-地球中心"
                 :clock [:clock/name "default"]
                 :center {:type :dynamic
                          :track-position [:planet/name "earth"]}
                 :orientation {:type :dynamic
                               :track-rotation [:planet/name "earth"]}})
  
  (def ref-4
    #:reference {:name "自定义参考系"
                 :clock [:clock/name "default"]
                 :center {:type :dynamic
                          :track-candidates [{:db/id [:planet/name "earth"]}]
                          :track-position [:planet/name "earth"]}
                 :orientation  {:type :dynamic
                                :track-rotation [:planet/name "earth"]}})
;; 
  )


;; model


(defn find-ids-by-clock [db clock-id]
  (d/q '[:find [?id ...]
         :in $ ?clock-id
         :where [?id :reference/clock ?clock-id]]
       db clock-id))


(defn sub-reference-fully [conn id]
  @(p/pull conn '[* {:reference/track-position [*]
                     :reference/track-rotation [*]}] id))

(defn cal-world-position [db id]
  (let [coor (d/pull db '[* {:reference/track-position [:db/id :entity/type]}] id)
        p-object (d/pull db '[*] (-> coor :reference/track-position :db/id))
        world-position (case (:entity/type p-object)
                         :star (:object/position p-object)
                         :planet (let [planet p-object
                                       star (d/pull db '[*] (-> planet :planet/star :db/id))]
                                   (mapv + (:object/position planet)
                                         (:object/position star)))
                         :satellite (let [satellite p-object
                                          planet (d/pull db '[*] (-> satellite :satellite/planet :db/id))
                                          star (d/pull db '[*] (-> planet :planet/star :db/id))]
                                      (mapv + (:object/position satellite)
                                            (:object/position planet)
                                            (:object/position star))))]
    world-position))

(defn cal-world-quaternion [db id]
  (let [coor (d/pull db '[* {:reference/track-rotation [:db/id :entity/type]}] id)
        r-object (d/pull db '[*] (-> coor :reference/track-rotation :db/id))
        world-quaternion (:object/quaternion r-object)]
    world-quaternion))


(defn cal-invert-matrix [coor]
  (let [{:reference/keys [position quaternion]} coor
        mat (m4/compose (v3/from-seq position) (q/from-seq quaternion) (v3/vector3 1 1 1))]
    (m4/invert mat)))

(defn original-position [coor-1]
  (v3/apply-matrix4 (v3/vector3 0 0 0) (cal-invert-matrix coor-1)))

(defn is-earth-center? [coor-1]
  (= (get-in coor-1 [:reference/track-position :planet/name])
     "earth"))

;; tx 

(defn update-track-position-tx [reference-id track-position-id]
  [{:db/id reference-id
    :reference/track-position track-position-id}])

(defn update-track-rotation-tx [reference-id track-rotation-id]
  [{:db/id reference-id
    :reference/track-rotation track-rotation-id}])

(defn update-reference-tx [db id]
  [[:db/add id :reference/position (cal-world-position db id)]
   [:db/add id :reference/quaternion (cal-world-quaternion db id)]])


;; sub

(defn sub-reference [conn id]
  @(p/pull conn '[* {:reference/track-position [*]
                     :reference/track-rotation [*]}] id))




