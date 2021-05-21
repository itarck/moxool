(ns astronomy.test-conn
  (:require
   [cljs.core.async :refer [go >! <! chan]]
   [cljs-http.client :as http]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]))



(defn create-poshed-conn! []
  (let [conn (d/create-conn)]
    (go
      (let [response (<! (http/get "/edn/free-mode.edn"))
            db (dt/read-transit-str (:body response))]
        (d/reset-conn! conn db)
        (p/posh! conn)))
    conn))


(def create-test-conn! create-poshed-conn!)


(comment
  (def conn (create-poshed-conn!))

  (count (d/datoms @conn :eavt))

  (d/pull @conn '[*] [:backpack/name "default"])
  
  ;; 
  )
