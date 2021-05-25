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
  (let [universe-tool @(p/pull conn '[*] (:db/id props))
        astro-scene (m.astro-scene/sub-scene-with-objects conn (-> universe-tool :universe-tool/astro-scene :db/id))
        objects (:object/_scene astro-scene)]
    [:div.p-2
     [:div
      [:img {:src (:tool/icon universe-tool)
             :class "astronomy-button"}]
      [:span {:style {:font-size "18px"
                      :font-weight "bold"}}
       (:tool/chinese-name universe-tool)]]

     [:div
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
             :onChange #(js/console.log (j/get-in % [:target :checked]))}]
           [:span "是"]])]]
      (str (map :entity/chinese-name objects))]]))