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

(defn create-basic-db []
  (let [conn (d/create-conn schema)
        tx [#:scene {:name ::basic
                     :background "black"
                     :type :astro-scene
                     :scale [1 1 1]}
            #:object {:scene [:scene/name ::basic]
                      :type :box
                      :position [0 0 0]
                      :rotation [0 0 0]
                      :scale [1 1 1]
                      :box/color "blue"}
            #:framework{:name "default"
                        :scene [:scene/name ::basic]}]]
    (d/transact! conn tx)
    @conn))

(defn create-db []
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


(comment 
  (create-db)
  
  )