(ns film2.modules.editor.m)


(def schema {:editor/name {:db/unique :db.unique/identity}
             :editor/current-frame {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}
             :editor/current-video {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})


(def editor-1
  #:editor{:name "default"
           :status :init
           :current-frame [:frame/name "/temp/solar-1.fra"]})