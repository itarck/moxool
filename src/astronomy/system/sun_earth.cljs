(ns astronomy.system.sun-earth
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.conn.db-factory :as db-factory]
   [astronomy.ig.conn]
   [astronomy.ig.root-view]
   [astronomy.ig.service-center]))


(derive :astronomy/dom-atom :circuit/atom)
(derive :astronomy/state-atom :circuit/ratom)
(derive :astronomy/service-chan :circuit/chan)


;; (def real-db @(init-conn2/init-conn!))

(def db (db-factory/create-test-db10))

(def config 
  #:astronomy{:dom-atom #:atom {}
              :state-atom #:ratom {}
              :service-chan #:chan {}
              :conn #:conn {
                            ;; :db-url "/temp/frame/solar-1.fra"
                            :initial-db db}
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
  (ig/init config))

