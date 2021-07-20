(ns astronomy.model.astronomical-coordinate
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]))

;; 天球坐标系
;; * 天球坐标系：Astronomical coordinate
;; * 原点：某个天体为中心，可选择，并跟随
;; * 坐标轴：固定不可改
;;    * y方向：北天极方向
;;    * z方向：春分点


(def schema 
  {:astronomical-coordinate/center-object {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


(s/def :astronomy/astronomical-coordinate
  (s/keys :req [:astronomical-coordinate/center-object]
          :opt []))

(comment 
  (def sample
    #:astronomical-coordinate {:object/position [0 0 0]
                               :object/quaternion [0 0 0 1]
                               :coordinate/name "赤道天球坐标系"
                               :coordinate/type :astronomical-coordinate
                               :astronomical-coordinate/center-candidates [{:db/id [:planet/name "earth"]}
                                                                           {:db/id [:planet/name "sun"]}]
                               :astronomical-coordinate/center-object [:planet/name "earth"]
                               :astronomical-coordinate/quaternion [0 0 0 1]})

  (s/valid? :astronomy/astronomical-coordinate sample)

;; 
  )

;; transform


;; sub

;; tx

(defn update-position-tx [db astronomical-coordinate]
  {:pre [(s/assert :astronomy/astronomical-coordinate astronomical-coordinate)]}
  (let [pulled-one (d/pull db '[* {:astronomical-coordinate/center-object [:object/position]}] (:db/id astronomical-coordinate))]
    [{:db/id (:db/id pulled-one)
      :object/position (get-in pulled-one [:astronomical-coordinate/center-object :object/position])}]))

