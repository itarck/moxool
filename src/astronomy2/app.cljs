(ns astronomy2.app
  (:require
   [reagent.dom :as rdom]
   [astronomy2.system :as sys]
   [astronomy2.db :as db]))


(def initial-db
  (db/create-db))

(def user-config
  {::sys/pconn {:initial-db initial-db}})

(defonce instance
  (sys/init user-config))

(def entry
  {:db/id [:framework/name "default"]})

;; -------------------------
;; Initialize app

(defn mount-root
  []
  (let [view (::sys/view instance)]
    (rdom/render [view :framework/view entry]
                 (js/document.getElementById "app"))))


(defn ^:export init! []
  (mount-root))
