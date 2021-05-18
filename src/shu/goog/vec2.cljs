(ns shu.goog.vec2
;;   (:refer-clojure :exclude [contains?])
  (:require
   [applied-science.js-interop :as j]
   [goog.math :as gmath])
  (:import goog.math.Vec2))


(defprotocol IVec2
  (add [this] [this b])
  ;; (ceil [this])
  (clone' [this])
  (equals [this other])
  ;; (floor [this])
  (invert [this])
  (magnitude [this])
  (normalize [this])
  ;; (rotate [this angle])
  (rotate-degrees [this degrees] [this degrees opt_center])
  (rotate-radians [this radians] [this radians opt_center])
  ;; (round [this])
  (scale [this sx] [this sx opt_sy])
  (squared-magnitude [this])
  (subtract [this b])
  (to-string [this])
  (translate [this tx] [this tx opt_ty]))


(extend-type Vec2

  ISeqable
  (-seq [v] (list (.-x v) (.-y v)))

  ISeq
  (-first [v] (.-x v))
  (-rest [v] (list (.-y v)))

  ILookup
  (-lookup
    ([v k] (-lookup v k nil))
    ([v k not-found]
     (case k
       0 (.-x v)
       1 (.-y v)
       :x (.-x v)
       :y (.-y v)
       not-found)))

  IVec2
  (add ([this] this)
    ([this b]
     (let [that (clone' this)]
       (.add that b))))
  ;; (ceil [this] (let [that (clone' this)] (j/call that :ceil)))
  (clone' [this] (.clone this))

  ;; bug with (.equals this other)
  ;; (equals [this other] (.equals this other))
  (equals [this other] (< (magnitude (subtract this other)) 1e-10))

  ;; (floor [this] (let [that (clone' this)] (j/call that :floor)))
  (invert [this] (let [that (clone' this)] (j/call that :invert)))
  (magnitude [this] (.magnitude this))
  (normalize [this] (let [that (clone' this)] (j/call that :normalize)))

  ;; mutable
  ;; (rotate [this angle] (.rotate this angle))

  (rotate-degrees
    ([this degrees]
     (let [that (Vec2. (:x this) (:y this))]
       (.rotateDegrees that degrees)
       that))
    ([this degrees opt_center]
     (let [that (Vec2. (:x this) (:y this))]
       (.rotateDegrees that degrees opt_center)
       that)))

  (rotate-radians
    ([this radians]
     (let [that (Vec2. (:x this) (:y this))]
       (.rotateRadians that radians)
       that))
    ([this radians opt_center]
     (let [that (Vec2. (:x this) (:y this))]
       (.rotateRadians that radians opt_center)
       that)))

  ;; (round [this] (.round this))
  (scale
    ([this sx]
     (let [that (clone' this)]
       (.scale that sx)))
    ([this sx opt_sy]
     (let [that (clone' this)]
       (.scale that sx opt_sy))))
  (squared-magnitude [this] (.squaredMagnitude this))
  (subtract [this b] (let [that (.clone this)]
                       (.subtract that b)))
  (to-string [this] (.toString this))
  (translate
    ([this tx]
     (let [that (clone' this)]
       (j/call that :translate tx)))
    ([this tx opt_ty]
     (let [that (clone' this)]
       (j/call that :translate tx opt_ty)))))


(defn vector2 [x y] (Vec2. x y))

(defn determinant [a b] (Vec2.determinant a b))
;; (defn difference [a b] (Vec2.difference a b))
(defn distance [a b] (Vec2.distance a b))
(defn dot [a b] (Vec2.dot a b))
;; (defn from-coordinate [a] (Vec2.fromCoordinate a))
(defn lerp [a b x] (Vec2.lerp a b x))
(defn random [] (Vec2.random))
(defn random-unit [] (Vec2.randomUnit))
;; (defn rescaled [a sx sy] (Vec2.rescaled a sx sy))
;; (defn rotate-aroundPoint [v axisPoint angle] (Vec2.rotateAroundPoint v axisPoint angle))
;; (defn squared-distance [a b] (Vec2.squaredDistance a b))
;; (defn sum [a b] (Vec2.sum a b))

