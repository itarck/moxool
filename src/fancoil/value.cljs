(ns fancoil.value
  (:require 
   [integrant.core :as ig]))



;; 值模块，直接返回config

(defmethod ig/init-key :fancoil/value [_ config]
  config)
