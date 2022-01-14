(ns laboratory.plugin.backpack
  (:require
   [cljs.spec.alpha :as s]
   [laboratory.base :as base]
   [datascript.core :as d]
   [posh.reagent :as p]))


;; data

(def default
  #:backpack {:name "default"
              :owner [:user/name "default"]
              :cell (vec (for [i (range 12)]
                           #:backpack-cell{:index i}))})

;; schema

(defmethod base/schema :backpack/schema
  [_ _]
  {:backpack/name {:db/unique :db.unique/identity}
   :backpack/owner {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :backpack/active-cell {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :backpack/cells {:db/valueType :db.type/ref
                    :db/cardinality :db.cardinality/many
                    :db/isComponent true}
   :backpack-cell/tool {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

;; spec

(defmethod base/spec :backpack/spec
  [_ _]
  (s/def :backpack-cell/backpack-cell (s/keys :req [:db/id :backpack-cell/index]))
  (s/def :backpack/cells (s/coll-of :backpack-cell/backpack-cell))
  (s/def :backpack/backpack (s/keys :req [:db/id :backpack/name :backpack/cells])))

;; model

(defmethod base/model :backpack/create
  [_ _ entity]
  (let [default #:backpack {:name "default"
                            :user/_backpack [:user/name "default"]
                            :owner [:user/name "default"]
                            :cell (vec (for [i (range 12)]
                                         #:backpack-cell{:index i}))}]
    (merge default entity)))

(defmethod base/model :backpack/pull
  [_ _ {:keys [db entity]}]
  (s/assert :db/entity entity)
  (d/pull db '[*] (:db/id entity)))

(defmethod base/model :backpack/sync
  [_ _ {:keys [db entity]}]
  (if (s/valid? :backpack/backpack entity)
    entity
    (base/model :backpack/pull {:entity entity
                                :db db})))

(defmethod base/model :backpack/query-nth-cell
  [_ _ {:keys [db backpack nth-cell]}]
  (let [cell-id (d/q '[:find ?cell-id .
                       :where
                       [?bp-id :backpack/cells ?cell-id]
                       [?cell-id :backpack-cell/index ?nth-cell]
                       :in $ ?bp-id ?nth-cell]
                     db (:db/id backpack) nth-cell)]
    (when cell-id
      {:db/id cell-id})))

(defmethod base/model :backpack/get-nth-cell
  [_ _ {:keys [backpack nth-cell]}]
  (s/assert :backpack/backpack backpack)
  (->
   (filter (fn [cell] (= (:backpack-cell/index cell) nth-cell))
           (:backpack/cells backpack))
   first))

(defmethod base/model :backpack/sorted-cells
  [_ _ {:keys [backpack]}]
  (s/assert (s/keys :req [:backpack/cells]) backpack)
  (let [cells (:backpack/cells backpack)]
    (sort-by :backpack-cell/index cells)))

(defmethod base/model :backpack/active-cell-tx
  [_ _ {:keys [backpack cell]}]
  [[:db/add (:db/id backpack) :backpack/active-cell (:db/id cell)]])

(defmethod base/model :backpack/deactive-cell-tx
  [_ _ {:keys [backpack]}]
  [[:db.fn/retractAttribute (:db/id backpack) :backpack/active-cell]])

(defmethod base/model :backpack/put-in-nth-cell-tx
  [_ _ {:keys [backpack nth-cell tool]}]
  (let [cell (base/model {} :backpack/get-nth-cell {:backpack backpack
                                                    :nth-cell nth-cell})]
    [[:db/add (:db/id cell) :backpack-cell/tool (:db/id tool)]]))

(defmethod base/model :backpack/init-tools-tx
  [_ _ {:keys [backpack tools]}]
  (vec (apply concat
              (for [i (range (count tools))]
                (base/model {} :backpack/put-in-nth-cell-tx
                            {:backpack backpack
                             :nth-cell i
                             :tool (get tools i)})))))

;; handle 

(defmethod base/handle :backpack/click-cell
  [{:keys [model]} _ {:request/keys [body] db :posh/db}]
  (let [{:keys [backpack cell]} body
        backpack (model :backpack/sync {:entity backpack
                                        :db db})
        active-cell (:backpack/active-cell backpack)
        tx (if (= (:db/id active-cell) (:db/id cell))
             (model :backpack/deactive-cell-tx {:backpack backpack})
             (concat
              (model :backpack/deactive-cell-tx {:backpack backpack})
              (model :backpack/active-cell-tx {:backpack backpack :cell cell})))]
    {:posh/tx tx}))

;; subscribe 

(defmethod base/subscribe :backpack/pull
  [{:keys [pconn]} _ {:keys [entity pattern]}]
  (s/assert :db/entity entity)
  (let [pt (or pattern '[* {:user/_backpack [:db/id]}])]
    (p/pull pconn pt (:db/id entity))))

;; view

(defmethod base/view :backpack/view
  [{:keys [subscribe dispatch]} _ backpack]
  (s/assert :db/entity backpack)
  (let [bp @(subscribe :backpack/pull {:entity backpack})
        user @(subscribe :db/pull {:entity (get-in bp [:user/_backpack 0])})
        active-cell (:backpack/active-cell bp)]
    [:div {:class "d-flex justify-content-center astronomy-backpack"}
     (for [cell (:backpack/cells bp)]
       (let [tool (:backpack-cell/tool cell)
             style (if (= (:db/id active-cell) (:db/id cell))
                     "astronomy-cell astronomy-cell-active"
                     "astronomy-cell")]
         ^{:key (:db/id cell)}
         [:div {:class style
                :onClick #(dispatch :backpack/click-cell {:user user
                                                          :backpack bp
                                                          :cell cell
                                                          :active-cell active-cell})}
          (when tool
            [:img {:src (-> tool :tool/icon)
                   :class "astronomy-button"}])]))]))
