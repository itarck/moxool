(ns astronomy2.db
  (:require
   [datascript.core :as d]
   [astronomy2.system :as sys]))


(def temp-sys
  (sys/init {}))

(def schema
  (::sys/schema temp-sys))

(def model
  (::sys/model temp-sys))

(defmulti create-db
  (fn [db-type] db-type))

(defmethod create-db :basic
  [_]
  (let [conn (d/create-conn schema)
        tx [#:scene {:name "default"
                     :background "black"
                     :type :astro-scene
                     :scale [1 1 1]}
            #:object {:scene [:scene/name "default"]
                      :type :box
                      :position [0 0 0]
                      :rotation [0 0 0]
                      :scale [1 1 1]
                      :box/color "blue"}
            #:framework{:name "default"
                        :scene [:scene/name "default"]}]]
    (d/transact! conn tx)
    @conn))

(defmethod create-db :simple
  [_]
  (let [conn (d/create-conn schema)
        tx [(model :framework/create {})
            (model :scene/create {:scene/background "black"})
            (model :user/create {})
            (model :backpack/create {:backpack/cells [{:backpack-cell/index 0
                                                       :backpack-cell/tool -1}
                                                      {:backpack-cell/index 1
                                                       :backpack-cell/tool -2}]})
            (model :object/create #:object{:scale [1 1 5]})
            (model :object/create #:object{:scale [1 1 1]
                                           :position [-3 0 0]})
            (model :tool/create #:tool{:db/id -1
                                       :name "universe tool"
                                       :chinese-name "宇宙"
                                       :icon "image/moxool/universe.webp"})
            (model :tool/create #:tool{:db/id -2
                                       :name "clock tool"
                                       :chinese-name "时钟"
                                       :icon "image/moxool/clock.jpg"})]]
    (d/transact! conn tx)
    @conn))


(defmethod create-db :test-db
  [_]
  (let [conn (d/create-conn schema)
        tx [(model :framework/create {})
            (model :scene/create {:scene/background "black"})
            (model :user/create {})
            (model :backpack/create {:backpack/cells [{:backpack-cell/index 0
                                                       :backpack-cell/tool -1}
                                                      {:backpack-cell/index 1
                                                       :backpack-cell/tool -2}]})
            (model :star/create {:star/name "sun"
                                 :gltf/url "models/16-solar/Sun_1_1391000.glb"
                                 :gltf/scale [0.002 0.002 0.002]
                                 :object/scene [:scene/name "default"]})

            (model :planet/create #:planet
                                   {:name "earth"
                                    :star [:star/name "sun"]
                                    :gltf/url "models/11-tierra/scene.gltf"
                                    :gltf/scale [0.2 0.2 0.2]
                                    :object/scene {:db/id [:scene/name "default"]}
                                    :object/position [0 0 100]})
            (model :tool/create #:tool{:db/id -1
                                       :name "universe tool"
                                       :chinese-name "宇宙"
                                       :icon "image/moxool/universe.webp"})
            (model :tool/create #:tool{:db/id -2
                                       :name "clock tool"
                                       :chinese-name "时钟"
                                       :icon "image/moxool/clock.jpg"})]]
    (d/transact! conn tx)
    @conn))


(comment 
  (create-db :test-db)
  )