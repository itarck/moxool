(ns astronomy2.test-all
  (:require
   [cljs.test :refer-macros [run-all-tests]]
   [astronomy2.plugin.test-astro-scene]
   [astronomy2.plugin.test-star]
   [astronomy2.plugin.test-planet]))


(run-all-tests #"astronomy2.plugin.test\-.*")