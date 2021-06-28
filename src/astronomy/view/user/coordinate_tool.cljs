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
  (let [tool (m.coordinate-tool/sub-one conn (get-in props [:tool :db/id]))
        coordinate (:coordinate-tool/coordinate tool)
        candidate-id-and-names (sort-by first @(p/q m.celestial/query-all-id-and-chinese-name conn))]
    [:div {:class "astronomy-righthand"}
     [:div {:class "astronomy-righthand-tool"}
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
                                    (let [new-id (j/get-in e [:target :value])]
                                      (go (>! service-chan
                                              #:event {:action :coordinate-tool/set-track-position
                                                       :detail {:coordinate-id (:db/id coordinate)
                                                                :track-position-id new-id}}))))}
          (for [[id name] candidate-id-and-names]
            ^{:key id}
            [:> mt/MenuItem {:value id} name])]]

        [:> mt/Grid {:item true :xs 12}
         [:> mt/Typography {:variant "subtitle2"} "坐标系平面："]
         [:> mt/Select {:value (-> coordinate :coordinate/track-rotation :db/id)
                        :onChange (fn [e]
                                    (let [new-id (j/get-in e [:target :value])]
                                      (go (>! service-chan
                                              #:event {:action :coordinate-tool/set-track-rotation
                                                       :detail {:coordinate-id (:db/id coordinate)
                                                                :track-rotation-id new-id}}))))}
          (for [[id name] candidate-id-and-names]
            ^{:key id}
            [:> mt/MenuItem {:value id} name])]]]]]]))


