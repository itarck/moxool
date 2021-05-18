(ns user
  (:require
   [toolbag.core :refer [config]]
   [integrant.repl :refer [clear go halt prep init reset reset-all set-prep!]]))


(set-prep! (constantly config))

(go)

; (halt)
