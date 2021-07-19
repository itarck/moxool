(ns astronomy.test-conn
  (:require
   [cljs.core.async :refer [go >! <! chan]]
   [cljs-http.client :as http]
   [datascript.core :as d]
   [datascript.transit :as dt]
   [posh.reagent :as p]
   [methodology.model.core :as mtd-model]
   [astronomy.model.core :as ast-model]
   [astronomy.app.scene-free :as scene-free]
   [astronomy.model.constellation :as m.constel]))


(def schema (merge ast-model/schema
                   mtd-model/schema))


(defn create-empty-conn! []
  (let [conn (d/create-conn schema)]
    (p/posh! conn)
    conn))


(defn create-poshed-conn! []
  (let [conn (d/create-conn)]
    (go
      (let [response (<! (http/get "/edn/free-mode.edn"))
            db (dt/read-transit-str (:body response))]
        (d/reset-conn! conn db)
        (p/posh! conn)))
    conn))


(def create-test-conn! create-poshed-conn!)


(defn create-system-conn! []
  (let [test-app-instance (scene-free/create-app! #:app{:scene-db-url "/edn/free-mode.edn"})]
    (:app/scene-conn test-app-instance)))


(comment

  (def conn (create-poshed-conn!))
  (count (d/datoms @conn :eavt))
  (count (d/schema @conn))

  (def coor-1 (d/pull @conn '[*] [:coordinate/name "default"]))

  @(p/pull conn '[*] [:coordinate/name "default"])
  @(p/pull conn '[*] [:atmosphere/name "default"])

  (m.constel/sub-all-constellations-id-and-names conn)

  @(p/pull conn '[*] [:constellation/abbreviation "UMi"])
  ;; => {:db/id 9234, :constellation/abbreviation "UMi", :constellation/star-lines [[465 6830 6363 5944 5604 5776 6157 5944]]}

  @(p/pull conn '[*] 465)
  ;; => {:star/DEs 51, :star/bsc-name "1Alp UMi", :star/HR 424, :star/DEm 15, :star/right-ascension 30.530194444444444, :star/RAh 2, :db/id 465, :star/declination 89.26416666666667, :star/RAm 31, :star/visual-magnitude 2.02, :star/HD 8890, :star/RAs 48.7, :star/DEd 89}

 

)