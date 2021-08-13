(ns astronomy.objects.ecliptic.m
  (:require
   [posh.reagent :as p]))


(def ecliptic-1
  #:ecliptic {:show? true
              :object/scene [:scene/name "solar"]
              :entity/name "ecliptic"
              :entity/chinese-name "黄道"
              :entity/type :ecliptic})

;; data

(def unique-id [:entity/name "ecliptic"])

;; sub

(defn sub-unique-one [conn]
  @(p/pull conn '[*] unique-id))