(ns astronomy.model.test-moon-orbit
  (:require
   [cljs-time.core :as t]
   [shu.calendar.epoch :as epoch]
   [shu.three.vector3 :as v3]
   [shu.goog.math :as gmath]
   [cljs-time.format :as ft]
   [astronomy.objects.ellipse-orbit.m :as m.ellipse-orbit]
   [astronomy.objects.moon-orbit.m :as mo :refer [moon-sample1]]))


(=
 (mo/cal-current-longitude-of-the-ascending-node moon-sample1 0)
 (+ (mo/cal-current-longitude-of-the-ascending-node moon-sample1 6798) 360))

(=
 (mo/cal-current-argument-of-periapsis moon-sample1 0)
 (- (mo/cal-current-argument-of-periapsis moon-sample1 3233) 360))

(def earth-sample
  #:ellipse-orbit{:semi-major-axis 499.0052919
                  :eccentricity 0.01671022
                  :inclination-in-degree 0.00005
                  :longitude-of-the-ascending-node-in-degree -11.26064
                  :argument-of-periapsis-in-degree 114.20783
                  :mean-anomaly-in-degree 357.51716
                  :angular-velocity-in-degree (m.ellipse-orbit/period-to-angular-velocity-in-degree 365.256363004)

                  :orbit/type :ellipse-orbit
                  :orbit/period 365.256363004
                  :orbit/color "green"
                  :orbit/show? true})


(defn lunar-ellipse-diff [earth-orbit moon-orbit epoch-days]
  (let [earth-position-1 (m.ellipse-orbit/cal-position-vector earth-orbit epoch-days)
        moon-position-1 (mo/cal-position-vector moon-orbit epoch-days)
        deg (gmath/to-degree (v3/angle-to earth-position-1 moon-position-1))]
    (* (/ deg 360) 27.3 24)))

(defn sun-ellipse-diff [earth-orbit moon-orbit epoch-days]
  (let [earth-position-1 (m.ellipse-orbit/cal-position-vector earth-orbit epoch-days)
        moon-position-1 (mo/cal-position-vector moon-orbit epoch-days)
        deg (gmath/to-degree (v3/angle-to earth-position-1 moon-position-1))]
    (* (/ (- 180 deg) 360) 27.3 24)))

;; 研究一些月食
;; 2018 7 27 22 20 最大的月食
(def epoch-days-0 (epoch/to-epoch-days (t/date-time 2018 7 27 22 20)))

epoch-days-0
(+ (/ 27.21222082 2) epoch-days-0)
;; => 6796.037408835926


;; case1 2018年1月31日 13:30 食甚
;; 升交点 2018年1月31日 18:12

(def epoch-days-1 (epoch/to-epoch-days (t/date-time 2018 1 31 13 30)))
epoch-days-1

(def earth-position-1 (m.ellipse-orbit/cal-position-vector earth-sample epoch-days-1))

(def moon-position-1 (mo/cal-position-vector moon-sample1 epoch-days-1))

(mo/cal-rem-epoch-days moon-sample1 epoch-days-1)
;; => -0.3000000000001819

(gmath/to-degree (v3/angle-to earth-position-1 moon-position-1))

(lunar-ellipse-diff earth-sample moon-sample1 epoch-days-1)

;; 2021年5月26日 11:19

(def epoch-days-2 (epoch/to-epoch-days (t/date-time 2021 5 26 11 19)))

(def earth-position-2 (m.ellipse-orbit/cal-position-vector earth-sample epoch-days-2))

(def moon-position-2 (mo/cal-position-vector moon-sample1 epoch-days-2))

(mo/cal-rem-epoch-days moon-sample1 epoch-days-1)
;; => -0.4886202255555929

(gmath/to-degree (v3/angle-to earth-position-2 moon-position-2))
;; => 0.8143446097001119

(/ 0.81 360)

;; 2000年1月21日	4:44


(def epoch-days-3 (epoch/to-epoch-days (t/date-time 2000 1 21 4 44)))

