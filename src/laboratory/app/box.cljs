(ns laboratory.app.box
  (:require
   [reagent.dom :as rdom]
   [fancoil.unit :as fu]
   [fancoil.core :as fc]
   [integrant.core :as ig]
   [fancoil.module.posh.unit]
   [laboratory.parts.core]))


(def initial-tx
  [{:framework/name "default"
    :framework/scene -1}
   {:db/id -1
    :scene/name "default"
    :scene/background "white"}
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


(def hierarchy
  {::schema [:fancoil.module.posh/schema] 
   ::pconn [:fancoil.module.posh/pconn]})


(def config
  {::schema {}
   ::pconn {:schema (ig/ref ::schema)
            :initial-tx initial-tx}
   ::fu/subscribe {:pconn (ig/ref ::pconn)}
   ::fu/inject {:pconn (ig/ref ::pconn)}
   ::fu/do! {:pconn (ig/ref ::pconn)}
   ::fu/handle {}
   ::fu/process {:inject (ig/ref ::fu/inject)
                 :do! (ig/ref ::fu/do!)
                 :handle (ig/ref ::fu/handle)}
   ::fu/view {:dispatch (ig/ref ::fu/dispatch)
              :subscribe (ig/ref ::fu/subscribe)}
   ::fu/chan {}
   ::fu/dispatch {:out-chan (ig/ref ::fu/chan)}
   ::fu/service {:process (ig/ref ::fu/process)
                 :in-chan (ig/ref ::fu/chan)}})


(def system
  (let [_ (fc/load-hierarchy hierarchy)]
    (ig/init config)))



;; -------------------------
;; Initialize app


(defn mount-root
  []
  (let [view (::fu/view system)]
    (rdom/render [view :framework/view {:db/id [:framework/name "default"]}]
                 (js/document.getElementById "app")))
  )


(defn ^:export init! []
  (mount-root))
