(ns astronomy.system.mini2
  (:require
   [methodology.lib.circuit :as circuit]
   [astronomy.system.default :as system.default])
  (:require-macros
   [methodology.lib.resource :refer [read-resource]]))


(def user-config
  {:astronomy/conn
   #:conn {:db-transit-str (read-resource "private/frame/solar-0.0.3.fra")}

   :astronomy/root-view
   #:view {:init-fn (fn [])
           :props {:user-name "dr who"
                   :scene-name "solar"}}

   :astronomy/service-center
   #:service {:props {:user {:db/id [:user/name "dr who"]}
                      :astro-scene {:db/id [:scene/name "solar"]}
                      :camera {:db/id [:camera/name "default"]}
                      :spaceship-camera-control {:db/id [:spaceship-camera-control/name "default"]}}}})



(def config
  (circuit/merge-config
   system.default/default-config
   user-config))



(comment 
  config
  )