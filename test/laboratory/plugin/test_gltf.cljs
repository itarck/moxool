(ns laboratory.plugin.test-gltf
  (:require 
   [laboratory.app.playground :as app]))



;; view 


(comment
  (let [tx [#:framework{:name "default"
                        :scene {:scene/name ::scene}}
            {:gltf/url "models/3-cityscene_kyoto_1995/scene.gltf"
             :object/type :gltf
             :object/scale [0.001 0.001 0.001]
             :object/scene [:scene/name ::scene]}]]
    (app/app-transact! tx))
  )