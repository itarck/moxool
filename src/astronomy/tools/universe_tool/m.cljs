(ns astronomy.tools.universe-tool.m
  (:require
   [posh.reagent :as p]))


(def schema {:universe-tool/astro-scene {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})


(comment

  (def universe-tool-1
    #:universe-tool{:astro-scene [:scene/name "solar"]
                    :tool/name "universe tool"
                    :tool/chinese-name "宇宙工具"
                    :tool/icon "/image/moxool/universe.webp"

                    :entity/type :universe-tool})
  ;; 
  )

