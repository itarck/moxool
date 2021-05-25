(ns astronomy.view.user.core
  (:require
   [posh.reagent :as p]
   [methodology.view.backpack :as v.backpack]
   [astronomy.view.user.universe-tool :as v.universe-tool]
   [astronomy.view.user.clock-tool :as v.clock-tool]
   [astronomy.view.user.spaceship-camera-control :as v.spaceship]
   [astronomy.view.user.info-tool :as v.info-tool]
   [astronomy.view.user.coordinate-tool :as v.coordinate-tool]))


(defn RightHandToolView [{:keys [tool camera-control]} {:keys [conn] :as env}]
  (let [tool @(p/pull conn '[*] (:db/id tool))]
    [:div {:class "astronomy-righthand"}
     [:div {:class "astronomy-righthand-tool"}
      (case (:entity/type tool)
        :clock-tool [v.clock-tool/ClockToolView {:clock-tool tool
                                                 :camera-control camera-control} env]
        :info-tool [v.info-tool/InfoToolView tool env]
        :coordinate-tool [v.coordinate-tool/CoordinateToolView tool env]
        :universe-tool [v.universe-tool/UniverseToolView tool env]
        nil)]]))


(defn UserView [{:keys [user camera camera-control] :as props} {:keys [conn] :as env}]
  (let [user @(p/pull conn '[*] (:db/id user))
        backpack (:person/backpack user)]
    [:<>
     [v.spaceship/SpaceshipCameraToolView {:camera-control camera-control
                                           :camera camera} env]
     [v.backpack/BackPackView {:user user
                               :backpack backpack} env]
     (when (:person/right-tool user)
       [RightHandToolView {:tool (:person/right-tool user)
                           :camera-control camera-control} env])]))

