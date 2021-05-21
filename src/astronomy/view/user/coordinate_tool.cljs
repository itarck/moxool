(ns astronomy.view.user.coordinate-tool
  (:require
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [astronomy.model.user.coordinate-tool :as m.coordinate-tool]))



(defn CoordinateToolView [props {:keys [service-chan conn]}]
  (let [tool (m.coordinate-tool/sub-one conn (:db/id props))]
    [:div.p-2
     [:div
      [:img {:src (:tool/icon tool)
             :class "astronomy-button"}]
      [:span {:style {:font-size "18px"
                      :font-weight "bold"}}
       (:tool/chinese-name tool)]]

     (str tool)]))