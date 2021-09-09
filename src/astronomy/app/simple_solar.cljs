(ns astronomy.app.simple-solar
  (:require
   [astronomy.system.simple-solar :as ss]))


(def app-1
  (ss/create-system! {}))


(comment
  (:system/conn app-1)
  )