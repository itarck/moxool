(ns laboratory.system.zero
  (:require
   [fancoil.unit :as fu]
   [fancoil.core :as fc]
   [integrant.core :as ig]
   [fancoil.module.posh.unit]
   [laboratory.parts.core]
   [laboratory.unit.process]))


(def hierarchy
  {::schema [:fancoil.module.posh/schema]
   ::pconn [:fancoil.module.posh/pconn]
   ::view [::fu/view]})


(def default-config
  {::schema {}
   ::fu/spec {}
   ::fu/model {:spec (ig/ref ::fu/spec)}
   ::pconn {:schema (ig/ref ::schema)}
   ::fu/subscribe {:pconn (ig/ref ::pconn)}
   ::fu/inject {:pconn (ig/ref ::pconn)}
   ::fu/do! {:pconn (ig/ref ::pconn)}
   ::fu/handle {:model (ig/ref ::fu/model)}
   ::fu/process {:inject (ig/ref ::fu/inject)
                 :do! (ig/ref ::fu/do!)
                 :handle (ig/ref ::fu/handle)}
   ::view {:dispatch (ig/ref ::fu/dispatch)
           :subscribe (ig/ref ::fu/subscribe)}
   ::fu/chan {}
   ::fu/dispatch {:out-chan (ig/ref ::fu/chan)}
   ::fu/service {:process (ig/ref ::fu/process)
                 :in-chan (ig/ref ::fu/chan)}})


(fc/load-hierarchy hierarchy)


(defn init
  ([user-config]
   (let [config (fc/merge-config default-config user-config)]
     (ig/init config)))
  ([user-config unit-keys]
   (let [config (fc/merge-config default-config user-config)]
     (ig/init config unit-keys))))


