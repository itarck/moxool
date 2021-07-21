(ns astronomy.service.test-horizon-coordinate-tool
  (:require
   [posh.reagent :as p]
   [astronomy.scripts.test-conn :refer [create-test-conn!]]
   [astronomy.service.horizon-coordinate-tool :as s.horizon-coordinate-tool]
   [astronomy.app.core :refer [free-app-instance]]))


(def conn (create-test-conn!))

(def props {})

(def env
  {:conn conn})


(let [env {:conn (create-test-conn!)}
      event #:event {:action :horizon-coordinate/log
                     :detail {:name "testing"}}]
  (s.horizon-coordinate-tool/handle-event! {} env event))


(let [conn (create-test-conn!)
      env {:conn conn}
      event #:event {:action :horizon-coordinate/change-show-latitude
                     :detail {:horizon-coordinate {:db/id [:coordinate/name "地平坐标系"]}
                              :show? false}}]
  (s.horizon-coordinate-tool/handle-event! {} env event)
  (let [hc @(p/pull conn '[*] [:coordinate/name "地平坐标系"])]
    (:horizon-coordinate/show-latitude? hc)))


;; 闭环测试

(def real-conn (:app/scene-conn free-app-instance))

(def real-env {:conn real-conn})

(def show-latitude-event
  #:event {:action :horizon-coordinate/change-show-latitude
           :detail {:horizon-coordinate {:db/id [:coordinate/name "地平坐标系"]}
                    :show? true}})

(def show-longitude-event
  #:event {:action :horizon-coordinate/change-show-longitude
           :detail {:horizon-coordinate {:db/id [:coordinate/name "地平坐标系"]}
                    :show? false}})

(let [_ (s.horizon-coordinate-tool/handle-event! {} real-env show-latitude-event)
      hc @(p/pull real-conn '[*] [:coordinate/name "地平坐标系"])]
  (:horizon-coordinate/show-latitude? hc))

(let [_ (s.horizon-coordinate-tool/handle-event! {} real-env show-longitude-event)
      hc @(p/pull real-conn '[*] [:coordinate/name "地平坐标系"])]
  (:horizon-coordinate/show-longitude? hc))
