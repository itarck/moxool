(ns laboratory.base
  (:require 
   [fancoil.base :as base]
   [fancoil.module.posh.base :as posh.base]))


(def schema 
  posh.base/schema)

(def spec 
  base/spec)

(def model 
  base/model)

(def handle 
  base/handle)

(def inject 
  base/inject)

(def do!
  base/do!)

(def process
  base/process)

(def subscribe 
  base/subscribe)

(def view 
  base/view)

(def schedule 
  base/schedule)
