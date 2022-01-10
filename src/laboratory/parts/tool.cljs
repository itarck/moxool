(ns laboratory.parts.tool
  (:require
   [fancoil.base :as base]
   [posh.reagent :as p]))

;; value

(def sample 
  {:tool/name "constellation-tool"
   :tool/chinese-name "星座"
   :tool/icon "/image/pirate/earth.jpg"
   :tool/type :constellation-tool
   :entity/type :constellation-tool})


;; handle


;; view

(defmethod base/view :tool/view
  [{:keys [subscribe]} _ tool]
  (let [tool @(subscribe :entity/pull {:id (:db/id tool)})]
    [:div {:class "astronomy-righthand"}
     [:div {:class "astronomy-righthand-tool"}
      [:div.p-2
       [:div
        [:img {:src (:tool/icon tool)
               :class "astronomy-button"}]
        [:span {:style {:font-size "18px"
                        :font-weight "bold"}}
         (:tool/chinese-name tool)]]]]]))

