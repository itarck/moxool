(ns methodology.model.core
  (:require
   [methodology.model.entity :as m.entity]
   [methodology.model.gltf :as m.gltf]
   [methodology.model.object :as m.object]
   [methodology.model.scene :as m.scene]))



(def schema
  (merge m.entity/schema
         m.gltf/schema
         m.object/schema
         m.scene/schema))

