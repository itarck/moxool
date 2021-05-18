(ns test.film.model.test-video
  (:require
   [datascript.core :as d]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [cljs.pprint :refer [pprint]]
   [methodology.lib.chest :as chest]
   [film.model.core :refer [basic-db create-basic-conn!]]
   [film.model.video :as m.video :refer [video-sample]]))


(def test-conn (create-basic-conn!))

(deftest test-video1
  (is (= (+ 1 2) 3)))


(run-tests)


(comment

  (->>
   (chest/find-ids-by-attr @test-conn :video/name)
   (chest/pull-many @test-conn))
  
  ;; 
  )