(ns astronomy.model.atmosphere
  (:require
   [posh.reagent :as p]
   [astronomy.objects.coordinate.m :as m.coordinate]
   [astronomy.objects.horizon-coordinate.m :as hc.m]))


(def schema {:atmosphere/name {:db/unique :db.unique/identity}})


(defn has-day-light? [atmosphere sun-elevation-angle]
  (and
   (:atmosphere/show? atmosphere)
   (>= sun-elevation-angle 0)))

(defn show-atmosphere? [atmosphere sun-elevation-angle]
  (and
   (:atmosphere/show? atmosphere)
   (>= sun-elevation-angle -10)))


(comment
  (def sample-1
    #:atmosphere {:name "default"
                  :show? true
                  :object/scene [:scene/name "solar"]
                  :entity/type :atmosphere})
;;   
  )


(def whole-selector
  '[* 
    {:atmosphere/coordinate [*]}])

(def unique-id
  [:atmosphere/name "default"])

(defn sub-unique-one [conn]
  @(p/pull conn whole-selector unique-id))

(defn sub-show-atmosphere?
  [conn atmosphere]
  (let [atmosphere-1 @(p/pull conn '[* {:object/scene [{:astro-scene/coordinate [*]}]}] (:db/id atmosphere))
        coordinate (get-in atmosphere-1 [:object/scene :astro-scene/coordinate])
        sun-position (m.coordinate/from-system-position-now coordinate [0 0 0])]
    (and
     (= (:coordinate/type coordinate) :horizon-coordinate)
     (:atmosphere/show? atmosphere)
     (>= (hc.m/sun-elevation-angle sun-position) -10))))

(defn sub-has-day-light?
  [conn atmosphere]
  (let [atmosphere-1 @(p/pull conn '[* {:object/scene [{:astro-scene/coordinate [*]}]}] (:db/id atmosphere))
        coordinate (get-in atmosphere-1 [:object/scene :astro-scene/coordinate])
        sun-position (m.coordinate/from-system-position-now coordinate [0 0 0])]
    (and
     (= (:coordinate/type coordinate) :horizon-coordinate)
     (:atmosphere/show? atmosphere)
     (>= (hc.m/sun-elevation-angle sun-position) 0))))