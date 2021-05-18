(ns methodology.view.backpack
  (:require
   [posh.reagent :as p]
   [cljs.core.async :refer [go >!]]
   [methodology.model.user.backpack :as m.backpack]))


(defn BackPackView [{:keys [backpack user]} {:keys [service-chan conn]}]
  (let [user @(p/pull conn '[*] (-> user :db/id))
        bp (m.backpack/sub-backpack-fully conn (:db/id backpack))
        active-cell (:backpack/active-cell bp)]
    [:div {:class "d-flex justify-content-center astronomy-backpack"}
     (for [cell (:backpack/cell bp)]
       (let [tool (:backpack-cell/tool cell)
             style (if (= (:db/id active-cell) (:db/id cell))
                     "astronomy-cell astronomy-cell-active"
                     "astronomy-cell")]
         ^{:key (:db/id cell)}
         [:div {:class style
                :onClick #(go (>! service-chan #:event{:action :user/active-backpack-cell
                                                       :detail {:user user
                                                                :backpack bp
                                                                :cell cell}}))}
          (when tool
            [:img {:src (-> tool :tool/icon)
                   :class "astronomy-button"}])]))]))

