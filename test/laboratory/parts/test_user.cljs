(ns laboratory.parts.test-user
  (:require
   [cljs.test :refer-macros [deftest is are testing run-tests]]
   [laboratory.test-system :as test-system]))


(def user-sample
  {:db/id -1
   :user/name "dr who"
   :user/backpack {:db/id -34}
   :user/right-tool {:db/id -100}})

(deftest test-spec-unit
  (let [spec (test-system/create-spec-unit)]
    (testing "testing spec unit"
      (is (spec :valid? :db/id 345)))))

(deftest test-model-unit
  (let [model (test-system/create-model-unit)]
    (testing "testing model unit"
      (is (model :user/select-tool-tx {:user user-sample
                                       :tool-id -2})
          [[:db/add -1 :user/right-tool -2]]))))


(run-tests)


(comment 
  
  (def model (test-system/create-model-unit))

  (model :user/create {})
  ;; => {:user/name "default", :framework/_user [:framework/name "default"]}

  (model :user/select-tool-tx {:user user-sample
                               :tool-id -2})
  ;; => [[:db/add -1 :user/right-tool -2]]


  
  )