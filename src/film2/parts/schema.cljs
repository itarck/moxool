(ns film2.parts.schema
  (:require
   [film2.modules.editor.m :as editor.m]
   [film2.modules.ioframe.m :as ioframe.m]))


(def schema
  (merge editor.m/schema
         ioframe.m/schema))