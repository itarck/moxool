(ns laboratory.test-helper
  (:require
   [fancoil.unit :as fu]
   [datascript.core :as d]
   [laboratory.system :as sys]))

(defn create-schema-unit []
  (let [sys (sys/init {} [::sys/schema])]
    (::sys/schema sys)))

(defn create-spec-unit []
  (let [sys (sys/init {} [::sys/spec])]
    (::sys/spec sys)))

(defn create-model-unit []
  (let [sys (sys/init {} [::sys/model])]
    (::sys/model sys)))

(defn create-handle-unit
  ([]
   (let [sys (sys/init {} [::sys/handle])]
     (::sys/handle sys)))
  ([initial-db]
   (let [sys (sys/init {::sys/pconn {:initial-db initial-db}}
                        [::sys/handle])]
     (::sys/handle sys))))

(defn create-initial-db
  [initial-tx]
  (let [schema (create-schema-unit)
        conn (d/create-conn schema)]
    (d/transact! conn initial-tx)
    @conn))

(defn create-event-system
  [initial-db]
  (let [sys (sys/init {::sys/pconn {:initial-db initial-db}}
                       [::sys/process
                        ::sys/subscribe])]
    sys))

(defn create-whole-system
  [initial-db]
  (sys/init {::sys/pconn {:initial-db initial-db}}))

(comment

  (create-spec-unit)

  (create-model-unit)

  (def db 
    (create-initial-db [{:framework{:name "default"}}]))

  (keys (create-event-system db))
  ;; => (:laboratory.system.sys/schema :laboratory.system.sys/pconn :fancoil.unit/do! :fancoil.unit/model :fancoil.unit/handle :fancoil.unit/inject :fancoil.unit/process :fancoil.unit/subscribe)

  (keys (sys/init {} [::fu/handle]))
  ;; => (:fancoil.unit/spec :fancoil.unit/model :fancoil.unit/handle)

  )