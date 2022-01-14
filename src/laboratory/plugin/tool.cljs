(ns laboratory.plugin.tool
  (:require
   [laboratory.base :as base]))


;; value

(def sample
  {:tool/name "constellation-tool"
   :tool/chinese-name "星座"
   :tool/icon "/image/pirate/earth.jpg"
   :tool/type :constellation-tool
   :entity/type :constellation-tool})

;; model 

(defmethod base/model :tool/create
  [_ _ props]
  (let [default {}]
    (merge default props)))

;; handle


;; view

(defmethod base/view :tool/view
  [{:keys [subscribe] :as core} _ tool]
  (let [tool @(subscribe :db/pull {:id (:db/id tool)})]
    (if (:tool/type tool)
      [base/view core (keyword (:tool/type tool) "view") tool]
      [:div {:class "astronomy-righthand"}
       [:div {:class "astronomy-righthand-tool"}
        [:div.p-2
         [:div
          [:img {:src (:tool/icon tool)
                 :class "astronomy-button"}]
          [:span {:style {:font-size "18px"
                          :font-weight "bold"}}
           (:tool/chinese-name tool)]]]]])))

