(ns astronomy.objects.astronomical-coordinate.m
  (:require
   [cljs.spec.alpha :as s]
   [datascript.core :as d]
   [posh.reagent :as p]
   [astronomy.model.const :as m.const]
   [astronomy.objects.planet.m :as m.planet]
   [astronomy.model.satellite :as m.satellite]))


;; 天球坐标系
;; * 天球坐标系：Astronomical coordinate
;; * 原点：某个天体为中心，可选择，并跟随
;; * 坐标轴：固定不可改
;;    * y方向：北天极方向
;;    * z方向：春分点


;; schema

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
  (def sample2
    #:astronomical-coordinate {:db/id -1002
                               :object/position [0 0 0]
                               :object/quaternion m.const/ecliptic-quaternion
                               :coordinate/name "赤道黄道坐标系"
                               :coordinate/type :astronomical-coordinate
                               :astronomical-coordinate/center-candidates [{:db/id [:planet/name "earth"]}
                                                                           {:db/id [:planet/name "sun"]}]
                               :astronomical-coordinate/center-object [:planet/name "earth"]
                               :astronomical-coordinate/quaternion [0 0 0 1]})

  (s/valid? :astronomy/astronomical-coordinate sample)

;; 
  )

;; transform

(defn cal-min-distance [db ac]
  (let [tc1 (d/pull db '[* {:astronomical-coordinate/center-object [:celestial/radius]
                            :object/scene [:scene/scale]}]
                    (:db/id ac))]
    (* (get-in tc1 [:astronomical-coordinate/center-object :celestial/radius])
       (get-in tc1 [:object/scene :scene/scale])
       1.1)))


(defn cal-current-system-position
  "计算当前的系统位置"
  [db ac]
  (let [pulled-one (d/pull db '[{:astronomical-coordinate/center-object [*]}] (:db/id ac))
        center-object (:astronomical-coordinate/center-object pulled-one)
        position (case (:entity/type center-object)
                   :star (:object/position center-object)
                   :planet (m.planet/cal-current-system-position db center-object)
                   :satellite (m.satellite/cal-current-system-position db center-object))]
    position))


(defn cal-system-position-at-epoch-days
  [db ac epoch-days]
  (let [pulled-one (d/pull db '[{:astronomical-coordinate/center-object [*]}] (:db/id ac))
        center-object (:astronomical-coordinate/center-object pulled-one)
        position (case (:entity/type center-object)
                   :star (:object/position center-object)
                   :planet (m.planet/cal-system-position-at-epoch-days db center-object epoch-days)
                   :satellite (m.satellite/cal-system-position-at-epoch-days db center-object epoch-days))]
    position))


;; query

(def query-coordinate-names
  '[:find [?name ...]
    :where
    [?id :coordinate/name ?name]
    [?id :entity/type :astronomical-coordinate]])

;; find

(defn pull-one-by-name [db name]
  (d/pull db '[*] [:coordinate/name name]))

;; sub

(defn sub-coordinate-names [conn]
  @(p/q query-coordinate-names conn))

;; tx

(defn update-position-and-quaternion-tx [db id]
  (let [ac (d/pull db '[*] id)
        position (cal-current-system-position db ac)]
    [{:db/id id
      :object/position position
      :object/quaternion (get-in ac [:astronomical-coordinate/quaternion])}]))
