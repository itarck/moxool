(ns astronomy.system.mini2
  (:require
   [integrant.core :as ig]
   [astronomy.conn.schema :refer [schema]]
   [astronomy.parts.root-view :as parts.root-view]
   [astronomy.parts.listeners :as parts.listeners]
   [fancoil.core :as pp])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


(def hierarchy
  {:astronomy/meta-atom :fancoil/db.ratom
   :astronomy/dom-atom :fancoil/db.atom
   :astronomy/state-atom :fancoil/db.ratom
   :astronomy/service-chan :fancoil/async.chan
   :astronomy/conn :fancoil/db.pconn
   :astronomy/publisher :fancoil/async.publisher
   :astronomy/root-view :fancoil/view.reagent-view
   :astronomy/service.listeners :fancoil/service.listeners})


(def default-config
  #:astronomy
   {:conn #:db.pconn {:schema schema
                      :db-transit-str (read-resource "private/frame/default.fra")}
    :service-chan #:async.chan {}
    :meta-atom  #:db.ratom {:initial-value {:mode :read-and-write}}
    :state-atom #:db.ratom {}
    :dom-atom #:db.atom {}
    :publisher #:async.publisher {:pub-fn (fn [event]
                                            (namespace (:event/action event)))
                                  :in-chan (ig/ref :astronomy/service-chan)}

    :root-view
    #:view.reagent-view {:view-fn parts.root-view/RootView
                         :props {:user-name "dr who"
                                 :scene-name "solar"}
                         :env {:object-libray parts.root-view/object-libray
                               :tool-library parts.root-view/tool-library
                               :hud-library parts.root-view/hud-library
                               :conn (ig/ref :astronomy/conn)
                               :service-chan (ig/ref :astronomy/service-chan)
                               :meta-atom (ig/ref :astronomy/meta-atom)
                               :state-atom (ig/ref :astronomy/state-atom)
                               :dom-atom (ig/ref :astronomy/dom-atom)}}

    :service.listeners
    #:service.listeners {:init-fn parts.listeners/init-service-center!
                         :publication (ig/ref :astronomy/publisher)
                         :listeners parts.listeners/listeners
                         :props {:user {:db/id [:user/name "dr who"]}
                                 :astro-scene {:db/id [:scene/name "solar"]}
                                 :camera {:db/id [:camera/name "default"]}
                                 :spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}
                         :env {:conn (ig/ref :astronomy/conn)
                               :service-chan (ig/ref :astronomy/service-chan)
                               :meta-atom (ig/ref :astronomy/meta-atom)
                               :state-atom (ig/ref :astronomy/state-atom)
                               :dom-atom (ig/ref :astronomy/dom-atom)}}})


(defn create-system! [user-config]
  (pp/load-hierarchy! hierarchy)
  (let [config (pp/merge-config default-config user-config)]
    (ig/init config)))



(comment

  (def user-config
    {:astronomy/conn
     #:pconn {:db-transit-str (read-resource "private/frame/solar-0.0.3.fra")}})

  (def system
    (create-system! user-config))

  (:astronomy/listeners system)

  ;; 
  )