(ns shu.astronomy.test-celestial-coordinate
  (:require
   [shu.three.vector3 :as v3]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [shu.astronomy.celestial-coordinate :as cc]))


(def sample
  #:celestial-coordinate {:longitude 30
                          :latitude 0
                          :radius 1})

(def sample2
  #:celestial-coordinate {:longitude 60
                          :latitude 23
                          :radius 1})


(deftest test-celestial-coordinate
  (is (= (cc/right-ascension sample) 2))
  (is (= (cc/declination sample) 40))

  (is (cc/almost-equal?
       (cc/from-unit-vector (cc/to-unit-vector sample))
       sample))

  (is (v3/almost-equal?
       (v3/normalize (cc/cal-position (cc/celestial-coordinate 30 60) 2500))
       (cc/to-unit-vector (cc/celestial-coordinate 30 60)))))




(run-tests)


(comment 
  
  (let [v (v3/from-seq '(54.659866380199894 163.49055161809267 119.63234325255185))]
    (cc/from-vector v) )
  

  ;; 普通点
  (cc/distance-in-degree (cc/celestial-coordinate 121.21, 19.31) 
                         (cc/celestial-coordinate 135.83, 17.09))
  ;; => 14.060109694233343

  ;; 远日点
  (cc/distance-in-degree (cc/celestial-coordinate -93.22, -21.40)
                         (cc/celestial-coordinate -80.42, -21.93))
  ;; => 11.90414160286232

  (cc/distance-in-degree (cc/celestial-coordinate -47.72, -21.34)
                         (cc/celestial-coordinate -35.33, -18.58))
  ;; => 11.964219687324125

;; 近日点
  (cc/distance-in-degree (cc/celestial-coordinate 114.00, 22.11)
                         (cc/celestial-coordinate 129.84, 20.36))
  ;; => 14.860861721107398

  (/ 14.874936375730885 11.964219687324125)
  ;; => 1.243285125522278

  
  ;; 普通点，偏近日点 月球大小

  (cc/distance-in-degree (cc/celestial-coordinate 105.53, 20.18)
                         (cc/celestial-coordinate 105.69, 20.67))
  ;; => 0.5124275933550734


  ;; 近日点 月球大小

  (cc/distance-in-degree (cc/celestial-coordinate -80.40, -22.21)
                         (cc/celestial-coordinate -80.34, -22.65))
  ;; => 0.44348153911584437

  -80.40, -22.19
  -80.34, -22.66

  
  (cc/from-vector '(93.63720568179536 104.72812072112882 156.39404688984635))
  ;; => #:celestial-coordinate{:longitude 30.910060054274055, :latitude 29.878937704799164, :radius 210.22607702247035}

  

  ;; 
  )