(def earth-position-3 (m.ellipse-orbit/cal-position-vector earth-sample epoch-days-3))

(def moon-position-3 (mo/cal-position-vector moon-sample1 epoch-days-3))

(mo/cal-rem-epoch-days moon-sample1 epoch-days-3)

(gmath/to-degree (v3/angle-to earth-position-3 moon-position-3))
;; => 0.44993780653404153



;; 2037年1月31日	14:00

(def epoch-days-4 (epoch/to-epoch-days (t/date-time 2037 1 31 14 00)))

(def earth-position-4 (m.ellipse-orbit/cal-position-vector earth-sample epoch-days-4))

(def moon-position-4 (mo/cal-position-vector moon-sample1 epoch-days-4))

(mo/cal-rem-epoch-days moon-sample1 epoch-days-4)

(gmath/to-degree (v3/angle-to earth-position-4 moon-position-4))



;; 日食 2020年12月14日	16:14:39


(def epoch-days-5 (epoch/to-epoch-days (t/date-time  2020 12 14 16 14 39)))

(def earth-position-5 (m.ellipse-orbit/cal-position-vector earth-sample epoch-days-5))

(def moon-position-5 (mo/cal-position-vector moon-sample1 epoch-days-5))

(mo/cal-rem-epoch-days moon-sample1 epoch-days-5)

(gmath/to-degree (v3/angle-to earth-position-5 moon-position-5))


;; 月食 2011年6月15日 	20:13 升点月食

(def epoch-days-6 (epoch/to-epoch-days (t/date-time 2011 6 15 20 13)))

epoch-days-6
;; => 4183.343103981481


(def earth-position-6 (m.ellipse-orbit/cal-position-vector earth-sample epoch-days-6))

(def moon-position-6 (mo/cal-position-vector moon-sample1 epoch-days-6))

(lunar-ellipse-diff earth-sample moon-sample1 epoch-days-6)

(mo/cal-current-longitude-of-periapsis-eme moon-sample1 epoch-days-6)
;; => 558.8441849675224

(- 558.8441849675224 360)

(mo/cal-current-argument-of-periapsis-emo-from-eme moon-sample1 epoch-days-6)
;; => 655.3177433670518

(- 655.3177433670518 360)
;; => 295.3177433670518


(mo/cal-current-argument-of-periapsis-emo moon-sample1 epoch-days-6)
;; => 282.31

(mo/cal-current-argument-of-periapsis-emo moon-sample1 0)
;; => -405.04897646432363

(mo/cal-current-argument-of-periapsis-emo moon-sample1 2191)
;; => -45.04897646432369

(mo/cal-current-argument-of-periapsis-emo-from-eme  moon-sample1 0)
;; => -32.04082652280994

(mo/cal-current-argument-of-periapsis-emo-from-eme  moon-sample1 2191)
;; => 327.95896053633766

(mo/cal-current-argument-of-periapsis-emo moon-sample1 epoch-days-1)
(- (mo/cal-current-argument-of-periapsis-emo-from-eme  moon-sample1 epoch-days-1) 360)

(mo/cal-current-longitude-of-the-ascending-node moon-sample1 epoch-days-6)
;; => -96.47355839952931

(mo/cal-current-argument-of-periapsis-emo moon-sample1 epoch-days-6)
;; => 282.31

(mo/cal-current-mean-anomaly moon-sample1 epoch-days-6)
;; => 72.8853077934439



(comment
  (lunar-ellipse-diff earth-sample moon-sample1 epoch-days-1)
  (lunar-ellipse-diff earth-sample moon-sample1 epoch-days-2)
  (lunar-ellipse-diff earth-sample moon-sample1 epoch-days-3)
  (lunar-ellipse-diff earth-sample moon-sample1 epoch-days-4)
  
  )

