(ns laboratory.test-all
  (:require 
   [cljs.test :refer-macros [run-all-tests]]
   [laboratory.plugin.test-backpack]
   [laboratory.plugin.test-user]
   [laboratory.plugin.test-entity]))


(run-all-tests #"laboratory.plugin.test\-.*")