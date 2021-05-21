(ns astronomy.view.user.coordinate-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [helix.core :refer [$]]
   [posh.reagent :as p]
   [astronomy.model.user.coordinate-tool :as m.coordinate-tool]
   [astronomy.model.celestial :as m.celestial]
   ["@material-ui/core" :as mt]))



(defn CoordinateToolView [props {:keys [service-chan conn]}]
  (let [tool (m.coordinate-tool/sub-one conn (:db/id props))
        coordinate (:coordinate-tool/coordinate tool)
        candidate-id-and-names @(p/q m.celestial/query-all-id-and-chinese-name conn)]
    [:div.p-2
     [:div
      [:img {:src (:tool/icon tool)
             :class "astronomy-button"}]
      [:span {:style {:font-size "18px"
                      :font-weight "bold"}}
       (:tool/chinese-name tool)]]

     [:> mt/Grid {:container true :spacing 1}
      [:> mt/Grid {:item true :xs 12}
       [:> mt/Typography {:variant "subtitle2"} "坐标系中心："]
       [:> mt/Select {:value (-> coordinate :coordinate/track-position :db/id)
                      :onChange (fn [e]
                                  (let [value (j/get-in e [:target :value])]
                                    (println value)))}
        (for [[id name] candidate-id-and-names]
          ^{:key id}
          [:> mt/MenuItem {:value id} name])]]

      [:> mt/Grid {:item true :xs 12}
       [:> mt/Typography {:variant "subtitle2"} "坐标系平面："]
       [:> mt/Select {:value (-> coordinate :coordinate/track-rotation :db/id)
                      :onChange (fn [e]
                                  (let [value (j/get-in e [:target :value])]
                                    (println value)))}
        (for [[id name] candidate-id-and-names]
          ^{:key id}
          [:> mt/MenuItem {:value id} name])]]]

     #_(str tool)]))


