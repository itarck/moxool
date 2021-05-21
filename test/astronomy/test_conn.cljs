(ns astronomy.test-conn
  (:require
   [cljs.core.async :refer [go >! <! chan]]
   [cljs-http.client :as http]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [shu.three.vector3 :as v3]
   [astronomy.model.coordinate :as m.coordinate]))



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
  
  (def coor-1 (d/pull @conn '[*] [:coordinate/name "default"]))

  (let [m (m.coordinate/cal-invert-matrix coor-1)
        v (v3/vector3 0 0 0)]
    (v3/apply-matrix4 v m))

  ;; 
  )
