#! /usr/bin/env bb
(require
 '[babashka.fs :as fs]
 '[clojure.string :as string]
 '[clojure.java.shell :refer [sh]])


(def root
  (->
   (:out (sh "pwd"))
   (string/trim)))

(def source-folder
  "/resources/public/")

(def dist-folder
  "/desktop/")

(def release-paths
  ["css"
   "image/moxool"
   "icons"
   "models/3-cityscene_kyoto_1995"
   "models/13-galaxy"
   "models/16-solar"
   "models/constellations.gltf"
   "models/starsphere.gltf"
   "slides"
   "private"
   "frame"
   "index.html"
   "package.json"])

(def dev-paths
  ["css"])

(defn create-dist-folders! []
  (fs/create-dirs "desktop/models")
  (fs/create-dirs "desktop/js")
  (fs/create-dirs "desktop/private")
  (fs/create-dirs "desktop/image")
  (fs/create-dirs "desktop/icons"))


(defn copy-to-release! []
  (doseq [path release-paths]
    (println "copying " path)
    (fs/copy-tree
     (str root source-folder path)
     (str root dist-folder path)
     {:replace-existing true})))


(create-dist-folders!)
(copy-to-release!)