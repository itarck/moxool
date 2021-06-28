(ns astronomy.model.user.goto-celestial-tool
  (:require
   [posh.reagent :as p]))


(def sample-1
  #:goto-celestial-tool {:tool/name "goto celestial tool"
                         :tool/chinese-name "前往星球"
                         :tool/icon "/image/pirate/cow.jpg"
                         :tool/target {:planet/name "earth"}
                         :entity/type :goto-celestial-tool})


(def schema {})



(defn set-target-tx [goto-celestial-tool new-target-id]
  [{:db/id (:db/id goto-celestial-tool)
    :tool/target new-target-id}])