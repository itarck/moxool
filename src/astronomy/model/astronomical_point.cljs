(ns astronomy.model.astronomical-point
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [shu.astronomy.celestial-coordinate :as shu.cc]))


;; 天球坐标系下的点，缩写 apt

(def apt-1
  #:astronomical-point
   {:db/id -10001
    :point [100 100 100]
    :coordinate [:coordinate/name "赤道天球坐标系"]})


;; schema

(def schema
  {:astronomical-point/name {:db/unique :db.unique/identity}
   :astronomical-point/coordinate {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; create 

(defn astronomical-point
  "创建天球坐标点的快捷方式"
  ([point]
   (astronomical-point {:db/id [:coordinate/name "赤道天球坐标系"]} point))
  ([coordinate point]
   #:astronomical-point {:coordinate coordinate
                         :point point}))


;; transform

(defn get-longitude-and-latitude [apt1]
  (let [{:celestial-coordinate/keys [latitude longitude]} (shu.cc/from-vector (:astronomical-point/point apt1))]
    [longitude latitude]))


;; tx

