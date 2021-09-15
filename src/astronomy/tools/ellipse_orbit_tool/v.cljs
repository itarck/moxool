(ns astronomy.tools.ellipse-orbit-tool.v
  (:require
   [applied-science.js-interop :as j]
   [cljs.core.async :refer [go >!]]
   [posh.reagent :as p]
   ["@material-ui/core" :as mt]
   ))



(defn EllipseOrbitToolView [{:keys [tool]} {:keys [conn service-chan]}]
  (let [eot @(p/pull conn '[*] (:db/id tool))
        ids @(p/q '[:find [?id ...]
                    :where [?id :entity/type :planet]]
                  conn)
        candidates (doall (mapv (fn [id] @(p/pull conn '[:db/id :planet/chinese-name] id)) ids))]
    
    [:div {:class "astronomy-righthand"}
     [:div {:class "astronomy-righthand-tool"}
      [:div.p-2
       [:div
        [:img {:src (:tool/icon eot)
               :class "astronomy-button"}]
        [:span {:style {:font-size "18px"
                        :font-weight "bold"}}
         (:tool/chinese-name eot)]]

       [:> mt/Grid {:item true :xs 12}
        [:> mt/Select {:value (get-in eot [:selector/selected :db/id])
                       :onChange (fn [e]
                                   (let [new-value (j/get-in e [:target :value])]
                                     (go (>! service-chan
                                             #:event {:action :selector/select
                                                      :detail {:selector tool
                                                               :selected new-value}}))))}
         (for [one candidates]
           ^{:key (:db/id one)}
           [:> mt/MenuItem {:value (:db/id one)}
            (or (:planet/chinese-name one)
                (:db/id one))])]]]]]))