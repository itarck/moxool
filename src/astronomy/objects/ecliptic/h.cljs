(ns astronomy.objects.ecliptic.h
  (:require
   [astronomy.service.effect :as s.effect :refer [create-effect]]))


;; handle-event version

(defmulti handle-event (fn [_props _env event] (:event/action event)))

(defmethod handle-event :ecliptic/change-show
  [_props _env {:event/keys [detail]}]
  (let [{:keys [ecliptic show?]} detail
        tx [{:db/id (:db/id ecliptic)
             :ecliptic/show? show?}]]
    (create-effect :tx tx)))