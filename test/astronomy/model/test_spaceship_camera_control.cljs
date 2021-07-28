(ns astronomy.model.test-spaceship-camera-control
  (:require
   [cljs.test :refer-macros [deftest is run-tests]]
   [astronomy.model.user.spaceship-camera-control :as m.spaceship]
   [astronomy.data.basic :as d.basic]))


(def scc-1 d.basic/spaceship-camera-control)


(deftest test-spaceship1
  (is (= (m.spaceship/cal-component-props scc-1 :static-mode)
         {:up [0 1 0], :zoom 1, :azimuthRotateSpeed -0.3, :polarRotateSpeed -0.3, :target [2000 2000 2000], :position [2000.2 2000.2 2000.2], :minDistance 0.001, :maxDistance 0.001}))
  (is (= (m.spaceship/cal-component-props scc-1 :orbit-mode)
         {:up [0 1 0], :zoom 1, :azimuthRotateSpeed -0.3, :polarRotateSpeed -0.3, :target [0 0 0], :position [2000 2000 2000], :minDistance 210, :maxDistance 1.466424E22})))


(deftest test-spaceship-tx
  (is (= (m.spaceship/set-mode-tx scc-1 :static-mode)
         [{:db/id -10, :spaceship-camera-control/mode :static-mode}])))



(run-tests)
