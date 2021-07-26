(ns astronomy.service.test-horizon-coordinate-tool
  (:require
   [cljs.test :refer-macros [deftest is run-tests]]
   [posh.reagent :as p]
   [astronomy.scripts.test-conn :as test-conn]
   [astronomy.service.horizon-coordinate-tool :as s.horizon-coordinate-tool]))


(def conn (test-conn/init-conn!))

(def props {})

(def env
  {:conn conn})


(deftest test-horizon-coordinate-tool
  (is (= false
         (let [event1 #:event {:action :horizon-coordinate/change-show-latitude
                               :detail {:horizon-coordinate {:db/id [:coordinate/name "地平坐标系"]}
                                        :show? false}}
               _ (s.horizon-coordinate-tool/handle-event! {} {:conn conn} event1)
               hc @(p/pull conn '[*] [:coordinate/name "地平坐标系"])]
           (:horizon-coordinate/show-latitude? hc)))))


(run-tests)