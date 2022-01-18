(ns laboratory.dbs.dev
  (:require
   [datascript.core :as d]
   [laboratory.system :as sys]))


(def schema
  (::sys/schema (sys/init {})))


(defn create-dev-db1 []
  (let [system (sys/init {} [::sys/process])
        {::sys/keys [process model pconn]} system
        tx [(model :framework/create {})
            (model :scene/create {:scene/background "black"})
            (model :user/create {})
            (model :backpack/create {:backpack/cells
                                     (for [i (range 10)]
                                       {:backpack-cell/index i})})
            (model :object/create #:object{:scale [1 1 5]})
            (model :object/create #:object{:position [3 0 0]
                                           :scale [3 3 3]})
            (model :tool/create #:tool{:db/id -1
                                       :name "universe tool"
                                       :chinese-name "宇宙"
                                       :icon "image/moxool/universe.webp"})
            (model :tool/create #:tool{:db/id -2
                                       :name "clock tool"
                                       :chinese-name "时钟"
                                       :icon "image/moxool/clock.jpg"})]]
    (d/transact! pconn tx)
    (process :backpack/put-tools-in
             {:request/body {:backpack {:db/id [:backpack/name "default"]}
                             :tools [{:db/id [:tool/name "universe tool"]}
                                     {:db/id [:tool/name "clock tool"]}]}})
    @pconn))


(comment 
  (create-dev-db1)
  )
