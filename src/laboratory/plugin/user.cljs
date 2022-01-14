(ns laboratory.plugin.user
  (:require
   [cljs.spec.alpha :as s]
   [laboratory.base :as base]
   [posh.reagent :as p]
   [reagent.core :as r]))



;; data

(def user-sample
  #:user {:db/id -1
          :name "dr who"
          :backpack {:db/id -34}
          :user/right-tool {:db/id -100}})

;; schema

(defmethod base/schema :user/schema
  [_ _]
  {:user/name {:db/unique :db.unique/identity}
   :user/backpack {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/mouse {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/camera-control {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/right-tool {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

;; spec


(defmethod base/spec :user/spec
  [_ _]
  (base/spec {} :entity/spec)
  (s/def :user/name string?)
  (s/def :user/backpack (s/keys :req [:db/id]))
  (s/def :user/entity (s/keys :req [:db/id :user/name :user/backpack])))

;; model

(defmethod base/model :user/create
  [_ _ props]
  (let [default {:user/name "default"
                 :framework/_user [:framework/name "default"]}]
    (merge default props)))

;; subscribe

(defmethod base/subscribe :user/right-hand-tool
  [{:keys [pconn]} _ {:keys [user]}]
  (s/assert :entity/entity user)
  (let [user @(p/pull pconn '[{:user/backpack
                               [{:backpack/active-cell 
                                 [{:backpack-cell/tool [*]}]}]}] (:db/id user))]
    (r/reaction (get-in user [:user/backpack :backpack/active-cell :backpack-cell/tool]))))

;; view

(defmethod base/view :user/left-hand.view
  [_ _ _]
  [:div {:class "astronomy-lefthand"}
   [:div {:style {:font-size "14px"
                  :color "#aaa"}}
    [:p "left hand"]]])

(defmethod base/view :user/right-hand.view
  [{:keys [subscribe] :as core} _ {:keys [tool]}]
  (let [tool @(subscribe :entity/pull {:entity tool})
        tool-type (:entity/type tool)]
    [base/view core :tool/view tool]))


(defmethod base/view :user/view
  [{:keys [subscribe] :as core} _ user]
  (let [user1 @(subscribe :entity/pull {:entity user})
        right-hand-tool @(subscribe :user/right-hand-tool {:user user})
        backpack (:user/backpack user1)]
    [:<>
     [base/view core :user/left-hand.view {}]
     [base/view core :backpack/view backpack]
     (when right-hand-tool
       [base/view core :user/right-hand.view {:tool right-hand-tool}])]))
