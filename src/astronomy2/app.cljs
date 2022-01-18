(ns astronomy2.app
  (:require
   [reagent.dom :as rdom]
   [posh.reagent :as p]
   [astronomy2.system :as sys]
   [astronomy2.db :as db]))


(def user-config
  {::sys/pconn {:initial-db (db/create-basic-db)}})

(defonce instance
  (sys/init user-config))

(def entry
  {:db/id [:framework/name "default"]})

(defn app-transact! [tx]
  (let [pconn (::sys/pconn instance)]
    (p/transact! pconn tx)))

(def homies
  (partial sys/system instance))

;; -------------------------
;; Initialize app

(defn mount-root
  []
  (let [view (::sys/view instance)]
    (rdom/render [view :framework/view entry]
                 (js/document.getElementById "app"))))


(defn ^:export init! []
  (mount-root))
