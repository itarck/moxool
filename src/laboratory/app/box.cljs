(ns laboratory.app.box
  (:require
   [reagent.dom :as rdom]
   [laboratory.system.zero :as zero]))


(def initial-tx
  [{:framework/name "default"
    :framework/scene -1
    :framework/user -2}
   {:db/id -1
    :scene/name "default"
    :scene/background "white"}
   {:db/id -2
    :user/name "default"}
   {:object/type :box
    :object/position [0 0 0]
    :object/rotation [0 0 0]
    :object/scale [1 1 5]
    :object/scene [:scene/name "default"]}
   {:object/type :box
    :object/position [3 0 0]
    :object/rotation [0 0 0]
    :object/scale [3 2 5]
    :object/scene [:scene/name "default"]}])


(def user-config 
  {::zero/pconn {:initial-tx initial-tx}})

(def instance
  (zero/init user-config))

(def entry
  {:db/id [:framework/name "default"]})

;; -------------------------
;; Initialize app

(defn mount-root
  []
  (let [view (::zero/view instance)]
    (rdom/render [view :framework/view entry]
                 (js/document.getElementById "app"))))


(defn ^:export init! []
  (mount-root))
