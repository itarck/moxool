(ns astronomy.model.terrestrial-coordinate
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.quaternion :as q]
   [shu.three.vector3 :as v3]
   [shu.geometry.angle :as shu.angle]
   [astronomy.objects.planet.m :as m.planet]
   [astronomy.model.satellite :as m.satellite]))


;; * 地球坐标系：Terrestrial Coordinate
;;     * 中心天体：可选择，一般为地球
;;     * 原点：以天体为中心，可选择，并跟随变化
;;     * 坐标轴，选定天体后，跟随天体随时间变化
;;         * y方向：地球地理北极
;;         * z方向：2000年春分点
;;     * 随时间变化
;;         * 随地球旋转

(def schema
  {:terrestrial-coordinate/center-object {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

(s/def :astronomy/terrestrial-coordinate
  (s/keys :req [:terrestrial-coordinate/center-object]
          :opt []))

(comment
  (def terrestrial-coordinate-1
    #:terrestrial-coordinate
     {:db/id -1003
      :entity/type :terrestrial-coordinate
      :object/position [0 0 0]
      :object/quaternion [0 0 0 1]
      :object/scene [:scene/name "solar"]
      :coordinate/name "地球坐标系"
      :coordinate/type :terrestrial-coordinate

      :terrestrial-coordinate/longitude-0-offset -119.49298021035723
      :terrestrial-coordinate/radius 0.0215
      :terrestrial-coordinate/show-latitude? true
      :terrestrial-coordinate/show-longitude? true
      :terrestrial-coordinate/show-latitude-0? true
      :terrestrial-coordinate/show-longitude-0? true
      :terrestrial-coordinate/center-object [:planet/name "earth"]})


  (def terrestrial-coordinate-2
    (merge {:db/id -1003
            :entity/type :terrestrial-coordinate
            :object/position [0 0 0]
            :object/quaternion [0 0 0 1]
            :object/scene [:scene/name "solar"]}
           #:coordinate
            {:name "地球坐标系"
             :type :terrestrial-coordinate}
           #:terrestrial-coordinate
            {:longitude-0-offset -119.49298021035723
             :radius 0.0215
             :show-latitude? true
             :show-longitude? true
             :show-latitude-0? true
             :show-longitude-0? true
             :center-object [:planet/name "earth"]})))

;; query

(def coordinate-names-q
  '[:find [?name ...]
    :where
    [?id :coordinate/name ?name]
    [?id :entity/type :terrestrial-coordinate]])


;; transform

(defn find-coordinate-names [db]
  (d/q coordinate-names-q db))

(defn pull-one-by-name [db name]
  (d/pull db '[*] [:coordinate/name name]))

(defn cal-local-quaternion [terrestrial-coordinate]
  (let [{:terrestrial-coordinate/keys [longitude-0-offset]} terrestrial-coordinate]
    (q/from-axis-angle (v3/vector3 0 1 0) (shu.angle/to-radians longitude-0-offset))))

(defn cal-min-distance [db tc]
  (let [tc1 (d/pull db '[* {:terrestrial-coordinate/center-object [:celestial/radius]
                            :object/scene [:scene/scale]}]
                    (:db/id tc))]
    (* (get-in tc1 [:terrestrial-coordinate/center-object :celestial/radius])
       (get-in tc1 [:object/scene :scene/scale])
       1.1)))

;; sub

(defn sub-coordinate-names [conn]
  @(p/q coordinate-names-q conn))


;; tx

(defn change-show-latitude-tx [tc show]
  [{:db/id (:db/id tc)
    :terrestrial-coordinate/show-latitude? show}])

(defn change-show-longitude-tx [tc show]
  [{:db/id (:db/id tc)
    :terrestrial-coordinate/show-longitude? show}])


(defn update-position-and-quaternion-tx [db id]
  (let [pulled-one (d/pull db '[* {:terrestrial-coordinate/center-object [*]}] id)
        center-object (:terrestrial-coordinate/center-object pulled-one)
        position (case (:entity/type center-object)
                   :star (:object/position center-object)
                   :planet (m.planet/cal-world-position db center-object)
                   :satellite (m.satellite/cal-world-position db center-object))
        center-quaternion (q/from-seq (get-in center-object [:object/quaternion]))
        local-quaternion (cal-local-quaternion pulled-one)]
    [{:db/id (:db/id pulled-one)
      :object/position position
      :object/quaternion (seq (q/multiply center-quaternion local-quaternion))}]))
