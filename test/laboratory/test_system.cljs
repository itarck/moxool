(ns laboratory.test-system
  (:require
   [fancoil.unit :as fu]
   [laboratory.system.zero :as zero]))


(defn create-spec-unit []
  (let [sys (zero/init {} [::fu/spec])]
    (::fu/spec sys)))

(defn create-model-unit []
  (let [sys (zero/init {} [::fu/model])]
    (::fu/model sys)))

(defn create-db-system []
  (let [sys (zero/init {}
                       [::fu/process
                        ::fu/subscribe])]
    sys))


(comment

  (create-spec-unit)

  (create-model-unit)

  (keys (create-db-system))
  ;; => (:laboratory.system.zero/schema :laboratory.system.zero/pconn :fancoil.unit/do! :fancoil.unit/model :fancoil.unit/handle :fancoil.unit/inject :fancoil.unit/process :fancoil.unit/subscribe)


  )