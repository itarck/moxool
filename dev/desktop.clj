(ns desktop
  (:require
   [ring.middleware.file :refer [wrap-file]]))


(def app
  (wrap-file identity "desktop"))