(def lunar-dts ["1998-8-8 02:25" "1999-1-31 16:18" "1999-7-28 11:34" "2000-1-21 04:44" "2000-7-16 13:56" "2001-1-9 20:21" "2001-7-5 14:55" "2001-12-30 10:29" "2002-5-26 12:03" "2002-6-24 21:27" "2002-11-20 01:47" "2003-5-16 03:40" "2003-11-9 01:19" "2004-5-4 20:30" "2004-10-28 03:04" "2005-4-24 09:55" "2005-10-17 12:03" "2006-3-14 23:48" "2006-9-7 18:51" "2007-3-3 23:21" "2007-8-28 10:37" "2008-2-21 03:26" "2008-8-16 21:10" "2009-2-9 14:38" "2009-7-7 09:39" "2009-8-6 00:39" "2009-12-31 19:23" "2010-6-26 11:38" "2010-12-21 08:17" "2011-6-15 20:13" "2011-12-10 14:32" "2012-6-4 11:03" "2012-11-28 14:33" "2013-4-25 20:07" "2013-5-25 04:10" "2013-10-18 23:50" "2014-4-15 07:46" "2014-10-8 10:55" "2015-4-4 12:00" "2015-9-28 02:47" "2016-3-23 11:47" "2016-8-18 09:42" "2016-9-16 18:54" "2017-2-11 00:44" "2017-8-7 18:20" "2018-1-31 13:30" "2018-7-27 20:22" "2019-1-21 05:12" "2019-7-16 21:31" "2020-1-10 19:10" "2020-6-5 19:25" "2020-7-5 04:30" "2020-11-30 09:43" "2021-5-26 11:19" "2021-11-19 09:03" "2022-5-16 04:11" "2022-11-8 10:59" "2023-5-5 17:23" "2023-10-28 20:14" "2024-3-25 07:13" "2024-9-18 02:44" "2025-3-14 06:59" "2025-9-7 18:12" "2026-3-3 11:33" "2026-8-28 04:13" "2027-2-20 23:13" "2027-7-18 16:03" "2027-8-17 07:14" "2028-1-12 04:13" "2028-7-6 18:19" "2028-12-31 16:52" "2029-6-26 03:22" "2029-12-20 22:42" "2030-6-15 18:33" "2030-12-9 22:27" "2031-5-7 03:51" "2031-6-5 11:44" "2031-10-30 07:45" "2032-4-25 15:13" "2032-10-18 19:02" "2033-4-14 19:12" "2033-10-8 10:55" "2034-4-3 19:05" "2034-9-28 02:46" "2035-2-22 09:05" "2035-8-19 01:11" "2036-2-11 22:11" "2036-8-7 02:51" "2037-1-31 14:00" "2037-7-27 04:08" "2038-1-21 03:48" "2038-6-17 02:43" "2038-7-16 11:34" "2038-12-11 17:43" "2039-6-6 18:53" "2039-11-30 16:55" "2040-5-26 11:45" "2040-11-18 19:03" "2041-5-16 00:41" "2041-11-8 04:33" "2042-4-5 14:28" "2042-9-29 10:44" "2042-10-28 19:33" "2043-3-25 14:30" "2043-9-19 01:50" "2044-3-13 19:37" "2044-9-7 11:19" "2045-3-3 07:42" "2045-8-27 13:53" "2046-1-22 13:01" "2046-7-18 01:04" "2047-1-12 01:24" "2047-7-7 10:34" "2048-1-1 06:52" "2048-6-26 02:01" "2048-12-20 06:26" "2049-5-17 11:25" "2049-6-15 19:12" "2049-11-9 15:50" "2050-5-6 22:30" "2050-10-30 03:20" "2051-4-26 02:14" "2051-10-19 19:10" "2052-4-14 02:16" "2052-10-8 10:44" "2053-3-4 17:20" "2053-8-29 08:04" "2054-2-22 06:49" "2054-8-18 09:24" "2055-2-11 22:44" "2055-8-7 10:51" "2056-2-1 12:24" "2056-6-27 10:01" "2056-7-26 18:41" "2056-12-22 01:47" "2057-6-17 02:24" "2057-12-11 00:51" "2058-6-6 19:14" "2058-11-30 03:14" "2059-5-27 07:53" "2059-11-19 12:59" "2060-4-15 21:35" "2060-10-9 18:51" "2060-11-8 04:02" "2061-4-4 21:52" "2061-9-29 09:36" "2062-3-25 03:32" "2062-9-18 18:32" "2063-3-14 16:04" "2063-9-7 20:39" "2064-2-2 21:47" "2064-7-28 07:50" "2065-1-22 09:57" "2065-7-17 17:46" "2066-1-11 15:02" "2066-7-7 09:28" "2066-12-31 14:28" "2067-5-28 18:54" "2067-6-27 02:39" "2067-11-21 00:02" "2068-5-17 05:40" "2068-11-9 11:44" "2069-5-6 09:07" "2069-10-30 03:33" "2070-4-25 09:19" "2070-10-19 18:49" "2071-3-16 01:29" "2071-9-9 15:03" "2072-3-4 15:21" "2072-8-28 16:03" "2073-2-22 07:22" "2073-8-17 17:40" "2074-2-11 20:53" "2074-7-8 17:19" "2074-8-7 01:53" "2075-1-2 09:52" "2075-6-28 09:53" "2075-12-22 08:53" "2076-6-17 02:37" "2076-12-10 11:32" "2077-6-6 14:57" "2077-11-29 21:33" "2078-4-27 04:33" "2078-10-21 03:05" "2078-11-19 12:37" "2079-4-16 05:08" "2079-10-10 17:28" "2080-4-4 11:21" "2080-9-29 01:50" "2081-3-25 00:19" "2081-9-18 03:32" "2082-2-13 06:26" "2082-8-8 14:44" "2083-2-2 18:24" "2083-7-29 01:03" "2084-1-22 23:10" "2084-7-17 16:56" "2085-1-10 22:29" "2085-6-8 02:15" "2085-12-1 08:22" "2086-5-28 12:41" "2086-11-20 20:17" "2087-5-17 15:52" "2087-11-10 12:02" "2088-5-5 16:14" "2088-10-30 03:00" "2089-3-26 09:31" "2089-9-19 22:08" "2090-3-15 23:45" "2090-9-8 22:49" "2091-3-5 15:55" "2091-8-29 00:35" "2092-2-23 05:18" "2092-7-19 00:39" "2092-8-17 09:11" "2093-1-12 17:57" "2093-7-8 17:21" "2094-1-1 16:57" "2094-6-28 09:59" "2094-12-21 19:53" "2095-6-17 21:57" "2095-12-11 06:12" "2096-5-7 11:21" "2096-6-6 02:40" "2096-10-31 11:27" "2096-11-29 21:19" "2097-4-26 12:15" "2097-10-21 01:27" "2098-4-15 19:01" "2098-10-10 09:16" "2099-4-5 08:27" "2099-9-29 10:33" "2100-2-24 15:02" "2100-8-19 21:41"]) 

