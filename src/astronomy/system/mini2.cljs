(ns astronomy.system.mini2
  (:require
   [methodology.lib.circuit :as circuit]
   [astronomy.conn.mini-factory :as mini-factory]
   [astronomy.system.default :as system.default]))



(defn create-user-config []
  {:astronomy/conn #:conn {:initial-db (mini-factory/create-db1)}
   :astronomy/root-view #:view {:init-fn (fn [])
                                :props {:user-name "dr who"
                                        :scene-name "solar"}}
   :astronomy/service-center #:service {:props {:user {:db/id [:user/name "dr who"]}
                                                :astro-scene {:db/id [:scene/name "solar"]}
                                                :camera {:db/id [:camera/name "default"]}
                                                :spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}}})



(defn create-user-config2 []
  {:astronomy/conn #:conn {:db-url "/temp/frame/solar-1.fra"}
   :astronomy/root-view #:view {:init-fn (fn [])
                                :props {:user-name "dr who"
                                        :scene-name "solar"}}
   :astronomy/service-center #:service {:props {:user {:db/id [:user/name "dr who"]}
                                                :astro-scene {:db/id [:scene/name "solar"]}
                                                :camera {:db/id [:camera/name "default"]}
                                                :spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}}})


(def config
  (circuit/merge-config system.default/default-config (create-user-config2)))



(comment 
  config
  )