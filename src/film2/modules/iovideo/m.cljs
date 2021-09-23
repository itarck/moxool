(ns film2.modules.iovideo.m
  (:require
   [datascript.core :as d]
   [shu.calendar.timestamp :as timestamp]
   [posh.reagent :as p]
   [astronomy.system.slider :as slider]))


(def iovideo-sample
  #:iovideo {:name "slider move"
             :start-timestamp 124234
             :stop-timestamp 534543
             :total-time 3000
             :initial-ioframe #:ioframe {:db slider/db
                                         :type :slider
                                         :name "slider-1"
                                         :description "一个进度条"}
             :tx-logs [{:relative-time 1000
                        :tx-data [#:slider{:name "bmi"
                                           :value 80}]}
                       {:relative-time 2000
                        :tx-data [#:slider{:name "bmi"
                                           :value 20}]}]})

(def schema {:iovideo/name {:db/unique :db.unique/identity}
             :iovideo/initial-ioframe {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; model

(def all-iovideo-ids-query
  '[:find [?vid ...]
    :where
    [?vid :iovideo/name]])

(def all-id-and-names-query
  '[:find ?id ?name
    :where
    [?id :iovideo/name ?name]])

(defn pull-one [db id]
  (d/pull db '[*] id))


(defn parse-datoms [datoms]
  (mapv (fn [datom] (vec (concat [:db/add] datom))) datoms))

(defn append-tx-log [iovideo tx-data]
  (let [timestamp (timestamp/current-timestamp!)
        new-tx-log {:relative-time (- timestamp (:iovideo/start-timestamp iovideo))
                    :tx-data (parse-datoms tx-data)}]
    (-> iovideo
        (assoc :iovideo/total-time (- timestamp (:iovideo/start-timestamp iovideo)))
        (update :iovideo/tx-logs (fn [tx-logs] (vec (conj tx-logs new-tx-log)))))))

(defn update-stop-timestamp [iovideo]
  (let [stop-timestamp (timestamp/current-timestamp!)
        start-timestamp (:iovideo/start-timestamp iovideo)
        total-time (- stop-timestamp start-timestamp)]
    #:iovideo {:db/id (:db/id iovideo)
               :stop-timestamp stop-timestamp
               :total-time total-time}))


(defn get-tx-logs-in-range [iovideo time1 time2]
  (->> (:iovideo/tx-logs iovideo)
       (filter (fn [tx-log] (and (>= (:relative-time tx-log) time1)
                                 (< (:relative-time tx-log) time2))))
       (map :tx-data)
       (apply concat)
       vec))


;; sub

(defn sub-entity [system-conn id]
  @(p/pull system-conn '[*] id))


;; process

(defn iovideo-append-tx-log! [system-conn iovideo-id tx-data]
  (let [iovideo @(p/pull system-conn '[*] iovideo-id)
        new-iovideo (append-tx-log iovideo tx-data)]
    (p/transact! system-conn [new-iovideo])))

