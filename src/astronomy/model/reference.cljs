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
             :reference/center-object {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
             :reference/orientation-object {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

(comment


  (def ref-1
    #:reference {:name "赤道天球坐标系-太阳中心"
                 :clock [:clock/name "default"]
                 :center-type :static
                 :center-position [0 0 0]
                 :orientation-type :static
                 :orientation-quaternion [0 0 0 1]
                 :position [0 0 0]
                 :quaternion [0 0 0 1]})

  (def ref-2
    #:reference {:name "赤道天球坐标系-地球中心"
                 :clock [:clock/name "default"]
                 :center-type :dynamic
                 :center-object {:db/id [:planet/name "earth"]}
                 :orientation-type :static
                 :orientation-quaternion [0 0 0 1]})

  (def ref-3
    #:reference {:name "赤道天球坐标系-地球中心"
                 :clock [:clock/name "default"]
                 :center-type :dynamic
                 :center-object {:db/id [:planet/name "earth"]}
                 :orientation-type :dynamic
                 :orientation-object {:db/id [:planet/name "earth"]}})

  (def ref-4
    #:reference {:name "自定义参考系"
                 :clock [:clock/name "default"]
                 :center-type :dynamic
                 :center-candidates [{:db/id [:planet/name "earth"]}
                                     {:db/id [:star/name "sun"]}]
                 :center-object {:db/id [:planet/name "earth"]}
                 :orientation-type :dynamic
                 :orientation-candidates [{:db/id [:planet/name "earth"]}
                                          {:db/id [:star/name "sun"]}]
                 :orientation-object {:db/id [:planet/name "earth"]}})

;; 
  )


;; model

(defn find-ids-by-clock [db clock-id]
  (d/q '[:find [?id ...]
         :in $ ?clock-id
         :where [?id :reference/clock ?clock-id]]
       db clock-id))

(defn cal-world-position [db id]
  (let [ref (d/pull db '[* {:reference/center-object [:db/id :entity/type]}] id)
        p-object (d/pull db '[*] (-> ref :reference/center-object :db/id))]
    (if (= (:reference/center-type ref) :static)
      (:reference/center-position ref)
      (case (:entity/type p-object)
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
                           (:object/position star)))))))

(defn cal-world-quaternion [db id]
  (let [ref (d/pull db '[* {:reference/orientation-object [:db/id :entity/type]}] id)]
    (case (:reference/orientation-type ref)
      :static (:reference/orientation-quaternion ref)
      :dynamic (let [r-object (d/pull db '[*] (-> ref :reference/orientation-object :db/id))]
                 (:object/quaternion r-object)))))


(defn cal-invert-matrix [coor]
  (let [{:reference/keys [position quaternion]} coor
        mat (m4/compose (v3/from-seq position) (q/from-seq quaternion) (v3/vector3 1 1 1))]
    (m4/invert mat)))

(defn original-position [coor-1]
  (v3/apply-matrix4 (v3/vector3 0 0 0) (cal-invert-matrix coor-1)))

(defn is-earth-center? [coor-1]
  (= (get-in coor-1 [:reference/center-object :planet/name])
     "earth"))

;; tx 

(defn update-reference-tx [db id]
  [[:db/add id :reference/position (cal-world-position db id)]
   [:db/add id :reference/quaternion (cal-world-quaternion db id)]])

