(ns astronomy.app.simple-solar
  (:require
   [astronomy.system.simple-solar :as ss]
   [astronomy.scripts.test-conn :as test-conn]))


(def config
  {:initial-db test-conn/real-db})


(def app-1
  (ss/create-system! config))


(comment
  
  (:system/conn app-1)
  )