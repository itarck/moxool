(ns astronomy.view.user.info-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [astronomy.model.user.info-tool :as m.info-tool]
   ))



(defn InfoToolView [props {:keys [service-chan conn]}]
  (let [info-tool (m.info-tool/sub-info-tool conn (:db/id props))]
    [:div.p-2
     [:div
      [:img {:src (:tool/icon info-tool)
             :class "astronomy-button"}]
      [:span {:style {:font-size "18px"
                      :font-weight "bold"}}
       (:tool/chinese-name info-tool)]]

     (if (:info-tool/object info-tool)
       [:div
        (str (:info-tool/object info-tool))]
       [:div
        "请点击场景中的物体"])]))