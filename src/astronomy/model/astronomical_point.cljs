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


(s/def :astronomical-point/radius float?)
(s/def :astronomical-point/longitude float?)
(s/def :astronomical-point/latitude float?)
(s/def :astronomy/astronomical-point
  (s/keys :req [:astronomical-point/radius :astronomical-point/longitude :astronomical-point/latitude]
          :opt [:astronomical-point/name]))


;; create 

(defn astronomical-point
  "创建天球坐标点的标准方法"
  ([longitude latitude]
   #:astronomical-point {:radius const/astronomical-sphere-radius
                         :longitude longitude
                         :latitude latitude
                         :size 1
                         :coordinate {:db/id [:coordinate/name "赤道天球坐标系"]}})
  ([longitude latitude name]
   #:astronomical-point {:radius const/astronomical-sphere-radius
                         :longitude longitude
                         :latitude latitude
                         :name name
                         :size 1
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
  {:pre [(s/valid? :astronomy/astronomical-point apt)]}
  (let [{:astronomical-point/keys [radius longitude latitude]} apt
        cc (shu.cc/celestial-coordinate longitude latitude radius)]
    (shu.cc/cal-position cc)))


(defn get-longitude-and-latitude [apt]
  {:pre [(s/valid? :astronomy/astronomical-point apt)]}
  (let [{:astronomical-point/keys [longitude latitude]} apt]
    [longitude latitude]))


(defn distance-in-degree [apt1 apt2]
  {:pre [(s/valid? :astronomy/astronomical-point apt1)
         (s/valid? :astronomy/astronomical-point apt1)]}
  (let [cc1 (shu.cc/celestial-coordinate (:astronomical-point/longitude apt1)
                                         (:astronomical-point/latitude apt1))
        cc2 (shu.cc/celestial-coordinate (:astronomical-point/longitude apt2)
                                         (:astronomical-point/latitude apt2))]
    (shu.cc/distance-in-degree cc1 cc2)))

;; find and pull

(defn find-all-ids [db]
  (d/q '[:find [?id ...]
         :in $
         :where [?id :astronomical-point/longitude _]]
       db))

(defn get-latest-id [db]
  (first (sort > (find-all-ids db))))


;; sub

(def query-all-ids-by-coordinate
  '[:find [?id ...]
    :in $ ?cid
    :where [?id :astronomical-point/coordinate ?cid]])

(defn sub-all-ids-by-coordinate [conn coordinate]
  @(p/q query-all-ids-by-coordinate
        conn (:db/id coordinate)))


;; tx

(defn create-astronomical-point-tx [props]
  (let [{:keys [longitude latitude size name]} props
        apt (-> (astronomical-point longitude latitude)
                (#(if name (assoc % :astronomical-point/name name) %))
                (#(if name (assoc % :astronomical-point/size size) %)))]
    [apt]))

(defn set-size-tx [apt1 size]
  {:pre [(s/valid? :methodology/entity apt1)]}
  [{:db/id (:db/id apt1)
    :astronomical-point/size size}])

(defn delete-astronomical-point-tx [apt1]
  [[:db.fn/retractEntity (:db/id apt1)]])


(comment 
  (s/valid? :astronomy/astronomical-point (astronomical-point 30 40))
  )