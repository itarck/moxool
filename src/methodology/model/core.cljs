(ns methodology.model.core
  (:require
   [methodology.model.entity :as m.entity]
   [methodology.model.gltf :as m.gltf]
   [methodology.model.object :as m.object]
   [methodology.model.scene :as m.scene]
   [methodology.model.camera :as m.camera]
   [methodology.model.user.tool :as m.tool]))



(def schema
  (merge m.entity/schema
         m.gltf/schema
         m.object/schema
         m.scene/schema
         m.camera/schema
         m.tool/schema))

