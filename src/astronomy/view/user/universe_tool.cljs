(ns astronomy.view.user.universe-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [helix.core :refer [$]]
   ["@material-ui/core" :as mt]
   [astronomy.model.user.universe-tool :as m.universe-tool]
   [astronomy.model.astro-scene :as m.astro-scene]))



(defn UniverseToolView [props {:keys [service-chan conn]}]
  (let [universe-tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        astro-scene-id (-> universe-tool :universe-tool/astro-scene :db/id)
        astro-scene (m.astro-scene/sub-scene-with-objects conn astro-scene-id)
        objects (:object/_scene astro-scene)]
    [:div {:class "astronomy-righthand"}
     [:div {:class "astronomy-righthand-tool"}
      [:div.p-2
       [:div
        [:img {:src (:tool/icon universe-tool)
               :class "astronomy-button"}]
        [:span {:style {:font-size "18px"
                        :font-weight "bold"}}
         (:tool/chinese-name universe-tool)]]

       [:> mt/Grid {:container true :spacing 1}

        [:> mt/Grid {:item true :xs 12}
         [:> mt/Typography {:variant "subtitle1"}
          "是否加载"]
         (for [object objects]
           ^{:key (:db/id object)}
           [:div
            (str (:entity/chinese-name object) "：    ")
            [:span "否"]
            [:> mt/Switch
             {:color "default"
              :size "small"
              :checked (or (:object/show? object) false)
              :onChange (fn [event]
                          (let [show? (j/get-in event [:target :checked])]
                            (go (>! service-chan #:event {:action :universe-tool/load-object
                                                          :detail {:object-id (:db/id object)
                                                                   :show? show?}}))))}]
            [:span "是"]])]

        #_(let [celestial-scale (:astro-scene/celestial-scale astro-scene)]
            [:> mt/Grid {:item true :xs 12}
             [:> mt/Typography {:variant "subtitle2"} "天体放大系数: " celestial-scale]
             ($ mt/Slider
                {:style #js {:color "#666"
                             :width "200px"}
                 :value celestial-scale
                 :onChange (fn [e value]
                             (go (>! service-chan #:event {:action :universe-tool/change-celestial-scale
                                                           :detail {:astro-scene-id astro-scene-id
                                                                    :celestial-scale value}})))
                 :step 10 :min 1 :max 100 :marks true
                 :getAriaValueText identity
                 :aria-labelledby "discrete-slider-restrict"
                 :valueLabelDisplay "auto"})])]]]]
    ))