(def sun-dts [ "2001-6-21 12:04:46" "2001-12-14 20:53:01" "2002-6-10 23:45:22" "2002-12-4 07:32:16" "2003-5-31 04:09:22" "2003-11-23 22:50:22" "2004-4-19 13:35:05" "2004-10-14 03:00:23" "2005-4-8 20:36:51" "2005-10-3 10:32:47" "2006-3-29 10:12:23" "2006-9-22 11:41:16" "2007-3-19 02:32:57" "2007-9-11 12:32:24" "2008-2-7 03:56:10" "2008-8-1 10:22:12" "2009-1-26 07:59:45" "2009-7-22 02:36:25" "2010-1-15 07:07:39" "2010-7-11 19:34:38" "2011-1-4 08:51:42" "2011-6-1 21:17:18" "2011-7-1 08:39:30" "2011-11-25 06:21:24" "2012-5-20 23:53:54" "2012-11-13 22:12:55" "2013-5-10 00:26:20" "2013-11-3 12:47:36" "2014-4-29 06:04:33" "2014-10-23 21:45:39" "2015-3-20 09:46:47" "2015-9-13 06:55:19" "2016-3-9 01:58:19" "2016-9-1 09:08:02" "2017-2-26 14:54:33" "2017-8-21 18:26:40" "2018-2-15 20:52:33" "2018-7-13 03:02:16" "2018-8-11 09:47:28" "2019-1-6 01:42:38" "2019-7-2 19:24:08" "2019-12-26 05:18:53" "2020-6-21 06:41:15" "2020-12-14 16:14:39" "2021-6-10 10:43:07" "2021-12-4 07:34:38" "2022-4-30 20:42:36" "2022-10-25 11:01:20" "2023-4-20 04:17:56" "2023-10-14 18:00:41" "2024-4-8 18:18:29" "2024-10-2 18:46:13" "2025-3-29 10:48:36" "2025-9-21 19:43:04" "2026-2-17 12:13:06" "2026-8-12 17:47:06" "2027-2-6 16:00:48" "2027-8-2 10:07:50" "2028-1-26 15:08:59" "2028-7-22 02:56:40" "2029-1-14 17:13:48" "2029-6-12 04:06:13" "2029-7-11 15:37:19" "2029-12-5 15:03:58" "2030-6-1 06:29:13" "2030-11-25 06:51:37" "2031-5-21 07:16:04" "2031-11-14 21:07:31" "2032-5-9 13:26:42" "2032-11-3 05:34:13" "2033-3-30 18:02:36" "2033-9-23 13:54:31" "2034-3-20 10:18:45" "2034-9-12 16:19:28" "2035-3-9 23:05:54" "2035-9-2 01:56:46" "2036-2-27 04:46:49" "2036-7-23 10:32:06" "2036-8-21 17:25:45" "2037-1-16 09:48:55" "2037-7-13 02:40:36" "2038-1-5 13:47:11" "2038-7-2 13:32:55" "2038-12-26 01:00:10" "2039-6-21 17:12:54" "2039-12-15 16:23:46" "2040-5-11 03:43:02" "2040-11-4 19:09:02" "2041-4-30 11:52:21" "2041-10-25 01:36:22" "2042-4-20 02:17:30" "2042-10-14 02:00:42" "2043-4-9 18:57:49" "2043-10-3 03:01:49" "2044-2-28 20:24:40" "2044-8-23 01:17:02" "2045-2-16 23:56:07" "2045-8-12 17:42:39" "2046-2-5 23:06:26" "2046-8-2 10:21:13" "2047-1-26 01:33:18" "2047-6-23 10:52:31" "2047-7-22 22:36:17" "2047-12-16 23:50:12" "2048-6-11 12:58:53" "2048-12-5 15:35:27" "2049-5-31 13:59:59" "2049-11-25 05:33:48" "2050-5-20 20:42:50" "2050-11-14 13:30:53" "2051-4-11 02:10:39" "2051-10-4 21:02:14" "2052-3-30 18:31:53" "2052-9-22 23:39:10" "2053-3-20 07:08:19" "2053-9-12 09:34:09" "2054-3-9 12:33:40" "2054-8-3 18:04:02" "2054-9-2 01:09:34" "2055-1-27 17:54:05" "2055-7-24 09:57:50" "2056-1-16 22:16:45" "2056-7-12 20:21:59" "2057-1-5 09:47:52" "2057-7-1 23:40:15" "2057-12-26 01:14:35" "2058-5-22 10:39:25" "2058-6-21 00:19:35" "2058-11-16 03:23:07" "2059-5-11 19:22:16" "2059-11-5 09:18:15" "2060-4-30 10:10:00" "2060-10-24 09:24:10" "2061-4-20 02:56:49" "2061-10-13 10:32:10" "2062-3-11 04:26:16" "2062-9-3 08:54:27" "2063-2-28 07:43:30" "2063-8-24 01:22:11" "2064-2-17 07:00:23" "2064-8-12 17:46:06" "2065-2-5 09:52:26" "2065-7-3 17:33:52" "2065-8-2 05:34:17" "2065-12-27 08:39:56" "2066-6-22 19:25:48" "2066-12-17 00:23:40" "2067-6-11 20:42:26" "2067-12-6 14:03:43" "2068-5-31 03:56:39" "2068-11-24 21:32:30" "2069-4-21 10:11:09" "2069-5-20 17:53:18" "2069-10-15 04:19:56" "2070-4-11 02:36:09" "2070-10-4 07:08:57" "2071-3-31 15:01:06" "2071-9-23 17:20:28" "2072-3-19 20:10:31" "2072-9-12 08:59:20" "2073-2-7 01:55:59" "2073-8-3 17:15:23" "2074-1-27 06:44:15" "2074-7-24 03:10:32" "2075-1-16 18:36:04" "2075-7-13 06:05:44" "2076-1-6 10:07:27" "2076-6-1 17:31:22" "2076-7-1 06:50:43" "2076-11-26 11:43:01" "2077-5-22 02:46:05" "2077-11-15 17:07:56" "2078-5-11 17:56:55" "2078-11-4 16:55:44" "2079-5-1 10:50:13" "2079-10-24 18:11:21" "2080-3-21 12:20:15" "2080-9-13 16:38:09" "2081-3-10 15:23:31" "2081-9-3 09:07:31" "2082-2-27 14:47:00" "2082-8-24 01:16:21" "2083-2-16 18:06:36" "2083-7-15 00:14:23" "2083-8-13 12:34:41" "2084-1-7 17:30:23" "2084-7-3 01:50:26" "2084-12-27 09:13:48" "2085-6-22 03:21:16" "2085-12-16 22:37:48" "2086-6-11 11:07:14" "2086-12-6 05:38:55" "2087-5-2 18:04:42" "2087-6-1 01:27:14" "2087-10-26 11:46:57" "2088-4-21 10:31:49" "2088-10-14 14:48:05" "2089-4-10 22:44:42" "2089-10-4 01:15:23" "2090-3-31 03:38:08" "2090-9-23 16:56:36" "2091-2-18 09:54:40" "2091-8-15 00:34:43" "2092-2-7 15:10:20" "2092-8-3 09:59:33" "2093-1-27 03:22:16" "2093-7-23 12:32:04" "2094-1-16 18:59:03" "2094-6-13 00:22:11" "2094-7-12 13:24:35" "2094-12-7 20:05:56" "2095-6-2 10:07:40" "2095-11-27 01:02:57" "2096-5-22 01:37:14" "2096-11-15 00:36:15" "2097-5-11 18:34:31" "2097-11-4 02:01:25" "2098-4-1 20:02:31" "2098-9-25 00:31:16" "2098-10-24 10:36:11" "2099-3-21 22:54:32" "2099-9-14 16:57:53"])


