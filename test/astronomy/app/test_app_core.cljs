(ns astronomy.app.test-app-core
  (:require
   [astronomy.app.core :as app]
   [datascript.core :as d]))


(def conn (:studio/conn app/studio))

(def scene-system (:scene-system @(:studio/instance-atom app/studio)))

(def scene-conn (:system/conn scene-system))

(def mercury (d/pull @scene-conn '[*] [:planet/name "mercury"]))

(:planet/position-log mercury)