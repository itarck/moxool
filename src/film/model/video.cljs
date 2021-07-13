(ns film.model.video
  (:require
   [applied-science.js-interop :as j]
   [datascript.core :as d]
   [shu.calendar.timestamp :as timestamp]
   [posh.reagent :as p]))


(def video-sample 
  #:video {:db/id -201
           :scene -101
           :name "camera move"
           :start-timestamp 124234
           :stop-timestamp 534543
           :total-time 3000
           :initial-db nil
           :tx-logs [{:relative-time 1000
                      :tx-data [#:camera{:name "default"
                                         :position [1000 900 1000]
                                         :rotation [0 0 0]}]}
                     {:relative-time 2000
                      :tx-data [#:camera{:name "default"
                                         :position [1000 2000 1000]
                                         :rotation [0 0 0]}]}]})

(def schema {:video/name {:db/unique :db.unique/identity}
             :video/scene {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})


;; model

(def find-all-video-ids-query
  '[:find [?vid ...]
    :where
    [?vid :video/name]])

(defn pull-one [db id]
  (d/pull db '[*] id))


(defn parse-datoms [datoms]
  (mapv (fn [datom] (vec (concat [:db/add] datom))) datoms))

(defn append-tx-log [video tx-data]
  (let [timestamp (timestamp/current-timestamp!)
        new-tx-log {:relative-time (- timestamp (:video/start-timestamp video))
                    :tx-data (parse-datoms tx-data)}]
    (-> video
        (assoc :video/total-time (- timestamp (:video/start-timestamp video)))
        (update :video/tx-logs (fn [tx-logs] (vec (conj tx-logs new-tx-log)))))))

(defn update-stop-timestamp [video]
  (let [stop-timestamp (timestamp/current-timestamp!)
        start-timestamp (:video/start-timestamp video)
        total-time (- stop-timestamp start-timestamp)]
    #:video {:db/id (:db/id video)
             :stop-timestamp stop-timestamp
             :total-time total-time}))


(defn get-tx-logs-in-range [video time1 time2]
  (->> (:video/tx-logs video)
       (filter (fn [tx-log] (and (>= (:relative-time tx-log) time1)
                                 (< (:relative-time tx-log) time2))))
       (map :tx-data)
       (apply concat)
       vec))


;; sub

(defn sub-entity [system-conn id]
  @(p/pull system-conn '[*] id))


;; process

(defn video-append-tx-log! [system-conn video-id tx-data]
  (let [video @(p/pull system-conn '[*] video-id)
        new-video (append-tx-log video tx-data)]
    (p/transact! system-conn [new-video])))

