(ns astronomy.view.user.ruler-tool
  (:require
   [posh.reagent :as p]
   [astronomy.model.astronomical-point :as m.apt]
   [astronomy.component.line :as c.line]))


(defn RulerSceneView [props {:keys [conn]}]
  (let [ruler-tool @(p/pull conn '[*] (get-in props [:object :db/id]))
        {:ruler-tool/keys [point1 point2]} ruler-tool
        point-vector1 (m.apt/cal-position-vector3 point1)
        point-vector2 (m.apt/cal-position-vector3 point2)]
    [:> c.line/LineComponent {:points [point-vector1 point-vector2]
                              :color "green"}]))