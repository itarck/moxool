(ns astronomy.view.user.crosshair-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :refer [bean ->js]]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [helix.core :refer [$ defnc]]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]))



(defnc PanelsComponent [{:keys [classes]}]
  {:wrap [((mt/withStyles (->js {:root {:margin-top "4px"}
                                 :button  {:padding "4px 10px"}})))]}
  (let [bclasses (bean classes)]
    ($ mt/ButtonGroup {:size "medium"
                       :className (:root bclasses)}
       ($ mt/Button {:onClick println
                     :variant "contained"
                     :className (:button bclasses)}
          "新建")
       ($ mt/Button {:onClick println
                     :variant "outlined"
                     :className (:button bclasses)}
          "读取")
       ($ mt/Button {:onClick println
                     :variant "outlined"
                     :className (:button bclasses)}
          "删除")))
  )


(defn CrosshairToolView [props {:keys [service-chan conn]}]
  (let [tool @(p/pull conn '[*] (get-in props [:tool :db/id]))]
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
         ($ PanelsComponent)]
        [:> mt/Grid {:item true :xs 12}
         [:span (str tool)]]]


    ;;    
       ]]]))

