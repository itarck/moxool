(ns astronomy2.plugin.test-planet
  (:require
   [cljs.pprint :refer [pprint]]
   [cljs.test :refer-macros [deftest testing is run-tests]]
   [astronomy2.test-helper :refer [test-db spec model subscribe homies]]))


;; subscribe

(def sun
  @(subscribe :star/pull {:entity {:db/id [:star/name "sun"]}}))

(def earth
  @(subscribe :planet/pull {:entity {:db/id [:planet/name "earth"]}}))

@(subscribe :planet/sub-planets-with-chinese-names)
;; => #{[10 "地球"]}

(pprint sun)

(pprint earth)

;; spec

(deftest test-spec
  (is (spec :valid? :planet/planet earth)))


;; view

(comment

  (do
    (def tx
      [#:scene {:name ::scene
                :background "black"
                :type :astro-scene
                :scale [3 3 3]}
       #:framework{:name "default"
                   :scene [:scene/name ::scene]}
       sun])

    (homies :transact! tx))

;; 
  )


(run-tests)