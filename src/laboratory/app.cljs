(ns laboratory.app
  (:require
   [reagent.dom :as rdom]
   [laboratory.system :as sys]))


(def initial-tx
  [{:framework/name "default"
    :framework/scene {:scene/name "default"}}])

(def user-config
  {::sys/pconn {:initial-tx initial-tx}})

(defonce instance
  (sys/init user-config))

(def entry
  {:db/id [:framework/name "default"]})

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


