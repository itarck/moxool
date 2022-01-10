(ns laboratory.parts.user
  (:require
   [fancoil.base :as base]
   [fancoil.module.posh.base :as posh.base]))


(defmethod posh.base/schema :user/schema
  [_ _]
  {:user/name {:db/unique :db.unique/identity}
   :user/backpack {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/mouse {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/camera-control {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/right-tool {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; model

(defmethod base/model :user/create
  [_ _ props]
  (let [default {:user/name "default"
                 :framework/_user [:framework/name "default"]}]
    (merge default props)))

;; view

(defmethod base/view :user/left-hand.view
  [_ _ _]
  [:div {:class "astronomy-lefthand"}
   [:div {:style {:font-size "14px"
                  :color "#aaa"}}
    [:p "left hand"]]])

(defmethod base/view :user/right-hand.view
  [{:keys [subscribe] :as core} _ {:keys [tool]}]
  (let [tool @(subscribe :entity/pull (:db/id tool))
        tool-type (:entity/type tool)]
    [base/view core (keyword tool-type "view") {:tool tool}]))


(defmethod base/view :user/view
  [{:keys [subscribe] :as core} _ user]
  (let [user1 @(subscribe :entity/pull {:id (:db/id user)})
        backpack (:user/backpack user1)]
    [:<>
     [base/view core :user/left-hand.view {}]
     [base/view core :backpack/view backpack]
     #_(when (:user/right-tool user)
         [base/view core :user/right-hand.view {:user user
                                                :tool (:user/right-tool user)}])]))