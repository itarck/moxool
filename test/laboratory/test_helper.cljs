(ns laboratory.test-helper
  (:require
   [fancoil.unit :as fu]
   [datascript.core :as d]
   [laboratory.system.zero :as zero]))

(defn create-schema-unit []
  (let [sys (zero/init {} [::zero/schema])]
    (::zero/schema sys)))

(defn create-spec-unit []
  (let [sys (zero/init {} [::zero/spec])]
    (::zero/spec sys)))

(defn create-model-unit []
  (let [sys (zero/init {} [::zero/model])]
    (::zero/model sys)))

(defn create-handle-unit []
  (let [sys (zero/init {} [::zero/handle])]
    (::zero/handle sys)))

(defn create-initial-db
  [initial-tx]
  (let [schema (create-schema-unit)
        conn (d/create-conn schema)]
    (d/transact! conn initial-tx)
    @conn))

(defn create-event-system
  [initial-db]
  (let [sys (zero/init {::zero/pconn {:initial-db initial-db}}
                       [::zero/process
                        ::zero/subscribe])]
    sys))

(defn create-whole-system
  [initial-db]
  (zero/init {::zero/pconn {:initial-db initial-db}}))

(comment

  (create-spec-unit)

  (create-model-unit)

  (def db 
    (create-initial-db [{:framework{:name "default"}}]))

  (keys (create-event-system db))
  ;; => (:laboratory.system.zero/schema :laboratory.system.zero/pconn :fancoil.unit/do! :fancoil.unit/model :fancoil.unit/handle :fancoil.unit/inject :fancoil.unit/process :fancoil.unit/subscribe)

  (keys (zero/init {} [::fu/handle]))
  ;; => (:fancoil.unit/spec :fancoil.unit/model :fancoil.unit/handle)

  )