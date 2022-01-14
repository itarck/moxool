(ns laboratory.test-helper
  (:require
   [fancoil.unit :as fu]
   [laboratory.system.zero :as zero]))


(defn create-spec-unit []
  (let [sys (zero/init {} [::fu/spec])]
    (::fu/spec sys)))

(defn create-model-unit []
  (let [sys (zero/init {} [::fu/model])]
    (::fu/model sys)))

(defn create-handle-unit []
  (let [sys (zero/init {} [::fu/handle])]
    (::fu/handle sys)))

(defn create-event-system [{:keys [initial-db]}]
  (let [sys (zero/init {::zero/pconn {:initial-db initial-db}}
                       [::fu/process
                        ::fu/subscribe])]
    sys))


(comment

  (create-spec-unit)

  (create-model-unit)

  (keys (create-event-system))
  ;; => (:laboratory.system.zero/schema :laboratory.system.zero/pconn :fancoil.unit/do! :fancoil.unit/model :fancoil.unit/handle :fancoil.unit/inject :fancoil.unit/process :fancoil.unit/subscribe)

  (keys (zero/init {} [::fu/handle]))
  ;; => (:fancoil.unit/spec :fancoil.unit/model :fancoil.unit/handle)

  )