(ns laboratory.system
  (:require
   [fancoil.unit :as fu]
   [fancoil.core :as fc]
   [fancoil.module.posh.unit]
   [laboratory.plugin.core]
   [integrant.core :as ig]
   [posh.reagent :as p]))


(def hierarchy
  {::schema [:fancoil.module.posh/schema]
   ::pconn [:fancoil.module.posh/pconn]
   ::spec [::fu/spec]
   ::model [::fu/model]
   ::subscribe [::fu/subscribe]
   ::inject [::fu/inject]
   ::do! [::fu/do!]
   ::handle [::fu/handle]
   ::process [::fu/process]
   ::view [::fu/view]
   ::chan [::fu/chan]
   ::dispatch [::fu/dispatch]
   ::service [::fu/service]})


(def default-config
  {::schema {}
   ::spec {}
   ::model {:spec (ig/ref ::spec)}
   ::pconn {:schema (ig/ref ::schema)}
   ::subscribe {:pconn (ig/ref ::pconn)}
   ::inject {:pconn (ig/ref ::pconn)}
   ::do! {:pconn (ig/ref ::pconn)}
   ::handle {:model (ig/ref ::model)
             :spec (ig/ref ::spec)}
   ::process {:inject (ig/ref ::inject)
              :do! (ig/ref ::do!)
              :handle (ig/ref ::handle)}
   ::view {:dispatch (ig/ref ::dispatch)
           :subscribe (ig/ref ::subscribe)}
   ::chan {}
   ::dispatch {:out-chan (ig/ref ::chan)}
   ::service {:process (ig/ref ::process)
              :in-chan (ig/ref ::chan)}})


(fc/load-hierarchy hierarchy)


(defn init
  ([user-config]
   (let [config (fc/merge-config default-config user-config)]
     (ig/init config)))
  ([user-config unit-keys]
   (let [config (fc/merge-config default-config user-config)]
     (ig/init config unit-keys))))


;; homies version of system

(defmulti system
  (fn [core method & args]
    method))

(defmethod system :transact!
  [core _ tx]
  (let [pconn (::pconn core)]
    (p/transact! pconn tx)))
