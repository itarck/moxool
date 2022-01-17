(ns laboratory.plugin.object
  (:require
   [cljs.spec.alpha :as s]
   [laboratory.base :as base]
   [applied-science.js-interop :as j]
   ["three" :as three]
   ["@react-three/drei" :refer [Box]]))


;; data 

(def sample 
  #:object {:position [0 0 0]
            :rotation [0 0 0]
            :scale [1 1 1]
            :scene {:db/id [:scene/name "default"]}})

;; schema

(defmethod base/schema :object/schema
  [_ _]
  {:object/name {:db/unique :db.unique/identity}
   :object/scene {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})

;; spec


(defmethod base/spec :object/spec
  [_ _]
  (base/spec {} :entity/spec)
  (s/def :object/name string?)
  (s/def :object/position (s/tuple number? number? number?))
  (s/def :object/rotation (s/tuple number? number? number?))
  (s/def :object/scale (s/tuple number? number? number?))
  (s/def :object/scene :entity/entity)
  (s/def :object/object (s/keys :req [:db/id :object/position :object/rotation :object/scale])))

;; model 

(defmethod base/model :object/create
  [_ _ props]
  (let [default #:object {:position [0 0 0]
                          :rotation [0 0 0]
                          :scale [1 1 1]
                          :scene [:scene/name "default"]}]
    (merge default props)))

;; view 

(defmethod base/view :object/view
  [{:keys [subscribe] :as core} _method object]
  (let [object @(subscribe :entity/pull {:entity object})
        detail-type (keyword (:object/type object) "view")]
    (if (:object/type object)
      [base/view core detail-type object]
      [:> Box {:on-click (fn [e]
                           (let [inter (j/get-in e [:intersections 0 :point])]
                             (js/console.log "box click" inter)))
               :args [1 1 1]
               :position (:object/position object)
               :rotation (:object/rotation object)
               :scale (:object/scale object)}
       [:meshStandardMaterial {:color "red"
                               :side three/DoubleSide
                               :opacity 0.5
                               :transparent true}]])))


(comment 
  
  (s/def :object/position (s/cat :x number? :y number? :z number?))
  (s/valid? :object/position [3 3 -34])
  
  )