(ns astronomy.system.mini2
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit :as circuit]
   [astronomy.conn.schema :refer [schema]]
   [astronomy.parts.root-view]
   [astronomy.parts.listeners :as parts.listeners]
   [pumpnet.core])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))



(derive :astronomy/meta-atom :circuit/ratom)  ;; 记录控制整个系统的atom
(derive :astronomy/dom-atom :circuit/atom)   ;; 记录dom的状态
(derive :astronomy/state-atom :circuit/ratom)   ;; 不同服务间共享一些数据
(derive :astronomy/service-chan :circuit/chan)
(derive :astronomy/conn :circuit/conn)
(derive :astronomy/publisher :pumpnet/publisher)
(derive :astronomy/service.listeners :pumpnet/service.listeners)



(def default-config
  #:astronomy
   {:conn #:conn {:schema schema
                  :db-transit-str (read-resource "private/frame/default.fra")}
    :service-chan #:chan {}
    :meta-atom  #:ratom {:init-value {:mode :read-and-write}}
    :state-atom #:ratom {}
    :dom-atom #:atom {}
    :publisher #:publisher {:pub-fn (fn [event]
                                      (namespace (:event/action event)))
                            :in-chan (ig/ref :astronomy/service-chan)}
    :root-view #:view {:props {:user-name "dr who"
                               :scene-name "solar"}
                       :env {:conn (ig/ref :astronomy/conn)
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
  (let [config (circuit/merge-config
                default-config
                user-config)]
    (ig/init config)))



(comment

  (def user-config
    {:astronomy/conn
     #:conn {:db-transit-str (read-resource "private/frame/solar-0.0.3.fra")}})

  (def system
    (create-system! user-config))

  (:astronomy/listeners system)

  ;; 
  )