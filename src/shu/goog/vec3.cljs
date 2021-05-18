(ns shu.goog.vec3
  (:require
   [applied-science.js-interop :as j]
   [goog.math :as gmath])
  (:import goog.math.Vec3))

;; this.add (b) → goog.math.Vec3
;; this.clone () → goog.math.Vec3
;; this.equals (b) → boolean
;; this.invert () → goog.math.Vec3
;; this.magnitude () → number
;; this.normalize () → goog.math.Vec3
;; this.scale (s) → goog.math.Vec3
;; this.squaredMagnitude () → number
;; this.subtract (b) → goog.math.Vec3
;; this.toArray () → Array<number>
;; this.toString () → string

(defprotocol IVec3
  (add [this] [this b])
  (clone' [this])
  (equals [this other])
  (invert [this])
  (magnitude [this])
  (normalize [this])
  (scale [this s])
  (squared-magnitude [this])
  (subtract [this b])
  (to-string [this]))


(extend-type Vec3

  ISeqable
  (-seq [v] (list (.-x v) (.-y v) (.-z v)))

  ISeq
  (-first [v] (.-x v))
  (-rest [v] (list (.-y v) (.-z v)))

  ILookup
  (-lookup
    ([v k] (-lookup v k nil))
    ([v k not-found]
     (case k
       0 (.-x v)
       1 (.-y v)
       2 (.-z v)
       :x (.-x v)
       :y (.-y v)
       :z (.-z v)
       not-found)))

  IVec3
  (add ([this] this)
    ([this b]
     (let [that (clone' this)]
       (.add that b))))
  (clone' [this] (.clone this))
  (equals [this other] (.equals this other))
  (invert [this] (j/call (clone' this) :invert))
  (magnitude [this] (.magnitude this))
  (normalize [this] (let [that (clone' this)]
                      (.normalize that)))
  (scale [this sx] (let [that (clone' this)]
                     (.scale that sx)))
  (squared-magnitude [this] (.squaredMagnitude this))
  (subtract [this b] (let [that (.clone this)]
                       (.subtract that b)))
  (to-string [this] (.toString this)))


(defn vector3 [x y z] (Vec3. x y z))

(defn vec3 [v1]
  (let [[x y z] v1]
    (Vec3. x y z)))

;; Vec3.cross (a, b) → goog.math.Vec3
;; Vec3.difference (a, b) → goog.math.Vec3
;; Vec3.distance (a, b) → number
;; Vec3.dot (a, b) → number
;; Vec3.equals (a, b) → boolean
;; Vec3.fromCoordinate3 (a) → goog.math.Vec3
;; Vec3.lerp (a, b, x) → goog.math.Vec3
;; Vec3.random () → goog.math.Vec3
;; Vec3.randomUnit () → goog.math.Vec3
;; Vec3.rescaled (a, s) → goog.math.Vec3
;; Vec3.squaredDistance (a, b) → number
;; Vec3.sum (a, b) → goog.math.Vec3

(defn cross [a b] (Vec3.cross a b))
(defn difference [a b] (Vec3.difference a b))
(defn distance [a b] (Vec3.distance a b))
(defn dot [a b] (Vec3.dot a b))
(defn from-coordinate3 [a] (Vec3.fromCoordinate3 a))
(defn lerp [a b x] (Vec3.lerp a b x))
(defn random [] (Vec3.random))
(defn random-unit [] (Vec3.randomUnit))
(defn rescaled [a s] (Vec3.rescaled a s))
(defn squared-distance [a b] (Vec3.squaredDistance a b))
(defn sum [a b] (Vec3.sum a b))

