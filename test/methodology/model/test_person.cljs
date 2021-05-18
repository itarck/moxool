(ns methodology.model.test-person
  (:require
   [posh.reagent :as p]
   [astronomy.test-conn :refer [create-poshed-conn!]]
   [methodology.model.user.backpack :as m.bp]))


(def test-conn (create-poshed-conn!))


(def person1 @(p/pull test-conn '[*] [:person/name "dr who"]))

person1


@(p/pull test-conn '[* {:tool/_backpack [*]}]
         (-> person1 :person/backpack :db/id))
;; => {:db/id 17, :backpack/name "default", :backpack/owner #:db{:id 16}, :tool/_backpack [{:db/id 18, :clock-tool/current-time 0, :clock-tool/days-per-step 0.041666666666666664, :clock-tool/start-time 0, :clock-tool/status :stop, :clock-tool/steps-per-second 50, :entity/type :clock-tool, :tool/backpack #:db{:id 17}}]}
