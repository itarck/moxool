(ns fancoil.db.atom
  (:require
   [cljs.spec.alpha :as s]
   [integrant.core :as ig]))

;; schema

(s/def ::initial-value (constantly true))
(s/def ::config 
       (s/keys :opt-un [::initial-value]))


;; atom 模块：用的是clojure atom，用来存一些数据
;; config: {:initial-value {:a 1}}
;; instance:  => #object[cljs.core.Atom {:val {:a 1}}]


(defmethod ig/init-key :fancoil/db.atom
  [_k config]
  {:pre [(s/valid? ::config config)]}
  (let [{:keys [initial-value]} config]
    (atom initial-value)))

(defmethod ig/halt-key! :fancoil/db.atom [_k value]
  (println "halt fancoil/atom!"))

