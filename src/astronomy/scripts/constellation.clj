(ns astronomy.scripts.constellation
  (:require
   [clojure.string :as string]
   [ultra-csv.core :as ucsv]))


;; part 1

(def constellations1
  [;
   #:constellation {:abbreviation "And" :star-HR-lines [[8961 8976 8965 8762 8965   68  165   15  165  163  215  163  165  337  603  337  464  337  269  226]]}
   #:constellation {:abbreviation "Ant" :star-HR-lines [[4273 4104 3871 3765]]}
   #:constellation {:abbreviation "Aps" :star-HR-lines [[5470 6020 6102 6163]]}
   #:constellation {:abbreviation "Aql" :star-HR-lines [[7602 7557 7525 7377 7235 7377 7570 7710 7570 7377 7236 7193]]}
   #:constellation {:abbreviation "Aqr" :star-HR-lines [[8812 8841 8834 8597 8518 8698 8709 8698 8518 8499 8418 8499 8518 8414 8232]]}
   #:constellation {:abbreviation "Ara" :star-HR-lines [[6743 6510 6461 6462 6500 6462 6285 6229 6285 6295 6510]]}
   #:constellation {:abbreviation "Ari" :star-HR-lines [[951  888  838  617  553  546]]}
   #:constellation {:abbreviation "Aur" :star-HR-lines [[1708 2088 2095 1791 1577 1612 1641 1605 1708]]}
   #:constellation {:abbreviation "Boo" :star-HR-lines [[5435 5429 5340 5506 5681 5602 5435 5351 5404 5329 5351]]}
   #:constellation {:abbreviation "CMa" :star-HR-lines [[2596 2574 2657 2596 2491 2294 2429 2580 2618 2646 2693 2827 2693 2653 2491]]}
   #:constellation {:abbreviation "CMi" :star-HR-lines [[2943 2845]]}
   #:constellation {:abbreviation "CVn" :star-HR-lines [[4915 4785]]}
   #:constellation {:abbreviation "Cae" :star-HR-lines [[1443 1502 1503]]}
   #:constellation {:abbreviation "Cam" :star-HR-lines [[3318 3340 4231 4674 4174 3318] [1040 1035 1155 1148 1542 1603 1568]]}
   #:constellation {:abbreviation "Cap" :star-HR-lines [[7754 7776 7936 7980 8204 8278 8322 8278 8167 8075 7776]]}
   #:constellation {:abbreviation "Car" :star-HR-lines [[2326 2554 3117 3307 3699 4050 4140 4199 4037 3685 3890]]}
   #:constellation {:abbreviation "Cas" :star-HR-lines [[542  403  264  168   21]]}
   #:constellation {:abbreviation "Cen" :star-HR-lines [[4467 4522 4390 4638 4621 4743 4802 4819 5132 5267 5132 5459 5132 5231 5193 5190 5028 5190 5288 5190 5248 5440 5576]]}
   #:constellation {:abbreviation "Cep" :star-HR-lines [[8974 8238 8162 8465 8694 8974]]}
   #:constellation {:abbreviation "Cet" :star-HR-lines [[804  718  813  896  911  804  779  681  539  402  334  188  433  585  539]]}
   #:constellation {:abbreviation "Cir" :star-HR-lines [[5670 5463 5704]]}
   #:constellation {:abbreviation "Cnc" :star-HR-lines [[3572 3461 3249 3461 3449 3475]]}
   #:constellation {:abbreviation "Col" :star-HR-lines [[2296 2256 2106 2040 2120 2040 1956 1743]]}
   #:constellation {:abbreviation "Com" :star-HR-lines [[4968 4983 4737]]}
   #:constellation {:abbreviation "CrA" :star-HR-lines [[7152 7226 7254 7259 7242 7188]]}
   #:constellation {:abbreviation "CrB" :star-HR-lines [[5971 5947 5889 5849 5793 5747 5778]]}
   #:constellation {:abbreviation "Crt" :star-HR-lines [[4567 4514 4405 4343 4287 4382 4405 4382 4402 4468]]}
   #:constellation {:abbreviation "Cru" :star-HR-lines [[4730 4763] [4853 4656]]}
   #:constellation {:abbreviation "Crv" :star-HR-lines [[4623 4630 4662 4757 4786 4630]]}
   #:constellation {:abbreviation "Cyg" :star-HR-lines [[7417 7615 7796 7949 8115 7949 7796 7924 7796 7528 7420]]}
   #:constellation {:abbreviation "Del" :star-HR-lines [[7852 7882 7906 7948 7928 7882]]}
   #:constellation {:abbreviation "Dor" :star-HR-lines [[1338 1465 1674 1922 2015 1922 2102]]}
   #:constellation {:abbreviation "Dra" :star-HR-lines [[6688 6705 6536 6554 6688 7310 7582 6927 6636 6396 6132 5986 5744 5291 4787 4434 3751]]}
   #:constellation {:abbreviation "Equ" :star-HR-lines [[8131 8178 8123 8097]]}
   #:constellation {:abbreviation "Eri" :star-HR-lines [[1666 1560 1520 1463 1298 1231 1136 1084  874  811  818  919 1003 1088 1173 1213 1240 1464 1393 1347 1195 1190 1106 1008  898  794  789  721  674  566  472]]}
   #:constellation {:abbreviation "For" :star-HR-lines [[963  841  612]]}
   #:constellation {:abbreviation "Gem" :star-HR-lines [[2134 2216 2286 2473 2343 2473 2697 2852 2890 2852 2697 2540 2697 2821 2905 2990 2905 2985 2905 2777 2650 2421 2650 2777 2763 2484]]}
   #:constellation {:abbreviation "Gru" :star-HR-lines [[8353 8411 8556 8636 8425 8636 8675 8747 8675 8820 8787]]}
   #:constellation {:abbreviation "Her" :star-HR-lines [[6324 6418 6695 6588 6695 6418 6220 6168 6092 6168 6220 6212 6148 6212 6324 6410 6623 6703 6779]]}
   #:constellation {:abbreviation "Hor" :star-HR-lines [[909  934  802  778  810  868 1326 1302]]}
   #:constellation {:abbreviation "Hya" :star-HR-lines [[3492 3482 3410 3418 3454 3492 3547 3665 3845 3748 3903 3994 4094 4232 4450 4552 5020 5287]]}
   #:constellation {:abbreviation "Hyi" :star-HR-lines [[591  806 1208   98  591]]}
   #:constellation {:abbreviation "Ind" :star-HR-lines [[7869 8140 8368 8387 8368 8140 8055 7986]]}
   #:constellation {:abbreviation "LMi" :star-HR-lines [[4247 4100 3974]]}
   #:constellation {:abbreviation "Lac" :star-HR-lines [[8498 8485 8579 8632 8579 8523 8579 8572 8541 8538 8585 8572]]}
   #:constellation {:abbreviation "Leo" :star-HR-lines [[3873 3905 4031 4058 3975 3982 3852 3982 4359 4399 4386 4399 4359 4534 4357 4058]]}
   #:constellation {:abbreviation "Lep" :star-HR-lines [[1654 1829 1865 1702 1865 1998 2085]]}
   #:constellation {:abbreviation "Lib" :star-HR-lines [[5908 5787 5685 5531 5603 5685 5603 5794 5812]]}
   #:constellation {:abbreviation "Lup" :star-HR-lines [[5987 5948 5776 5708 5646 5649 5469 5571 5695 5776 5695 5705 5883]]}
   #:constellation {:abbreviation "Lyn" :star-HR-lines [[3705 3690 3612 3579 3275 2818 2560 2238]]}
   #:constellation {:abbreviation "Lyr" :star-HR-lines [[7001 7056 7106 7178 7139 7056]]}
   #:constellation {:abbreviation "Men" :star-HR-lines [[2261 1953 1629 1677]]}
   #:constellation {:abbreviation "Mic" :star-HR-lines [[8151 8135 8039 7965]]}
   #:constellation {:abbreviation "Mon" :star-HR-lines [[2385 2298 2506 2714 2356 2227 2356 2714 2970 3188]]}
   #:constellation {:abbreviation "Mus" :star-HR-lines [[4844 4798 4923 4798 4773 4798 4671 4520]]}
   #:constellation {:abbreviation "Nor" :star-HR-lines [[6115 6072 5962]]}
   #:constellation {:abbreviation "Oct" :star-HR-lines [[8630 5339 8254 8630]]}
   #:constellation {:abbreviation "Oph" :star-HR-lines [[6556 6299 6149 6056 6075 6175 6378 6603 6556]]}
   #:constellation {:abbreviation "Ori" :star-HR-lines [[1948 2004 1713 1852 1790 1879 2061 2124 2199 2159 2047 2135 2199 2124 2061 1948 1903 1852 1790 1543 1552 1567 1601 1567 1552 1543 1544 1580 1638 1676]]}
   #:constellation {:abbreviation "Pav" :star-HR-lines [[7790 7913 8181 7913 7590 6982 6582 6745 6855 7074 7665 7913]]}
   #:constellation {:abbreviation "Peg" :star-HR-lines [[8454 8650 8775   15 8775 8781   39   15   39 8781 8634 8450 8308]]}
   #:constellation {:abbreviation "Per" :star-HR-lines [[936  941 1017  915 1017 1122 1220 1228 1203]]}
   #:constellation {:abbreviation "Phe" :star-HR-lines [[99  322  429  440  429  322  338  191   25   99]]}
   #:constellation {:abbreviation "Pic" :star-HR-lines [[2550 2042 2020]]}
   #:constellation {:abbreviation "PsA" :star-HR-lines [[8728 8628 8478 8386 8326 8305 8431 8576 8695 8720 8728]]}
   #:constellation {:abbreviation "Psc" :star-HR-lines [[360  383  352  360  437  510  596  489  294  224 9072 8969 8916 8878 8852 8911 8984 9004 8969]]}
   #:constellation {:abbreviation "Pup" :star-HR-lines [[3185 3045 2948 2922 2773 2451 2553 2878 3165 3185]]}
   #:constellation {:abbreviation "Pyx" :star-HR-lines [[3438 3468 3518]]}
   #:constellation {:abbreviation "Ret" :star-HR-lines [[1336 1355 1247 1264 1175 1336]]}
   #:constellation {:abbreviation "Scl" :star-HR-lines [[280 9016 8863 8937]]}
   #:constellation {:abbreviation "Sco" :star-HR-lines [[5985 6084 5953 6084 5944 6084 6134 6165 6241 6247 6271 6380 6553 6615 6580 6527 6508]]}
   #:constellation {:abbreviation "Sct" :star-HR-lines [[7063 6973 6930 6973 6884]]}
   #:constellation {:abbreviation "Ser" :star-HR-lines [[6446 6561 6581 6869 7142]]}
   #:constellation {:abbreviation "Ser" :star-HR-lines [[5867 5879 5933 5867 5788 5854 5892 5881 6446]]}
   #:constellation {:abbreviation "Sex" :star-HR-lines [[4119 3981 3909]]}
   #:constellation {:abbreviation "Sge" :star-HR-lines [[7635 7536 7479 7536 7488]]}
   #:constellation {:abbreviation "Sgr" :star-HR-lines [[7348 7581 7337 7581 7623 7650 7604 7440 7234 7121 7217 7264 7340 7342 7340 7264 7217 7150 7217 7121 7039 7194 7234 7194 7039 6913 6859 6746 6859 6879 6832]]}
   #:constellation {:abbreviation "Tau" :star-HR-lines [[1910 1457 1346 1239 1030 1239 1346 1373 1409 1791]]}
   #:constellation {:abbreviation "Tel" :star-HR-lines [[6783 6897 6905]]}
   #:constellation {:abbreviation "TrA" :star-HR-lines [[6217 5671 5771 5897 6217]]}
   #:constellation {:abbreviation "Tri" :star-HR-lines [[622  664  544  622]]}
   #:constellation {:abbreviation "Tuc" :star-HR-lines [[8540 8502 8848 9076   77  126 8848]]}
   #:constellation {:abbreviation "UMa" :star-HR-lines [[5191 5054 4905 4660 4554 4518 4377 4375 4377 4518 4335 4069 4033 4069 4335 4518 4554 4295 4301 4660 4301 3757 3323 3888 4295 3888 3775 3594 3569]]}
   #:constellation {:abbreviation "UMi" :star-HR-lines [[424 6789 6322 5903 5563 5735 6116 5903]]}
   #:constellation {:abbreviation "Vel" :star-HR-lines [[3206 3485 3734 3940 4216 4167 4023 3786 3634 3477 3426 3206]]}
   #:constellation {:abbreviation "Vir" :star-HR-lines [[5056 4963 4826 4910 5107 5056]]}
   #:constellation {:abbreviation "Vol" :star-HR-lines [[3615 3347 3223 2803 2736 3024 3223]]}
   #:constellation {:abbreviation "Vul" :star-HR-lines [[7306 7405 7592]]}])


;; part 2

(string/capitalize "SDFSDF")


(def filename "/Users/tizac/Downloads/constellation.csv")


(def csv-seq (ucsv/read-csv filename))

(keys (first csv-seq))
 
;; => {:DEm 25.91, :RAh 0, :group "英仙", :brightest-star "仙女座α(壁宿二)", :RAm 48.46, :latin-name "Andromeda", :DEd 37, :quadrant "NQ1", :abbr "AND", :chinese-name "仙女座", :area 722.278}

(defn parse-constellation2 [constel]
  (let [{:keys [DEm RAh group brightest-star RAm latin-name DEd quadrant abbr chinese-name area]} constel]
    #:constellation {:abbreviation (string/capitalize abbr)
                     :latin-name latin-name
                     :chinese-name chinese-name
                     :right-ascension (+ (* (/ RAh 24.0) 360.0)
                                         (/ RAm 60.0))
                     :declination (+ DEd
                                     (/ DEm 60.0))
                     :group group
                     :quadrant quadrant
                     :area area}))

(def constellations2
  (->>
   (ucsv/read-csv filename)
   (mapv parse-constellation2)))


(def output-filename1 "/Users/tizac/room/toolset/public/edn/constellation1.edn")
(def output-filename2 "/Users/tizac/room/toolset/public/edn/constellation2.edn")

(defn run! []
  (spit output-filename1 (str constellations1))
  (spit output-filename2 (str constellations2)))


(comment
  (run!)

  (read-string (slurp output-filename2))


  ;; 
  )
