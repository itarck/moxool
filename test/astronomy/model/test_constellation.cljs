(ns astronomy.model.test-constellation
  (:require
   [astronomy.test-conn :refer [create-poshed-conn!]]
   [astronomy.model.constellation :as m.constel]))


(def conn (create-poshed-conn!))


(def ids (m.constel/sub-all-constellation-ids conn))


(m.constel/sub-constellation conn (first ids))




