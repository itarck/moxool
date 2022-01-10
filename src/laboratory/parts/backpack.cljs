(ns laboratory.parts.backpack
  (:require
   [fancoil.base :as base]
   [fancoil.module.posh.base :as posh.base]
   [datascript.core :as d]
   [posh.reagent :as p]))


;; data

(def default
  #:backpack {:name "default"
              :owner [:user/name "default"]
              :cell (vec (for [i (range 12)]
                           #:backpack-cell{:index i}))})

;; schema

(defmethod posh.base/schema :backpack/schema
  [_ _]
  {:backpack/name {:db/unique :db.unique/identity}
   :backpack/owner {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :backpack/active-cell {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :backpack/cell {:db/valueType :db.type/ref
                   :db/cardinality :db.cardinality/many
                   :db/isComponent true}
   :backpack-cell/tool {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; model

(defmethod base/model :backpack/create
  [_ _ props]
  (let [default #:backpack {:name "default"
                            :user/_backpack [:user/name "default"]
                            :owner [:user/name "default"]
                            :cell (vec (for [i (range 12)]
                                         #:backpack-cell{:index i}))}]
    (merge default props)))

(defmethod base/model :backpack/find-nth-cell
  [_ _ {:keys [backpack nth-cell]}]
  (->
   (filter (fn [cell] (= (:backpack-cell/index cell) nth-cell))
           (:backpack/cell backpack))
   first))

(defmethod base/model :backpack/put-in-cell-tx
  [_ _ {:keys [backpack nth-cell tool]}]
  (let [cell (base/model {} :backpack/find-nth-cell {:backpack backpack
                                                     :nth-cell nth-cell})]
    [[:db/add (:db/id cell) :backpack-cell/tool (:db/id tool)]]))

(defmethod base/model :backpack/active-cell-tx
  [_ _ {:keys [backpack cell-id]}]
  [[:db/add (:db/id backpack) :backpack/active-cell cell-id]])

(defmethod base/model :backpack/deactive-cell-tx
  [_ _ {:keys [backpack]}]
  [[:db.fn/retractAttribute (:db/id backpack) :backpack/active-cell]])

(defmethod base/model :backpack/put-in-packpack-tx
  [_ _ {:keys [backpack tools]}]
  (apply concat
         (for [i (range (count tools))]
           (base/model {} :backpack/put-in-cell-tx {:backpack backpack
                                                    :nth-cell i
                                                    :tool (get tools i)}))))

(defmethod base/model :backpack/clear-backpack-tx
  [_ _ {:keys [db backpack]}]
  (let [backpack-1 (d/pull db '[{:backpack/cell [*]}] (:db/id backpack))
        tx (vec (for [cell (:backpack/cell backpack-1)]
                  [:db.fn/retractAttribute (:db/id cell) :backpack-cell/tool]))]
    tx))

;; subscribe 

(defmethod base/subscribe :backpack/pull
  [{:keys [pconn]} _ {:keys [pattern id]}]
  (let [pt (or pattern '[* {:backpack/cell [{:backpack-cell/tool [*]}]
                            :user/_backpack [:db/id]}])]
    (p/pull pconn pt id)))

;; view

(defmethod base/view :backpack/view
  [{:keys [subscribe dispatch]} _ backpack]
  (let [bp @(subscribe :backpack/pull {:id (:db/id backpack)})
        user @(subscribe :entity/pull {:id (get-in bp [:user/_backpack :db/id])})
        active-cell (:backpack/active-cell bp)]
    [:div {:class "d-flex justify-content-center astronomy-backpack"}
     (for [cell (:backpack/cell bp)]
       (let [tool (:backpack-cell/tool cell)
             style (if (= (:db/id active-cell) (:db/id cell))
                     "astronomy-cell astronomy-cell-active"
                     "astronomy-cell")]
         ^{:key (:db/id cell)}
         [:div {:class style
                :onClick #(dispatch :user/click-backpack-cell {:user user
                                                               :backpack bp
                                                               :cell cell
                                                               :active-cell active-cell})}
          (when tool
            [:img {:src (-> tool :tool/icon)
                   :class "astronomy-button"}])]))]))