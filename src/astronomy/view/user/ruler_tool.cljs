(ns astronomy.view.user.ruler-tool
  (:require
   [posh.reagent :as p]
   [astronomy.component.mouse :as c.mouse]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.astronomical-point :as m.apt]
   [astronomy.component.line :as c.line]))



(defn RulerSceneView
  [props {:keys [conn dom-atom]}]
  (let [ruler-tool @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:ruler-tool/keys [point1 point2 status]} ruler-tool
        point-vector1 (m.apt/cal-position-vector3 point1)
        point-vector2 (m.apt/cal-position-vector3 point2)]
    (case status
      :select2 [:> c.line/LineComponent {:points [point-vector1 point-vector2]
                                         :color "green"}]
      :select1 (let [user @(p/pull conn '[*] (get-in props [:user :db/id]))
                     mouse @(p/pull conn '[*] (get-in user [:person/mouse :db/id]))
                     mouse-direction (c.mouse/get-mouse-direction-vector3 (:three-instance @dom-atom))
                     camera-position (c.mouse/get-camera-position (:three-instance @dom-atom))
                     scene-coordinate (m.astro-scene/sub-scene-coordinate conn (get-in props [:astro-scene]))
                     mouse-point (m.apt/from-local-camera-view scene-coordinate (vec camera-position) (vec mouse-direction))]
                 [:> c.line/LineComponent {:points [point-vector1 (m.apt/cal-position-vector3 mouse-point)]
                                           :color "green"}])

      nil)))