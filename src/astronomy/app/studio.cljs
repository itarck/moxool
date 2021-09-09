(ns astronomy.app.studio
  (:require
   [film2.system.studio :as studio]))


(defonce app (studio/create-app! {}))

