(ns astronomy.app.test-solar
  (:require
   [astronomy.app.solar :as solar]))


(def conn
  (:astronomy/conn solar/system))

