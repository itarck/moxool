(ns shu.goog.matrix
  (:refer-clojure :exclude [contains?])
  (:require
   [goog.math :as gmath]
   [shu.goog.vec2 :as vec2]
   [shu.goog.vec3 :as vec3])
  (:import goog.math.Matrix))


(defprotocol IMatrix
  (add [this] [this m])
  (append-columns [this m])
  (append-rows [this m])
  (equals [this m] [this m opt_tolerance])
  (determinant [this])
  (inverse [this])
  (reduced-row-echelon-form [this])
  (size [this])
  (transpose [this])
  (get-value-at [this i j])
  (square? [this])
  (multiply [this m])
  (multiply-vec [this v])
  ;; (set-value-at [this i j value])
  (subtract [this m])
  (to-js-array [this])
  (to-string [this]))

(extend-type Matrix

  ISeqable
  (-seq [this] (js->clj (.toArray this)))

  ISeq
  (-first [this] (first (seq this)))
  (-rest [this] (rest (seq this)))

  IMatrix
  (add [this m] (.add this m))
  (append-columns [this m] (.appendColumns this m))
  (append-rows [this m] (.appendRows this m))
  (equals ([this m] (.equals this m))
    ([this m opt_tolerance] (.equals this m opt_tolerance)))
  (determinant [this] (.getDeterminant this))
  (inverse [this] (.getInverse this))
  (reduced-row-echelon-form [this] (.getReducedRowEchelonForm this))
  (size [this] (let [s (.getSize this)]
                 [(.-height s) (.-width s)]))
  (transpose [this] (.getTranspose this))
  (get-value-at [this i j] (.getValueAt this i j))
  (square? [this] (.isSquare this))
  (multiply [this m] (.multiply this m))
  (multiply-vec [this v]
    (let [m1 (Matrix. (clj->js (map vector v)))
          vct (->>
               (seq (multiply this m1))
               (apply concat))]
      (case (count vct)
        2 (apply vec2/vector2 vct)
        3 (apply vec3/vector3 vct))))
  ;; (set-value-at [this i j value] (.setValueAt this i j value))
  (subtract [this m] (.subtract this m))
  (to-js-array [this] (.toArray this))
  (to-string [this] (.toString this)))

(defn matrix [v]
  (Matrix. (clj->js v)))

(defn create-identity [n]
  (.createIdentityMatrix Matrix n))

