(ns laboratory.parts.user
  (:require
   [cljs.spec.alpha :as s]
   [fancoil.base :as base]
   [fancoil.module.posh.base :as posh.base]))

;; data

(def user-sample
  #:user {:db/id -1
          :name "dr who"
          :backpack {:db/id -34}
          :user/right-tool {:db/id -100}})

;; schema

(defmethod posh.base/schema :user/schema
  [_ _]
  {:user/name {:db/unique :db.unique/identity}
   :user/backpack {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/mouse {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/camera-control {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :user/right-tool {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

;; spec

(defmethod base/spec :user/name
  [_ _]
  string?)

(defmethod base/spec :user/backpack
  [_ _]
  (base/spec {} :db/entity)
  (s/def :user/backpack :db/entity))

;; model

(defmethod base/model :user/create
  [_ _ props]
  (let [default {:user/name "default"
                 :framework/_user [:framework/name "default"]}]
    (merge default props)))


(defmethod base/model :user/select-tool-tx
  [{:keys [spec]} _ {:keys [user tool]}]
  (spec :assert :db/entity user)
  (spec :assert (s/nilable :db/entity) tool)
  (when tool
    [[:db/add (:db/id user) :user/right-tool tool]]))

(defmethod base/model :user/drop-tool-tx
  [{:keys [spec]} _ {:keys [user]}]
  (spec :assert :db/entity user)
  [[:db.fn/retractAttribute (:db/id user) :user/right-tool]])


;; view

(defmethod base/view :user/left-hand.view
  [_ _ _]
  [:div {:class "astronomy-lefthand"}
   [:div {:style {:font-size "14px"
                  :color "#aaa"}}
    [:p "left hand"]]])

(defmethod base/view :user/right-hand.view
  [{:keys [subscribe] :as core} _ {:keys [tool]}]
  (let [tool @(subscribe :entity/pull {:id (:db/id tool)})
        tool-type (:entity/type tool)]
    [base/view core :tool/view tool]))


(defmethod base/view :user/view
  [{:keys [subscribe] :as core} _ user]
  (let [user1 @(subscribe :entity/pull {:id (:db/id user)})
        backpack (:user/backpack user1)]
    [:<>
     [base/view core :user/left-hand.view {}]
     [base/view core :backpack/view backpack]
     (when (:user/right-tool user1)
       [base/view core :user/right-hand.view {:user user1
                                              :tool (:user/right-tool user1)}])]))
