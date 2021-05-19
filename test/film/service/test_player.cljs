(ns film.service.test-player
  (:require
   [datascript.core :as d]
   [integrant.core :as ig]
   [methodology.lib.circuit]
   [cljs.core.async :refer [go >! <! timeout go-loop chan]]
   [cljs.test :refer-macros [deftest is testing run-tests async]]
   [methodology.service.log :as log]
   [film.test-conn :refer [create-system-conn! create-scene-conn!]]
   [film.model.player :as m.player]
   [film.service.player :as s.player]))


(derive ::conn :circuit/conn)
(derive ::chan :circuit/chan)
(derive ::service :circuit/service)


(def player-id [:player/name "default"])

(def scene-conn (create-scene-conn!))

(defn create-env! []
  (let [scene-system (ig/init {::conn #:conn{:schema {:slider/name {:db/unique :db.unique/identity}}
                                             :initial-db @scene-conn}
                               ::chan #:chan{}
                               ::service #:service{:service-fn log/init-service!
                                                   :props {}
                                                   :env {:service-chan (ig/ref ::chan)}}})]
    {:system-conn (create-system-conn!)
     :meta-chan (::chan scene-system)
     :scene-conn (::conn scene-system)}))


(deftest test-open-video!
  (let [env (create-env!)
        system-conn (:system-conn env)
        event1 {:player-id player-id
                :video-id [:video/name "another"]}
        event2 {:player-id player-id
                :video-id [:video/name "default"]}]
    (s.player/handle-event! :player/open-video event1 env)
    (let [player1 (m.player/pull-whole @system-conn player-id)]
      (is (= (-> player1 :player/current-video :video/name) 
             (-> event1 :video-id second))))

    (s.player/handle-event! :player/open-video event2
                            env)
    (let [player1 (m.player/pull-whole @system-conn player-id)]
      (is (= (-> player1 :player/current-video :video/name)
             (-> event2 :video-id second))))))


(defn done [])

(deftest test-start-play!
  (async done
   (go
     (let [env (create-env!)
           event {:player-id [:player/name "default"]}]
       (<! (s.player/handle-event! :player/start-play event env))
       (is (= 4 (count (d/datoms (deref (:scene-conn env)) :eavt))))
       (done)))))


(deftest test-seek-play!
  (let [env (create-env!)
        event {:player-id player-id
               :seek-time 60}]
    (s.player/handle-event! :player/seek-play event env)
    (is (= 2 (count (d/datoms (deref (:scene-conn env)) :eavt))))))


(run-tests)



(comment

  (def env (create-env!))

  (s.player/handle-event! :player/seek-play {:player-id player-id
                                             :seek-time 600} env)

  (def conn (:scene-conn env))

  conn

  (d/reset-conn! conn)

  (d/reset-conn! conn (d/empty-db (d/schema @conn)))

;
  )