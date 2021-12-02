(ns film2.app.test-studio
  (:require
   [film2.app.studio :refer [system]]
   [posh.reagent :as p]))


(keys system)

(def conn
  (:studio/conn system))


@(p/pull conn '[*] [:studio/name "default"])

(p/transact! conn [#:studio{:name "default"
                            :studio/show-user-menu? true}])