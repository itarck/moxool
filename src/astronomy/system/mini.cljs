(ns astronomy.system.mini
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.ig.conn]
   [astronomy.ig.root-view]
   [astronomy.ig.service-center]
   [astronomy.conn.mini-factory :as mini-factory]
   ))

;; 只有太阳、地球、月球的一个系统

(derive :astronomy/dom-atom :circuit/atom)
(derive :astronomy/state-atom :circuit/ratom)
(derive :astronomy/service-chan :circuit/chan)


(def default-db (mini-factory/create-db1))

(def default-config
  #:astronomy{:conn #:conn {:initial-db default-db}
              :service-chan #:chan {}
              :state-atom #:ratom {}
              :dom-atom #:atom {}
              :root-view #:view {:props {:user-name "dr who"
                                         :scene-name "solar"}
                                 :env {:conn (ig/ref :astronomy/conn)
                                       :service-chan (ig/ref :astronomy/service-chan)
                                       :state-atom (ig/ref :astronomy/state-atom)
                                       :dom-atom (ig/ref :astronomy/dom-atom)}}
              :service-center #:service {:props {:user {:db/id [:user/name "dr who"]}
                                                 :astro-scene {:db/id [:scene/name "solar"]}
                                                 :camera {:db/id [:camera/name "default"]}
                                                 :spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}
                                         :env {:conn (ig/ref :astronomy/conn)
                                               :service-chan (ig/ref :astronomy/service-chan)
                                               :state-atom (ig/ref :astronomy/state-atom)
                                               :dom-atom (ig/ref :astronomy/dom-atom)}}})

#_(def app
  (ig/init default-config
           #_[:astronomy/dom-atom
              :astronomy/state-atom
              :astronomy/service-chan
              :astronomy/conn
              :astronomy/root-view]))


(def ioframe-config-sample
  #:ioframe {:db default-db
             :name "mini-1"
             :type "mini"
             :description "只有太阳和地球的小型系统"})


(defn create-ioframe-system [ioframe-config]
  (let [{:ioframe/keys [db]} ioframe-config
        merged-config (merge default-config #:astronomy {:conn #:conn {:initial-db db}})
        astronomy-instance (ig/init merged-config)]
    #:ioframe-system {:view (:astronomy/root-view astronomy-instance)}))


(comment 
  
  (def ioframe-system (create-ioframe-system ioframe-config-sample))

  (keys ioframe-system)
  
  )