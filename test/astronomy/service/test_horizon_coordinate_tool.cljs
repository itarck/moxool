(ns astronomy.service.test-horizon-coordinate-tool
  (:require
   [astronomy.scripts.test-conn :refer [create-test-conn!]]
   [astronomy.service.horizontal-coordinate-tool :as s.hct :refer [handle-event!]]))


(def conn (create-test-conn!))

(def props {})

(def env
  {:conn conn})


(let [env {:conn (create-test-conn!)}
      event #:event {:action :horizontal-coordinate-tool/log
                     :detail {:name "testing"}}]
  (handle-event! {} env event))
