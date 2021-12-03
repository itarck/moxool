(ns film2.modules.cinema.m
  (:require
   [applied-science.js-interop :as j]
   [clojure.string :as string]
   [datascript.core :as d]
   [reagent.crypt :as crypt]))


(def sample
  #:cinema {:db/id -2
            :name "default"
            :ioframe-names ["a" "b" "c"]})

(def schema
  #:cinema{:name {:db/unique :db.unique/identity}
           :editor {:db/cardinality :db.cardinality/one :db/valueType :db.type/ref}})


(defn pull-all
  [db cinema-id]
  (d/pull db '[*] cinema-id))


(defn change-ioframe-tx
  [db cinema ioframe-name]
  [#:cinema {:db/id (:db/id cinema)
             :current-ioframe-name ioframe-name}])


(defn generate-angel-code
  [email]
  (let [angel-salt "Thanks angel!!!"]
    (->
     (crypt/hash (str email angel-salt) :sha256)
     (crypt/bytes->hex)
     (subs 0 6)
     (string/upper-case))))


(defn varify-angel-code
  [email angel-code]
  (let [true-angel-code (generate-angel-code email)
        formatted-angel-code (-> angel-code
                                 (string/upper-case))]
    (= formatted-angel-code true-angel-code)))


(comment
  (generate-angel-code "hello")
  ;; => "D60729"

  (.setItem js/localStorage "testing" "abc")
  (j/call js/localStorage :getItem "testing")

  )