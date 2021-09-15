(ns astronomy.space.selector.m)


(def sample
  {:selector/query '[:find [(pull ?id [:db/id :planet/name]) ...]
                     :where [?id :planet/name ?name]]
   :selector/selected [:planet/name "earth"]
   :tool/name "planet-tool"
   :tool/chinese-name "行星"
   :tool/icon "/image/pirate/earth.jpg"
   :tool/type :selector-tool
   :entity/type :selector-tool})


(def schema 
  {:selector/candinates {:db/valueType :db.type/ref :db/cardinality :db.cardinality/many}
   :selector/selected {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})