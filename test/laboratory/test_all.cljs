(ns laboratory.test-all
  (:require 
   [cljs.test :refer-macros [run-all-tests]]
   [laboratory.parts.test-backpack]
   [laboratory.parts.test-user]
   [laboratory.parts.test-db]))



(run-all-tests #"laboratory.parts.test\-.*")