(ns hello-spec
  (:require
   [cljs.spec.alpha :as s]))


(s/valid? even? 10)

(s/def ::id int?)
