(ns astronomy.view.user.universe-tool
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >! <! go-loop] :as a]
   [posh.reagent :as p]
   [helix.core :refer [$]]
   ["@material-ui/core" :as mt]
   [astronomy.model.user.universe-tool :as m.universe-tool]
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.reference :as m.reference]
   [astronomy.model.coordinate :as m.coordinate]))



(defn UniverseToolView [props {:keys [service-chan conn]}]
  (let [universe-tool @(p/pull conn '[*] (get-in props [:tool :db/id]))
        astro-scene-id (-> universe-tool :universe-tool/astro-scene :db/id)
        astro-scene @(p/pull conn '[*] astro-scene-id)
        ;; reference @(p/pull conn '[*] (get-in astro-scene [:astro-scene/reference :db/id]))
        ;; reference-names @(p/q m.reference/query-reference-names conn)
        coordinate-names (m.coordinate/sub-all-coordinate-names conn)
        coordinate @(p/pull conn '[*] (get-in astro-scene [:astro-scene/coordinate :db/id]))]
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
          "参考系"]
         [:> mt/Select {:value (:coordinate/name coordinate)
                        :onChange (fn [e]
                                    (let [new-value (j/get-in e [:target :value])]
                                      (go (>! service-chan
                                              #:event {:action :astro-scene/change-coordinate
                                                       :detail {:coordinate-name new-value}}))))}
          (for [name coordinate-names]
            ^{:key name}
            [:> mt/MenuItem {:value name} name])]]

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