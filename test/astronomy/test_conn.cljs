(ns astronomy.test-conn
  (:require
   [cljs.core.async :refer [go >! <! chan]]
   [cljs-http.client :as http]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]))


(def db nil)

(go
  (let [response (<! (http/get "/edn/free-mode.edn"))]
    (set! db (dt/read-transit-str (:body response)))))


(defn create-poshed-conn! []
  (let [conn (d/create-conn)]
    (d/reset-conn! conn db)
    (p/posh! conn)
    conn))
