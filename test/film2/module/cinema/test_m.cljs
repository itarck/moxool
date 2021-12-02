(ns film2.module.cinema.test-m
  (:require
   [film2.db.studio :as db.studio]
   [film2.modules.cinema.m :as cineme.m]))


(def db
  db.studio/simple-db)


(cineme.m/pull-all db [:cinema/name "default"])