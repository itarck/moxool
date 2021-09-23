(ns astronomy.system.mini
  (:require
   [datascript.transit :as dt]
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [astronomy.ig.conn]
   [astronomy.ig.root-view]
   [astronomy.ig.service-center]
   [astronomy.conn.mini-factory :as mini-factory]))

;; 只有太阳、地球、月球的一个系统

(derive :astronomy/meta-atom :circuit/atom)  ;; 记录控制整个系统的atom
(derive :astronomy/dom-atom :circuit/atom)   ;; 记录dom的状态
(derive :astronomy/state-atom :circuit/ratom)   ;; 不同服务间共享一些数据
(derive :astronomy/service-chan :circuit/chan)


(def default-db (mini-factory/create-db1))

(def default-config
  #:astronomy{:conn #:conn {:initial-db default-db}
              :service-chan #:chan {}
              :meta-atom  #:atom {:init-value {:mode :read-and-write}}
              :state-atom #:ratom {}
              :dom-atom #:atom {}
              :root-view #:view {:props {:user-name "dr who"
                                         :scene-name "solar"}
                                 :env {:conn (ig/ref :astronomy/conn)
                                       :service-chan (ig/ref :astronomy/service-chan)
                                       :meta-atom (ig/ref :astronomy/meta-atom)
                                       :state-atom (ig/ref :astronomy/state-atom)
                                       :dom-atom (ig/ref :astronomy/dom-atom)}}
              :service-center #:service {:props {:user {:db/id [:user/name "dr who"]}
                                                 :astro-scene {:db/id [:scene/name "solar"]}
                                                 :camera {:db/id [:camera/name "default"]}
                                                 :spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}
                                         :env {:conn (ig/ref :astronomy/conn)
                                               :service-chan (ig/ref :astronomy/service-chan)
                                               :meta-atom (ig/ref :astronomy/meta-atom)
                                               :state-atom (ig/ref :astronomy/state-atom)
                                               :dom-atom (ig/ref :astronomy/dom-atom)}}})



(def ioframe-config-sample
  #:ioframe {:db default-db
             :name "mini-1"
             :type :mini
             :description "只有太阳和地球的小型系统"})


(defn create-ioframe-system [ioframe-config]
  (let [{:ioframe/keys [db db-url db-transit-str]} ioframe-config
        conn-config (cond
                      db-transit-str #:conn {:db-transit-str db-transit-str}
                      db-url #:conn {:db-url db-url}
                      db #:conn {:initial-db db}
                      :else #:conn {:initial-db default-db})
        merged-config (merge default-config #:astronomy {:conn conn-config})
        astronomy-instance (ig/init merged-config)]
    #:ioframe-system {:view (:astronomy/root-view astronomy-instance)
                      :conn (:astronomy/conn astronomy-instance)
                      :meta-atom (:astronomy/meta-atom astronomy-instance)}))


(comment 
  
  (def ioframe-system (create-ioframe-system ioframe-config-sample))

  (keys ioframe-system)
  
  )