(defn lunar-date-string-to-epoch-days [date-string]
  (let [lunar-formatter (ft/formatter "yyyy-MM-dd HH:mm")]
    (->> date-string
         (ft/parse lunar-formatter)
         (epoch/to-epoch-days))))

(defn sun-date-string-to-epoch-days [date-string]
  (let [lunar-formatter (ft/formatter "yyyy-MM-dd HH:mm:ss")]
    (->> date-string
         (ft/parse lunar-formatter)
         (epoch/to-epoch-days))))

(defn average [seq]
  (/ (apply + seq) (count seq)))


(sun-date-string-to-epoch-days "2001-6-21 12:04:46")

(->> sun-dts
     (map sun-date-string-to-epoch-days)
     (map #(sun-ellipse-diff earth-sample moon-sample1 %))
     average)

(->> lunar-dts
     (map lunar-date-string-to-epoch-days)
     (map #(lunar-ellipse-diff earth-sample moon-sample1 %))
     average)


(lunar-ellipse-diff earth-sample moon-sample1 epoch-days-6)


;; => 73 2.6165756997595255
;; => 73.5 2.3370887792445973
;; => 73.7 2.2844828703999265
;; => 73.8 2.270814327229077
;; => 73.9 2.265880072303244
;; => 74 2.2700056442075622
;; => 75 2.841172767938135
;; => 76 4.254818445334801


