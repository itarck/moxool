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
  "/release/")

(def copy-paths
  ["css"
   "image"
   "js/app.js"
   "models/13-galaxy"
   "models/14-moon"
   "models/15-earth"
   "models/16-solar"
   "models/constellations.gltf"
   "models/starsphere.gltf"
   "slides"
   "temp"
   "index.html"
   "main.js"
   "package.json"])


(defn create-dist-folders! []
  (fs/create-dirs "release/models")
  (fs/create-dirs "release/js"))


(defn copy-to-release! []
  (doseq [path copy-paths]
    (println "copying " path)
    (fs/copy-tree
     (str root source-folder path)
     (str root dist-folder path)
     {:replace-existing true})))


(create-dist-folders!)
(copy-to-release!)