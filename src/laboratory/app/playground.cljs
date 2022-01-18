(ns laboratory.app.playground
  (:require
   [fancoil.base :as base]
   [reagent.dom :as rdom]
   [laboratory.system :as sys]
   [cljs.spec.alpha :as s]
   [fancoil.unit :as fu]
   [laboratory.dbs.dev :as dev]))


#_(def dev-db 
  (dev/create-dev-db1))

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




(comment
  (def model
    (partial base/model {}))

  (model :framework/create
         #:framework {:db/id -1
                      :scene -2
                      :user -3}))


(comment

  (s/valid? :db/id 324)

  (s/check-asserts true)

  (let [spec (::fu/spec instance)]
    (spec :assert :entity/entity {:db/id [:scene/name "34"]}))

  )
