(ns test.film.model.test-editor
  (:require
   [datascript.core :as d]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [cljs.pprint :refer [pprint]]
   [film.model.core :refer [basic-db create-basic-conn!]]
   [film.model.video :as m.video]
   [film.model.editor :as m.editor]))


(def test-conn (create-basic-conn!))

(def editor (m.editor/pull-one @test-conn [:editor/name "default"]))


(deftest test-record
  (let [test-conn (create-basic-conn!)
        scene-conn (let [conn (d/create-conn {})]
                     (d/transact! conn [{:user/name "user1"}])
                     conn)]

    (m.editor/create-video! test-conn [:editor/name "default"])
    (m.editor/start-record! test-conn scene-conn [:editor/name "default"])

    (d/transact! scene-conn [{:user/name "user2"}])
    (d/transact scene-conn [{:user/name "user4"}])

    (m.editor/stop-record! test-conn scene-conn [:editor/name "default"])

    (let [editor (m.editor/pull-one @test-conn [:editor/name "default"])
          video-after (m.video/pull-one @test-conn (-> editor :editor/current-video :db/id))]
      (is (= 2 (count (:video/tx-logs video-after)))))))


(run-tests)


(comment

  (m.editor/create-video! test-conn (:db/id editor))

  editor

  )