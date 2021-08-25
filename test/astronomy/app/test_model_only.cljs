(ns astronomy.app.test-model-only
  (:require
   [posh.reagent :as p]
   [datascript.core :as d]
   [integrant.core :as ig]
   [astronomy.app.free-mode :as free-app]
   [astronomy.objects.star.m :as m.star]
   [methodology.model.user.backpack :as m.backpack]))


(def system (ig/init free-app/config))

(free-app/create-system!)

(def conn (::free-app/conn system))

(count (d/datoms @conn :eavt))

@(p/pull conn '[*] [:constellation/abbreviation "And"])

(m.backpack/sub-backpack-fully conn [:backpack/name "default"])
;; => {:db/id 18, :backpack/cell [{:db/id 19, :backpack-cell/tool {:tool/icon "/image/pirate/sheep.jpg", :tool/chinese-name "时钟1", :clock-tool/clock #:db{:id 4}, :entity/type :clock-tool, :tool/name "clock control 1", :tool/backpack #:db{:id 18}, :clock-tool/steps-per-second 50, :db/id 29, :clock-tool/days-per-step 0.041666666666666664, :clock-tool/status :stop}} #:db{:id 20} #:db{:id 21} #:db{:id 22} #:db{:id 23} #:db{:id 24} #:db{:id 25} #:db{:id 26} #:db{:id 27} #:db{:id 28}], :backpack/name "default", :backpack/owner #:db{:id 17}}

(m.backpack/sub-backpack conn [:backpack/name "default"])


(:backpack/cell @(p/pull conn '[*] [:backpack/name "default"]))

@(p/pull conn '[*] [:spaceship-camera-control/name "default"])


(let [stars (m.star/find-all-stars @conn)] 
  (map :star/right-ascension stars))