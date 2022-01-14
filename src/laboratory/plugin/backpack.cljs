(ns laboratory.plugin.backpack
  (:require
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
   :backpack/cell {:db/valueType :db.type/ref
                   :db/cardinality :db.cardinality/many
                   :db/isComponent true}
   :backpack-cell/tool {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

;; spec


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

(defmethod base/model :backpack/find-nth-cell2
  [_ _ {:keys [db backpack nth-cell]}]
  (let [cell-id (d/q '[:find ?cell-id .
                       :where
                       [?bp-id :backpack/cell ?cell-id]
                       [?cell-id :backpack-cell/index ?nth-cell]
                       :in $ ?bp-id ?nth-cell]
                     db (:db/id backpack) nth-cell)]
    (when cell-id
      {:db/id cell-id})))

(defmethod base/model :backpack/put-in-cell-tx
  [_ _ {:keys [backpack nth-cell tool]}]
  (let [cell (base/model {} :backpack/find-nth-cell {:backpack backpack
                                                     :nth-cell nth-cell})]
    [[:db/add (:db/id cell) :backpack-cell/tool (:db/id tool)]]))

(defmethod base/model :backpack/active-cell-tx
  [_ _ {:keys [backpack cell]}]
  [[:db/add (:db/id backpack) :backpack/active-cell (:db/id cell)]])

(defmethod base/model :backpack/deactive-cell-tx
  [_ _ {:keys [backpack]}]
  [[:db.fn/retractAttribute (:db/id backpack) :backpack/active-cell]])

(defmethod base/model :backpack/put-in-backpack-tx
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


;; handle 

#_(defmethod base/handle :backpack/click-cell
  [{:keys [model]} _ {:request/keys [body]}]
  (let [{:keys [user cell active-cell backpack]} body
        tx (if (= (:db/id active-cell) (:db/id cell))
             (concat
              (model :backpack/deactive-cell-tx {:backpack backpack})
              (model :user/drop-tool-tx {:user user}))
             (concat
              (model :backpack/active-cell-tx {:backpack backpack
                                               :cell cell})
              (model :user/select-tool-tx {:user user
                                           :tool (:backpack-cell/tool cell)})))]
    {:posh/tx tx}))

(defmethod base/handle :backpack/click-cell
  [{:keys [model]} _ {:request/keys [body] db :pconn/db}]
  (let [{:keys [backpack cell]} body
        backpack (d/pull db '[:backpack/active-cell] (:db/id backpack))
        active-cell (:backpack/active-cell backpack)
        tx (if (= (:db/id active-cell) (:db/id cell))
             (model :backpack/deactive-cell-tx {:backpack backpack})
             (concat
              (model :backpack/deactive-cell-tx {:backpack backpack})
              (model :backpack/active-cell-tx {:backpack backpack :cell cell})))]
    {:posh/tx tx}))

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
        user @(subscribe :db/pull {:id (get-in bp [:user/_backpack 0 :db/id])})
        active-cell (:backpack/active-cell bp)]
    [:div {:class "d-flex justify-content-center astronomy-backpack"}
     (for [cell (:backpack/cell bp)]
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
