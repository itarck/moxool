(ns film.model.test-player
  (:require
   [datascript.core :as d]
   [cljs.core.async :refer [go >! <! timeout]]
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [cljs.pprint :refer [pprint]]
   [posh.reagent :as p]
   [methodology.lib.chest :as chest]
   [film.test-conn :refer [create-test-conn!]]
   [film.model.player :as m.player]))


(def test-conn (create-test-conn!))

(def test-db @test-conn)


(def player-id [:player/name "default"])

(def player (m.player/pull-whole test-db player-id))

(def video (:player/current-video player))


(deftest test-txs
  (let [db1 (d/db-with test-db (m.player/create-session-tx player video))
        player1 (m.player/pull-whole db1 player-id)
        db2 (d/db-with db1 (m.player/start-session-tx player1 100))
        player2 (m.player/pull-whole db2 player-id)
        db3 (d/db-with db2 (m.player/update-session-tx player2 1000))
        player3 (m.player/pull-whole db3 player-id)
        db3b (d/db-with db2 (m.player/update-session-tx player2 10000))
        player3b (m.player/pull-whole db3b player-id)
        db4 (d/db-with db3 (m.player/pause-session-tx player3))
        player4 (m.player/pull-whole db4 player-id)
        db5 (d/db-with db4 (m.player/stop-session-tx player4))
        player5 (m.player/pull-whole db5 player-id)]
    (is (= (:player/session player1)
           {:mode :stop, :start-time 0, :current-time 0, :total-time 3000}))
    (is (= (:player/session player2)
           {:mode :start, :start-time 0, :current-time 0, :total-time 3000, :start-timestamp 100}))
    (is (m.player/check-session-starting? player2))
    (is (= (:player/session player3)
           {:mode :start, :start-time 0, :current-time 900, :total-time 3000, :start-timestamp 100, :current-timestamp 1000}))
    (is (= (m.player/should-session-stop? player3 video)
           false))
    (is (= (m.player/should-session-stop? player3b video)
           true))
    (is (= (:player/session player4)
           {:mode :pause, :start-time 900, :current-time 900, :total-time 3000, :start-timestamp 100, :current-timestamp 1000}))
    (is (m.player/check-session-paused? player4))
    (is (= (:player/session player5)
           {:mode :stop, :start-time 0, :current-time 3000, :total-time 3000, :start-timestamp 100, :current-timestamp 1000}))
    (is (m.player/check-session-stoped? player5))))



(run-tests)



(comment
  
  


;
  )