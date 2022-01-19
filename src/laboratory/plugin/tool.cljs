(ns laboratory.plugin.tool
  (:require
   [cljs.spec.alpha :as s]
   [laboratory.base :as base]))


;; value

(def sample
  {:tool/name "constellation-tool"
   :tool/chinese-name "星座"
   :tool/icon "/image/pirate/earth.jpg"
   :tool/type :constellation-tool
   :entity/type :constellation-tool})

;; schema

(defmethod base/schema :tool/scheme
  [_ _]
  {:tool/name {:db/unique :db.unique/identity}
   :tool/target {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})

;; spec 

(defmethod base/spec :tool/spec
  [_ _]
  (s/def :tool/name string?))

;; model 

(defmethod base/model :tool/create
  [_ _ props]
  (let [default {}]
    (merge default props)))

;; handle


;; view

(defmethod base/view :tool/view
  [{:keys [subscribe] :as core} _ tool]
  (let [tool @(subscribe :entity/pull {:entity tool})]
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

