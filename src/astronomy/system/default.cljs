(ns astronomy.system.default
  (:require
   [integrant.core :as ig]
   [methodology.lib.circuit :as circuit]
   [astronomy.ig.conn]
   [astronomy.ig.root-view]
   [astronomy.ig.service-center]
   [astronomy.conn.mini-factory :as mini-factory]))

;; 只有太阳、地球、月球的一个系统

(derive :astronomy/meta-atom :circuit/ratom)  ;; 记录控制整个系统的atom
(derive :astronomy/dom-atom :circuit/atom)   ;; 记录dom的状态
(derive :astronomy/state-atom :circuit/ratom)   ;; 不同服务间共享一些数据
(derive :astronomy/service-chan :circuit/chan)


(def default-config
  #:astronomy{:conn #:conn {:initial-db (mini-factory/create-db1)}
              :service-chan #:chan {}
              :meta-atom  #:ratom {:init-value {:mode :read-and-write}}
              :state-atom #:ratom {}
              :dom-atom #:atom {}
              :root-view #:view {:props {}
                                 :env {:conn (ig/ref :astronomy/conn)
                                       :service-chan (ig/ref :astronomy/service-chan)
                                       :meta-atom (ig/ref :astronomy/meta-atom)
                                       :state-atom (ig/ref :astronomy/state-atom)
                                       :dom-atom (ig/ref :astronomy/dom-atom)}}
              :service-center #:service {:props {}
                                         :env {:conn (ig/ref :astronomy/conn)
                                               :service-chan (ig/ref :astronomy/service-chan)
                                               :meta-atom (ig/ref :astronomy/meta-atom)
                                               :state-atom (ig/ref :astronomy/state-atom)
                                               :dom-atom (ig/ref :astronomy/dom-atom)}}})

