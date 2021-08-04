(ns astronomy.model.astronomical-point
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [shu.astronomy.celestial-coordinate :as shu.cc]
   [astronomy.model.coordinate :as m.coordinate]
   [astronomy.model.const :as const]))


;; 天球点，缩写 apt
;; 天球点是天球上的点，半径被固定在天球上，默认按照赤道天球坐标系保存。


(def apt-1
  #:astronomical-point
   {:db/id -10001
    :radius const/astronomical-sphere-radius
    :longitude 90
    :latitude 30
    :coordinate {:db/id [:coordinate/name "赤道天球坐标系"]}})


;; schema

(def schema
  {:astronomical-point/name {:db/unique :db.unique/identity}
   :astronomical-point/coordinate {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; create 

(defn astronomical-point
  "创建天球坐标点的标准方法"
  ([longitude latitude]
   #:astronomical-point {:radius const/astronomical-sphere-radius
                         :longitude longitude
                         :latitude latitude
                         :coordinate {:db/id [:coordinate/name "赤道天球坐标系"]}})
  ([longitude latitude name]
   #:astronomical-point {:radius const/astronomical-sphere-radius
                         :longitude longitude
                         :latitude latitude
                         :name name
                         :coordinate {:db/id [:coordinate/name "赤道天球坐标系"]}}))


(defn from-position 
  "把给定的位置投射到天球上，给出天球点"
  [position]
  (let [{:celestial-coordinate/keys [latitude longitude]} (shu.cc/from-vector position)]
    (astronomical-point longitude latitude)))

(defn from-local-camera-view
  [local-coordinate local-position local-direction]
  (let [local-vector3 (v3/add (v3/multiply-scalar (v3/from-seq local-direction) const/astronomical-sphere-radius)
                              (v3/from-seq local-position))
        system-vector (m.coordinate/to-system-vector local-coordinate local-vector3)
        apt-1 (from-position system-vector)]
    apt-1))

;; transform

(defn cal-position-vector3 [apt]
  (let [{:astronomical-point/keys [radius longitude latitude]} apt
        cc (shu.cc/celestial-coordinate longitude latitude radius)]
    (shu.cc/cal-position cc)))


(defn get-longitude-and-latitude [apt]
  (let [{:astronomical-point/keys [longitude latitude]} apt]
    [longitude latitude]))


;; sub

(def query-all-ids-by-coordinate
  '[:find [?id ...]
    :in $ ?cid
    :where [?id :astronomical-point/coordinate ?cid]])

(defn sub-all-ids-by-coordinate [conn coordinate]
  @(p/q query-all-ids-by-coordinate
        conn (:db/id coordinate)))


;; tx

(defn delete-astronomical-point-tx [apt1]
  [[:db.fn/retractEntity (:db/id apt1)]])

