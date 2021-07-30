(ns astronomy.model.astronomical-point
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]))


;; 天球坐标系下的点，缩写 apt

(def apt-1
  #:astronomical-point {:db/id -10001
                        :point [100 100 100]
                        :coordinate [:coordinate/name "赤道天球坐标系"]})


;; schema

(def schema
  {:astronomical-point/coordinate {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; transform

(defn get-longitude-and-latitude [apt-1]
  
  )