(ns astronomy2.plugin.test-star
  (:require
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [astronomy2.test-helper :refer [spec model subscribe homies]]))


;; subscribe

(def sun
  @(subscribe :star/pull {:entity {:db/id [:star/name "sun"]}}))

sun
;; => {:db/id 9, :gltf/scale [0.002 0.002 0.002], :gltf/url "models/16-solar/Sun_1_1391000.glb", :object/scene #:db{:id 2}, :object/type :star, :star/name "sun", :planet/_star [{:db/id 10, :gltf/scale [0.2 0.2 0.2], :gltf/url "models/11-tierra/scene.gltf", :object/position [0 0 100], :object/scene #:db{:id 2}, :object/type :planet, :planet/name "earth", :planet/star #:db{:id 9}}]}

;; spec 

(deftest test-spec
  (is (spec :valid? :star/star sun))
  (is (spec :valid? :planet/_star (:planet/_star sun))))


;; model 

(deftest test-model
  (testing "testing model"
    (is (= (model :star/create {:gltf/url "models/16-solar/Sun_1_1391000.glb"
                                :object/scene [:scene/name ::scene]})
           {:object/type :star, :gltf/url "models/16-solar/Sun_1_1391000.glb"
            :object/scene [:scene/name :astronomy2.plugin.test-star/scene]}))))

;; view

(comment
  (homies :transact! [(assoc sun :object/scene [:scene/name "default"])]))


(run-tests)