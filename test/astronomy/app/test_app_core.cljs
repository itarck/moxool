(ns astronomy.app.test-app-core
  (:require
   [astronomy.app.core :as app]
   [cljs.core.async :refer [go >!]]
   [posh.reagent :as p]
   [film2.system.studio :as studio]
   [datascript.core :as d]
   [astronomy.objects.celestial-group.m :as cg.m]
   [astronomy.objects.astronomical-coordinate.m :as ac.m]))


;; current studio in app core

(comment
  (def solar-system1 (:scene-system @(:studio/instance-atom app/studio)))


  (def conn (get-in solar-system1 [:system/conn]))
  (def service-chan (get-in solar-system1 [:system/service-chan]))


  (def clock @(p/pull conn '[*] [:clock/name "default"]))
  (def clock-tool @(p/pull conn '[*] [:tool/name "clock control 1"]))

  (go (>! service-chan #:event{:action :clock-tool/start
                               :detail {:clock-tool clock-tool
                                        :clock clock}}))

  (:clock/time-in-days @(p/pull conn '[*] [:clock/name "default"]))


  (go (>! service-chan #:event{:action :clock-tool/stop
                               :detail {:clock-tool clock-tool
                                        :clock clock}})))



;; 

(comment

  (def studio-2 (studio/create-app! {}))

  (def solar-system2 (:scene-system @(:studio/instance-atom studio-2)))


  (def conn2 (get-in solar-system2 [:system/conn]))
  (def service-chan2 (get-in solar-system2 [:system/service-chan]))


  (def clock2 @(p/pull conn2 '[*] [:clock/name "default"]))
  (def clock-tool2 @(p/pull conn2 '[*] [:tool/name "clock control 1"]))

  (go (>! service-chan2 #:event{:action :clock-tool/start
                                :detail {:clock-tool clock-tool
                                         :clock clock}}))

  (go (>! service-chan2 #:event{:action :clock-tool/stop
                                :detail {:clock-tool clock-tool
                                         :clock clock}}))
  
  )


(comment

  (def solar-system1 (:scene-system @(:studio/instance-atom app/studio)))
  (def conn (get-in solar-system1 [:system/conn]))
  (def service-chan (get-in solar-system1 [:system/service-chan]))

  (def astro-scene @(p/pull conn '[*] [:scene/name "solar"]))
  (def ac-1 @(p/pull conn '[*] [:coordinate/name "赤道天球坐标系"]))

  (def sun @(p/pull conn '[*] [:star/name "sun"]))
  (def earth @(p/pull conn '[*] [:planet/name "earth"]))

  (def mercury @(p/pull conn '[*] [:planet/name "mercury"]))

  (def venus @(p/pull conn '[*] [:planet/name "venus"]))

  (let [tx [{:db/id (get-in mercury [:celestial/orbit :db/id])
            :orbit/show? true}]]
    (p/transact! conn tx))

  #_(let [tx (ac.m/change-center-object-tx @conn ac-1 sun)]
    (p/transact! conn tx))

  (let [event #:event {:action :astronomical-coordinate-tool/change-center-object
                       :detail {:astronomical-coordinate ac-1
                                :center-object sun}}]
    (go (>! service-chan event)))

  (let [tx [{:db/id (get-in sun [:db/id])
             :celestial/scale 5}]]
    (p/transact! conn tx))

  )



(def solar-system1 (:scene-system @(:studio/instance-atom app/studio)))
(def conn (get-in solar-system1 [:system/conn]))


@(p/pull conn '[*] [:satellite/name "moon"])
(:celestial/orbit @(p/pull conn '[*] [:satellite/name "newton-apple"]))

