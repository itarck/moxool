(ns astronomy.model.terrestrial-coordinate
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [astronomy.model.const :as m.const]
   [astronomy.model.planet :as m.planet]
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
  (def sample
    #:terrestrial-coordinate {:object/position [0 0 0]
                              :object/quaternion [0 0 0 1]
                              :coordinate/name "地球坐标系"
                              :coordinate/type :terrestrial-coordinate

                              :terrestrial-coordinate/center-object [:planet/name "earth"]}))


;; tx

(defn update-position-and-quaternion-tx [db id]
  (let [pulled-one (d/pull db '[* {:terrestrial-coordinate/center-object [*]}] id)
        center-object (:terrestrial-coordinate/center-object pulled-one)
        position (case (:entity/type center-object)
                   :star (:object/position center-object)
                   :planet (m.planet/cal-world-position db center-object)
                   :satellite (m.satellite/cal-world-position db center-object))]
    [{:db/id (:db/id pulled-one)
      :object/position position
      :object/quaternion (get-in center-object [:object/quaternion])}]))

