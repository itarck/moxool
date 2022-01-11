(ns laboratory.dbs.dev
  (:require
   [datascript.core :as d]
   [fancoil.base :as base]
   [laboratory.system.zero :as zero]))


(def schema
  (::zero/schema (zero/init {})))


(defn create-dev-db1 []
  (let [conn (d/create-conn schema)
        tx  [(base/model {} :framework/create {})
             (base/model {} :scene/create {:scene/background "black"})
             (base/model {} :user/create {})
             (base/model {} :backpack/create {:backpack/cell [{:backpack-cell/index 0
                                                               :backpack-cell/tool -1}
                                                              {:backpack-cell/index 1
                                                               :backpack-cell/tool -2}]})
             (base/model {} :object/create #:object{:scale [1 1 5]})
             (base/model {} :object/create #:object{:position [3 0 0]
                                                    :scale [3 3 3]})
             (base/model {} :tool/create #:tool{:db/id -1
                                                :type :universe-tool
                                                :name "universe tool"
                                                :chinese-name "宇宙"
                                                :icon "image/moxool/universe.webp"})
             (base/model {} :tool/create #:tool{:db/id -2
                                                :type :clock-tool
                                                :name "clock tool"
                                                :chinese-name "时钟"
                                                :icon "image/moxool/clock.jpg"})]]
    (d/transact! conn tx)
    @conn))


(comment 
  (create-dev-db1)
  )