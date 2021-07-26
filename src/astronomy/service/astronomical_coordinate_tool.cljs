(ns astronomy.service.astronomical-coordinate-tool
  (:require
   [astronomy.model.astro-scene :as m.astro-scene]
   [astronomy.model.astronomical-coordinate :as m.astronomical-coordinate]
   [astronomy.model.user.astronomical-coordinate-tool :as m.astronomical-coordinate-tool]
   [astronomy.service.effect :as s.effect :refer [create-effect]]))


(defmulti parse-event (fn [action detail props db] action))

(defmethod parse-event :astronomical-coordinate-tool/log
  [_ detail props db]
  #:effect {:type :log :detail detail})

(defmethod parse-event :astronomical-coordinate-tool/change-show-longitude
  [_ detail props db]
  (let [{:keys [astronomical-coordinate show?]} detail]
    (create-effect :tx (m.astronomical-coordinate/change-show-longitude-tx astronomical-coordinate show?))))

(defmethod parse-event :astronomical-coordinate-tool/change-show-latitude
  [_ detail props db]
  (let [{:keys [astronomical-coordinate show?]} detail]
    (create-effect :tx (m.astronomical-coordinate/change-show-latitude-tx astronomical-coordinate show?))))

(defmethod parse-event :astronomical-coordinate-tool/set-scene-reference
  [_ detail props db]
  (let [{:keys [astronomical-coordinate]} detail
        astro-scene (get-in props [:astro-scene])]
    (create-effect :tx (m.astro-scene/set-scene-coordinate-tx astro-scene astronomical-coordinate))))

(defmethod parse-event :astronomical-coordinate-tool/change-query-args
  [_ detail props db]
  (let [{:keys [tool query-args]} detail]
    (create-effect :tx (m.astronomical-coordinate-tool/update-query-args-tx db tool query-args))))

(defmethod parse-event :astronomical-coordinate-tool/change-show-latitude-0
  [_ detail props db]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-latitude-0? show?}]]
    (create-effect :tx tx)))

(defmethod parse-event :astronomical-coordinate-tool/change-show-regression-line
  [_ detail props db]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-regression-line? show?}]]
    (create-effect :tx tx)))


(defmethod parse-event :astronomical-coordinate-tool/change-show-longitude-0
  [_ detail props db]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-longitude-0? show?}]]
    (create-effect :tx tx)))

(defmethod parse-event :astronomical-coordinate-tool/change-show-ecliptic
  [_ detail props db]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-ecliptic? show?}]]
    (create-effect :tx tx)))

(defmethod parse-event :astronomical-coordinate-tool/change-show-lunar-orbit
  [_ detail props db]
  (let [{:keys [astronomical-coordinate show?]} detail
        tx [{:db/id (:db/id astronomical-coordinate)
             :astronomical-coordinate/show-lunar-orbit? show?}]]
    (create-effect :tx tx)))






