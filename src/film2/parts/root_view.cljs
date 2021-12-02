(ns film2.parts.root-view
  (:require
   [integrant.core :as ig]
   [film2.modules.studio.v :as studio.v]
   [film2.modules.cinema.v :as cinema.v]))


(defmethod ig/init-key :studio/view [_k config]
  (let [{:keys [props env]} config]
    [studio.v/StudioView props env]))


(defmethod ig/init-key :cinema/view [_k config]
  (let [{:keys [props env]} config]
    [cinema.v/CinemaView props env]))