(ns film2.parts.schema
  (:require
   [film2.modules.ioframe.m :as ioframe.m]
   [film2.modules.iovideo.m :as iovideo.m]
   [film2.modules.editor.m :as editor.m]
   [film2.modules.player.m :as player.m]
   [film2.modules.recorder.m :as recorder.m]
   [film2.modules.studio.m :as studio.m]
   ))


(def schema
  (merge
   ioframe.m/schema
   iovideo.m/schema
   editor.m/schema
   recorder.m/schema
   player.m/schema
   studio.m/schema))