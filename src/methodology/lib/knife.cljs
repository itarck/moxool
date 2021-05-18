(ns methodology.lib.knife
  (:require))


(defn qualified-map
  [m n]
  (reduce-kv (fn [acc k v]
               (let [new-kw (if (and (keyword? k)
                                     (not (qualified-keyword? k)))
                              (keyword (str n) (name k))
                              k)]
                 (assoc acc new-kw v)))
             {} m))


(defn unqualified-map
  [m]
  (reduce-kv (fn [acc k v]
               (let [new-kw (if (and (keyword? k)
                                     (qualified-keyword? k))
                              (keyword (name k))
                              k)]
                 (assoc acc new-kw v)))
             {} m))


(defn jsv-map
  [m]
  (reduce-kv (fn [acc k v]
               (let [new-value (if (or (map? v) (vector? v))
                                 (clj->js v)
                                 v)]
                 (assoc acc k new-value)))
             {} m))

(comment

  (qualified-map {:a 1 :b 2} "abc")

  (unqualified-map (qualified-map {:a 1 :b 2} "abc"))

  (jsv-map {:abc [3]
            :df "sdf"})

;;   
  )