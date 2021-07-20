(ns astronomy.model.terrestrial-coordinate
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [astronomy.model.const :as m.const]))


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

(defn update-position-and-quaternion-tx [db terrestrial-coordinate]
  {:pre [(s/assert :astronomy/terrestrial-coordinate terrestrial-coordinate)]}
  (let [pulled-one (d/pull db '[* {:terrestrial-coordinate/center-object [*]}] (:db/id terrestrial-coordinate))]
    [{:db/id (:db/id pulled-one)
      :object/position (get-in pulled-one [:terrestrial-coordinate/center-object :object/position])
      :object/quaternion (get-in pulled-one [:terrestrial-coordinate/center-object :object/quaternion])}]))

