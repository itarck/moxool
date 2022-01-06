(ns fancoil.db.ratom
  (:require
   [integrant.core :as ig]
   [reagent.core :as r]))

;; atom 模块：用的是reagent atom，view也可以自动响应
;; config: {:init-value {:a 1}}
;; instance:  ;; => #object[reagent.ratom.RAtom {:val {:a 1}}]


(defmethod ig/init-key :fancoil/db.ratom [_k config]
  (let [{:keys [initial-value]} config]
    (r/atom initial-value)))

(defmethod ig/halt-key! :fancoil/db.ratom [_k value]
  (println "halt fancoil/ratom!"))