(ns shu.astronomy.light)


(def light-second-unit 1)
(def light-hour-unit 3600)
(def light-day-unit (* light-hour-unit 24))
(def light-year-unit (* 365 light-day-unit))


(defn scale-light-year [light-years]
  (* light-year-unit light-years))


(comment
  (scale-light-year 100000)
;;   
  )