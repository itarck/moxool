(ns laboratory.app.playground
  (:require
   [fancoil.base :as base]
   [reagent.dom :as rdom]
   [laboratory.system.zero :as zero]
   [cljs.spec.alpha :as s]
   [fancoil.unit :as fu]
   [laboratory.dbs.dev :as dev]
   [laboratory.dbs.helper :as helper]))


#_(def dev-db 
  (dev/create-dev-db1))

(def dev-db
  (helper/create-initial-db
   [{:framework/name "default"
     :framework/scene {:scene/name "default"}}
    {:gltf/url "models/3-cityscene_kyoto_1995/scene.gltf"
     :object/type :gltf
     :object/scale [0.001 0.001 0.001]
     :object/scene [:scene/name "default"]}]))

(def user-config 
  {::zero/pconn {:initial-db dev-db}})

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

  (s/check-asserts true)

  (let [spec (::fu/spec instance)]
    (spec :assert :entity/entity {:db/id [:scene/name "34"]}))

  )
