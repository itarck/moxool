(ns laboratory.app.playground
  (:require
   [fancoil.base :as base]
   [reagent.dom :as rdom]
   [laboratory.system.zero :as zero]
   [cljs.spec.alpha :as s]))


(def initial-tx
  [(base/model {} :framework/create {})
   (base/model {} :scene/create {:scene/background "black"})
   (base/model {} :user/create {})
   (base/model {} :backpack/create {})
   (base/model {} :object/create #:object{:scale [1 1 5]})
   (base/model {} :object/create #:object{:position [3 0 0]
                                          :scale [3 3 3]})])


(def user-config 
  {::zero/pconn {:initial-tx initial-tx}})

(defonce instance
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


(comment
  (def model
    (partial base/model {}))

  (model :framework/create
         #:framework {:db/id -1
                      :scene -2
                      :user -3}))


(comment

  (s/valid? :db/id 324)
  )
