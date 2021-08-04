(ns astronomy.view.user.ruler-tool
  (:require
   [posh.reagent :as p]
   ["@react-three/drei" :refer [Html]]
   [goog.string :as gstring]
   [shu.three.vector3 :as v3]
   [astronomy.component.mouse :as c.mouse]
   [astronomy.component.line :as c.line]
   [methodology.model.user.person :as m.person]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.astronomical-point :as m.apt]
   [astronomy.model.user.ruler-tool :as m.ruler-tool]))




(defn LineWithMarkView
  "
   {
   :astronomical-point1 point1
   :astronomical-point2 point2
   :color color
   :show-distance? true
   }
   "
  [props]
  (let [{:keys [astronomical-point1 astronomical-point2 color show-distance?]} props
        point-vector1 (m.apt/cal-position-vector3 astronomical-point1)
        point-vector2 (m.apt/cal-position-vector3 astronomical-point2)
        mid-vector (v3/multiply-scalar (v3/add point-vector1 point-vector2) 0.5)
        distance (m.apt/distance-in-degree astronomical-point1 astronomical-point2)]

    [:<>
     [:> c.line/LineComponent {:points [point-vector1 point-vector2]
                               :color color}]
     (when show-distance?
       [:> Html {:position mid-vector
                 :zIndexRange [0 0]
                 :style {:color color
                         :font-size "14px"}}
        [:p (gstring/format "%0.2f" distance)]])]))


(defn RulerSceneView
  [{:keys [user object astro-scene]} {:keys [conn dom-atom]}]
  (when (m.person/in-right-hand? user object)
    (let [ruler-tool @(p/pull conn '[*] (:db/id object))
          {:ruler-tool/keys [point1 point2 status]} ruler-tool]
    ;;   (println "RulerSceneView: " (m.ruler-tool/distance-in-degree ruler-tool))
      (case status
        :select2 [LineWithMarkView {:astronomical-point1 point1
                                    :astronomical-point2 point2
                                    :color "green"
                                    :show-distance? true}]
        :select1 (let [mouse @(p/pull conn '[*] (get-in user [:person/mouse :db/id]))
                       mouse-direction (c.mouse/get-mouse-direction-vector3 (:three-instance @dom-atom))
                       camera-position (c.mouse/get-camera-position (:three-instance @dom-atom))
                       scene-coordinate (m.astro-scene/sub-scene-coordinate conn astro-scene)
                       mouse-point (m.apt/from-local-camera-view scene-coordinate (vec camera-position) (vec mouse-direction))]
                   [LineWithMarkView {:astronomical-point1 point1
                                      :astronomical-point2 mouse-point
                                      :color "green"
                                      :show-distance? false}])
        nil))))