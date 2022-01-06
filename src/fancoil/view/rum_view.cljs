(ns fancoil.view.rum-view
  (:require
   [cljs.spec.alpha :as s]
   [integrant.core :as ig]))

;; schema 

(s/def ::view-fn fn?)
(s/def ::env map?)
(s/def ::props map?)
(s/def ::config
  (s/keys :req-un [::view-fn ::props ::env]))


;; view 模块

(defmethod ig/init-key :fancoil/view.rum-view
  [_k config]
  {:pre [(s/valid? ::config config)]}
  (let [{:keys [props view-fn env]} config]
    (view-fn env props)))

(defmethod ig/halt-key! :fancoil/view.rum-view [_k view]
  (println "halt fancoil/rum-view: do nothing"))


(comment
  (def sample #:rum-view
               {:view-fn identity
                :env {:subscribe (ig/ref :todomvc2/subscribe-fn)
                      :dispatch (ig/ref :todomvc2/dispatch-fn)}
                :props {:todolist {:db/id [:todolist/name "default"]}}}))