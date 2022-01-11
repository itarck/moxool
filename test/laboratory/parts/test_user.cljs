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
      (is (= (model :user/select-tool-tx {:user {:db/id -1}
                                          :tool {:db/id -2}})
             [[:db/add -1 :user/right-tool -2]]))
      (is (= (model :user/drop-tool-tx {:user {:db/id -1}})
             [[:db.fn/retractAttribute -1 :user/right-tool]])))))


(run-tests)


(comment

  (let [spec (test-system/create-spec-unit)]
    (spec :valid? :user/backpack {:db/id 34}))


  (let [model (test-system/create-model-unit)]
    (model :user/select-tool-tx {:user {:db/id 1}
                                 :tool {:db/id 2}}))

)