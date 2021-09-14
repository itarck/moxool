(ns astronomy.system.mini
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.ig.conn]
   [astronomy.ig.root-view]
   [astronomy.ig.service-center]
   ))


(derive :astronomy/dom-atom :circuit/atom)
(derive :astronomy/state-atom :circuit/ratom)
(derive :astronomy/service-chan :circuit/chan)


;; (def db (db-factory/create-test-db10))

(def config
  #:astronomy{:dom-atom #:atom {}
              :state-atom #:ratom {}
              :service-chan #:chan {}
              :conn #:conn {:db-url "/temp/frame/solar-1.fra"
                            ;; :initial-db db
                            }
              :root-view #:view {:props {:user-name "dr who"
                                         :scene-name "solar"}
                                 :env {:conn (ig/ref :astronomy/conn)
                                       :service-chan (ig/ref :astronomy/service-chan)
                                       :dom-atom (ig/ref :astronomy/dom-atom)
                                       :state-atom (ig/ref :astronomy/state-atom)}}
              :service-center #:service {:props {:user {:db/id [:person/name "dr who"]}
                                                 :astro-scene {:db/id [:scene/name "solar"]}
                                                 :camera {:db/id [:camera/name "default"]}
                                                 :spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}
                                         :env {:service-chan (ig/ref :astronomy/service-chan)
                                               :conn (ig/ref :astronomy/conn)
                                               :state-atom (ig/ref :astronomy/state-atom)
                                               :dom-atom (ig/ref :astronomy/dom-atom)}}})

(def app
  (ig/init config
           #_[:astronomy/dom-atom
              :astronomy/state-atom
              :astronomy/service-chan
              :astronomy/conn
              :astronomy/root-view]))

