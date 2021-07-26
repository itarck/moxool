(ns methodology.lib.resource
  (:require [clojure.java.io :as io]))


(defmacro read-resource [resource-path]
  (slurp (clojure.java.io/resource resource-path)))
