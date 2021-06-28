(ns astronomy.scrips.stars
  (:require
   [clojure.string :as string]
   [clojure.java.io :as io]))


;; --------------------------------------------------------------------------------
;;    Bytes Format  Units   Label    Explanations
;; --------------------------------------------------------------------------------
;;    1-  4  I4     ---     HR       [1/9110]+ Harvard Revised Number
;;                                     = Bright Star Number
;;    5- 14  A10    ---     Name     Name, generally Bayer and/or Flamsteed name
;;   15- 25  A11    ---     DM       Durchmusterung Identification (zone in
;;                                     bytes 17-19)
;;   26- 31  I6     ---     HD       [1/225300]? Henry Draper Catalog Number
;;   32- 37  I6     ---     SAO      [1/258997]? SAO Catalog Number
;;   38- 41  I4     ---     FK5      ? FK5 star Number
;;       42  A1     ---     IRflag   [I] I if infrared source
;;       43  A1     ---   r_IRflag  *[ ':] Coded reference for infrared source
;;       44  A1     ---    Multiple *[AWDIRS] Double or multiple-star code
;;   45- 49  A5     ---     ADS      Aitken's Double Star Catalog (ADS) designation
;;   50- 51  A2     ---     ADScomp  ADS number components
;;   52- 60  A9     ---     VarID    Variable star identification
;;   61- 62  I2     h       RAh1900  ?Hours RA, equinox B1900, epoch 1900.0 (1)
;;   63- 64  I2     min     RAm1900  ?Minutes RA, equinox B1900, epoch 1900.0 (1)
;;   65- 68  F4.1   s       RAs1900  ?Seconds RA, equinox B1900, epoch 1900.0 (1)
;;       69  A1     ---     DE-1900  ?Sign Dec, equinox B1900, epoch 1900.0 (1)
;;   70- 71  I2     deg     DEd1900  ?Degrees Dec, equinox B1900, epoch 1900.0 (1)
;;   72- 73  I2     arcmin  DEm1900  ?Minutes Dec, equinox B1900, epoch 1900.0 (1)
;;   74- 75  I2     arcsec  DEs1900  ?Seconds Dec, equinox B1900, epoch 1900.0 (1)
;;   76- 77  I2     h       RAh      ?Hours RA, equinox J2000, epoch 2000.0 (1)
;;   78- 79  I2     min     RAm      ?Minutes RA, equinox J2000, epoch 2000.0 (1)
;;   80- 83  F4.1   s       RAs      ?Seconds RA, equinox J2000, epoch 2000.0 (1)
;;       84  A1     ---     DE-      ?Sign Dec, equinox J2000, epoch 2000.0 (1)
;;   85- 86  I2     deg     DEd      ?Degrees Dec, equinox J2000, epoch 2000.0 (1)
;;   87- 88  I2     arcmin  DEm      ?Minutes Dec, equinox J2000, epoch 2000.0 (1)
;;   89- 90  I2     arcsec  DEs      ?Seconds Dec, equinox J2000, epoch 2000.0 (1)
;;   91- 96  F6.2   deg     GLON     ?Galactic longitude (1)
;;   97-102  F6.2   deg     GLAT     ?Galactic latitude (1)
;;  103-107  F5.2   mag     Vmag     ?Visual magnitude (1)
;;      108  A1     ---   n_Vmag    *[ HR] Visual magnitude code
;;      109  A1     ---   u_Vmag     [ :?] Uncertainty flag on V
;;  110-114  F5.2   mag     B-V      ? B-V color in the UBV system
;;      115  A1     ---   u_B-V      [ :?] Uncertainty flag on B-V
;;  116-120  F5.2   mag     U-B      ? U-B color in the UBV system
;;      121  A1     ---   u_U-B      [ :?] Uncertainty flag on U-B
;;  122-126  F5.2   mag     R-I      ? R-I   in system specified by n_R-I
;;      127  A1     ---   n_R-I      [CE:?D] Code for R-I system (Cousin, Eggen)
;;  128-147  A20    ---     SpType   Spectral type
;;      148  A1     ---   n_SpType   [evt] Spectral type code
;;  149-154  F6.3 arcsec/yr pmRA    *?Annual proper motion in RA J2000, FK5 system
;;  155-160  F6.3 arcsec/yr pmDE     ?Annual proper motion in Dec J2000, FK5 system
;;      161  A1     ---   n_Parallax [D] D indicates a dynamical parallax,
;;                                     otherwise a trigonometric parallax
;;  162-166  F5.3   arcsec  Parallax ? Trigonometric parallax (unless n_Parallax)
;;  167-170  I4     km/s    RadVel   ? Heliocentric Radial Velocity
;;  171-174  A4     ---   n_RadVel  *[V?SB123O ] Radial velocity comments
;;  175-176  A2     ---   l_RotVel   [<=> ] Rotational velocity limit characters
;;  177-179  I3     km/s    RotVel   ? Rotational velocity, v sin i
;;      180  A1     ---   u_RotVel   [ :v] uncertainty and variability flag on
;;                                     RotVel
;;  181-184  F4.1   mag     Dmag     ? Magnitude difference of double,
;;                                     or brightest multiple
;;  185-190  F6.1   arcsec  Sep      ? Separation of components in Dmag
;;                                     if occultation binary.
;;  191-194  A4     ---     MultID   Identifications of components in Dmag
;;  195-196  I2     ---     MultCnt  ? Number of components assigned to a multiple
;;      197  A1     ---     NoteFlag [*] a star indicates that there is a note
;;                                     (see file notes)

(def bsc-filename "/Users/tizac/room/toolset/public/data/bsc5.dat")


(def lines (line-seq (io/reader bsc-filename)))

(def line1 (first lines))


(defn parse-bsc-line [line]
  "
;;   61- 62  I2     h       RAh1900  ?Hours RA, equinox B1900, epoch 1900.0 (1)
;;   63- 64  I2     min     RAm1900  ?Minutes RA, equinox B1900, epoch 1900.0 (1)
;;   65- 68  F4.1   s       RAs1900  ?Seconds RA, equinox B1900, epoch 1900.0 (1)
;;       69  A1     ---     DE-1900  ?Sign Dec, equinox B1900, epoch 1900.0 (1)
;;   70- 71  I2     deg     DEd1900  ?Degrees Dec, equinox B1900, epoch 1900.0 (1)
;;   72- 73  I2     arcmin  DEm1900  ?Minutes Dec, equinox B1900, epoch 1900.0 (1)
;;   74- 75  I2     arcsec  DEs1900  ?Seconds Dec, equinox B1900, epoch 1900.0 (1)

;;   26- 31  I6     ---     HD       [1/225300]? Henry Draper Catalog Number
;;   76- 77  I2     h       RAh      ?Hours RA, equinox J2000, epoch 2000.0 (1)
;;   78- 79  I2     min     RAm      ?Minutes RA, equinox J2000, epoch 2000.0 (1)
;;   80- 83  F4.1   s       RAs      ?Seconds RA, equinox J2000, epoch 2000.0 (1)
;;       84  A1     ---     DE-      ?Sign Dec, equinox J2000, epoch 2000.0 (1)
;;   85- 86  I2     deg     DEd      ?Degrees Dec, equinox J2000, epoch 2000.0 (1)
;;   87- 88  I2     arcmin  DEm      ?Minutes Dec, equinox J2000, epoch 2000.0 (1)
;;   89- 90  I2     arcsec  DEs      ?Seconds Dec, equinox J2000, epoch 2000.0 (1)
;;  103-107  F5.2   mag     Vmag     ?Visual magnitude (1)

;;   91- 96  F6.2   deg     GLON     ?Galactic longitude (1)
;;   97-102  F6.2   deg     GLAT     ?Galactic latitude (1)

"
  (let [ref (atom {})
        HR (read-string (string/trim (subs line 0 4)))
        bsc-name (string/trim (subs line 4 14))
        HD (string/trim (subs line 25 31))
        RAh (string/trim (subs line 75 77))
        RAm (string/trim (subs line 77 79))
        RAs (string/trim (subs line 79 83))
        DEd (string/trim (subs line 83 86))
        DEm (string/trim (subs line 86 88))
        DEs (string/trim (subs line 88 90))
        Vmag (string/trim (subs line 102 107))

        RAh1990 (string/trim (subs line 60 62))
        RAm1990 (string/trim (subs line 62 64))
        RAs1990 (string/trim (subs line 64 68))
        DEd1990 (string/trim (subs line 68 71))
        DEm1990 (string/trim (subs line 71 73))
        DEs1990 (string/trim (subs line 73 75))

        GLON (string/trim (subs line 90 96))
        GLAT (string/trim (subs line 96 102))]
    (swap! ref assoc :star/HR HR)
    (when-not (empty? bsc-name)
      (swap! ref assoc :star/bsc-name bsc-name))
    (when-not (empty? HD)
      (swap! ref assoc :star/HD (Integer/parseInt HD)))
    (when-not (empty? RAh)
      (swap! ref assoc :star/RAh (Float/parseFloat RAh)))
    (when-not (empty? RAm)
      (swap! ref assoc :star/RAm (Float/parseFloat RAm)))
    (when-not (empty? RAs)
      (swap! ref assoc :star/RAs (Float/parseFloat RAs)))
    (when-not (empty? DEd)
      (swap! ref assoc :star/DEd (Float/parseFloat DEd)))
    (when-not (empty? DEm)
      (swap! ref assoc :star/DEm (Float/parseFloat DEm)))
    (when-not (empty? DEs)
      (swap! ref assoc :star/DEs (Float/parseFloat DEs)))
    (when-not (empty? Vmag)
      (swap! ref assoc :star/visual-magnitude (Float/parseFloat Vmag)))

    (when-not (empty? RAh1990)
      (swap! ref assoc :star/RAh1990 (Float/parseFloat RAh1990)))
    (when-not (empty? RAm1990)
      (swap! ref assoc :star/RAm1990 (Float/parseFloat RAm1990)))
    (when-not (empty? RAs1990)
      (swap! ref assoc :star/RAs1990 (Float/parseFloat RAs1990)))
    (when-not (empty? DEd1990)
      (swap! ref assoc :star/DEd1990 (Float/parseFloat DEd1990)))
    (when-not (empty? DEm1990)
      (swap! ref assoc :star/DEm1990 (Float/parseFloat DEm1990)))
    (when-not (empty? DEs1990)
      (swap! ref assoc :star/DEs1990 (Float/parseFloat DEs1990)))

    (when-not (empty? GLON)
      (swap! ref assoc :star/GLON (Float/parseFloat GLON)))
    (when-not (empty? GLAT)
      (swap! ref assoc :star/GLAT (Float/parseFloat GLAT)))

    @ref))


(defn load-stars! []
  (let [stars-ref (atom [])]
    (with-open [rdr (io/reader bsc-filename)]
      (doseq [line (line-seq rdr)]
        ;; (println line)
        (when-not (empty? line)
          (let [star (parse-bsc-line line)]
            (swap! stars-ref conj star)))))
    @stars-ref))


(def stars (load-stars!))


(def output-filename "/Users/tizac/room/moxool/public/edn/stars.edn")


(defn run! []
  (spit output-filename (str stars)))


(comment

  (parse-bsc-line line1)  

  (last (read-string (slurp output-filename)))
  (run!)
  )
