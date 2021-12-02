(ns film2.data.iovideo
  (:require
   [film2.data.ioframe :refer [mini-1]]))


(def mini
  #:iovideo {:name "mini move"
             :start-timestamp 124234
             :stop-timestamp 534543
             :total-time 3000
             :initial-ioframe mini-1
             :tx-logs [{:relative-time 1000
                        :tx-data [#:camera{:name "default"
                                           :position [40000 40000 40000]
                                           :quaternion [0 0 0 1]}]}
                       {:relative-time 2000
                        :tx-data [#:camera{:name "default"
                                           :position [80000 80000 80000]
                                           :quaternion [0 0 0 1]}]}]})
