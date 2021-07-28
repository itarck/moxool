(ns astronomy.data.tool)



(def universe-tool-1
  #:universe-tool{:astro-scene [:scene/name "solar"]
                  :tool/name "universe tool"
                  :tool/chinese-name "宇宙"
                  :tool/icon "/image/moxool/universe.webp"

                  :entity/type :universe-tool})

(def clock-tool1
  #:clock-tool {:db/id -2
                :status :stop
                :steps-per-second 100
                :step-interval :hour
                :days-per-step (/ 1 24)
                :clock [:clock/name "default"]
                :tool/name "clock control 1"
                :tool/chinese-name "时光机"
                :tool/icon "/image/moxool/clock.jpg"
                :entity/type :clock-tool})


(def info-tool
  #:info-tool {:tool/name "info tool 1"
               :tool/chinese-name "信息查询"
               :tool/icon "/image/moxool/info-tool.jpg"
               :entity/type :info-tool})



(def ppt-0-3
  #:ppt {:pages [#:ppt-page{:image-url "/slides/0.3.tidal-locking/Slide1.jpeg"}
                 #:ppt-page{:image-url "/slides/0.3.tidal-locking/Slide2.jpeg"}
                 #:ppt-page{:image-url "/slides/0.3.tidal-locking/Slide3.jpeg"}]
         :chinese-name "0.3.潮汐锁定"
         :current-page 0})

(def ppt-tool
  #:ppt-tool {:query-type :ppt-by-name
              :query-args ["18.食季"]
              :ppts [#:ppt{:pages [#:ppt-page{:image-url "/slides/1.universe-distance/Slide1.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide2.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide3.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide4.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide5.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide6.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide7.jpeg"}
                                   #:ppt-page{:image-url "/slides/1.universe-distance/Slide8.jpeg"}]
                           :chinese-name "1.天文尺度"
                           :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/2.history/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/2.history/Slide2.jpeg"}]
                            :chinese-name "2.天文学的历史"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/3.one-day/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/3.one-day/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/3.one-day/Slide3.jpeg"}]
                            :chinese-name "3.假如你连续看一整天的天空"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/5.decan/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/5.decan/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/5.decan/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/5.decan/Slide4.jpeg"}
                                    #:ppt-page{:image-url "/slides/5.decan/Slide5.jpeg"}]
                            :chinese-name "5.旬星&黄道十二宫"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/6.sirius/Slide01.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide02.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide03.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide04.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide05.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide06.jpeg"}
                                    #:ppt-page{:image-url "/slides/6.sirius/Slide07.jpeg"}]
                            :chinese-name "6.古埃及人如何看出一年有365.25天"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/7.moon/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/7.moon/Slide2.jpeg"}]
                            :chinese-name "7.假如你连续看一整月的月亮"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/9.metonic/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/9.metonic/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/9.metonic/Slide3.jpeg"}]
                            :chinese-name "9.默冬章：同步阳历和阴历"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/10.sphere/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/10.sphere/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/10.sphere/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/10.sphere/Slide4.jpeg"}]
                            :chinese-name "10.地球是球形"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/11.heliocentric/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/11.heliocentric/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/11.heliocentric/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/11.heliocentric/Slide4.jpeg"}
                                    #:ppt-page{:image-url "/slides/11.heliocentric/Slide5.jpeg"}
                                    #:ppt-page{:image-url "/slides/11.heliocentric/Slide6.jpeg"}]
                            :chinese-name "11.公元前的日心说模型"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/12.earth-size/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/12.earth-size/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/12.earth-size/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/12.earth-size/Slide4.jpeg"}]
                            :chinese-name "12.地球的大小"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/13.armillary/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/13.armillary/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/13.armillary/Slide3.jpeg"}]
                            :chinese-name "13.浑仪：在地球不同地点看太阳"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/14.equatorial/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/14.equatorial/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/14.equatorial/Slide3.jpeg"}]
                            :chinese-name "14.天球坐标系"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/15.constellation/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/15.constellation/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/15.constellation/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/15.constellation/Slide4.jpeg"}
                                    #:ppt-page{:image-url "/slides/15.constellation/Slide5.jpeg"}
                                    #:ppt-page{:image-url "/slides/15.constellation/Slide6.jpeg"}]
                            :chinese-name "15.星座"
                            :current-page 0}
                     #:ppt {:pages [#:ppt-page{:image-url "/slides/18.eclipse-season/Slide1.jpeg"}
                                    #:ppt-page{:image-url "/slides/18.eclipse-season/Slide2.jpeg"}
                                    #:ppt-page{:image-url "/slides/18.eclipse-season/Slide3.jpeg"}
                                    #:ppt-page{:image-url "/slides/18.eclipse-season/Slide4.jpeg"}]
                            :chinese-name "18.食季"
                            :current-page 0}]
              :tool/name "ppt tool"
              :tool/chinese-name "脚本"
              :tool/icon "/image/moxool/ppt.jpg"
              :entity/type :ppt-tool})


(def goto-tool-1
  #:goto-celestial-tool {:tool/name "goto celestial tool"
                         :tool/chinese-name "到达星球"
                         :tool/icon "/image/moxool/goto.jpg"
                         :tool/target {:planet/name "earth"}
                         :entity/type :goto-celestial-tool})


(def constellation-tool-1
  #:constellation-tool{:query-type :all
                       :query-args []

                       :tool/name "constellation-tool"
                       :tool/chinese-name "星座"
                       :tool/icon "/image/moxool/constellation.jpg"

                       :entity/type :constellation-tool})

(def atmosphere-tool-1
  #:atmosphere-tool{:tool/target [:atmosphere/name "default"]
                    :tool/name "atmosphere-tool"
                    :tool/chinese-name "大气层工具"
                    :tool/icon "/image/moxool/atmosphere.jpg"

                    :entity/type :atmosphere-tool})


(def horizon-coordinate-tool
  #:horizon-coordinate-tool {:query-type :one-by-name
                             :query-args-candidates []
                             :query-args []
                             :query-result []

                             :tool/name "horizon-coordinate-tool"
                             :tool/chinese-name "地平坐标系工具"
                             :tool/icon "/image/moxool/horizon-coordinate.jpg"
                             :tool/type :horizon-coordinate-tool
                             :entity/type :horizon-coordinate-tool})

(def astronomical-coordinate-tool
  #:astronomical-coordinate-tool {:query-type :one-by-name
                                  :query-args-candidates []
                                  :query-args []
                                  :query-result []

                                  :tool/name "astronomical-coordinate-tool"
                                  :tool/chinese-name "天球坐标系工具"
                                  :tool/icon "/image/moxool/astronomical-coordinate.jpg"
                                  :tool/type :astronomical-coordinate-tool
                                  :entity/type :astronomical-coordinate-tool})

(def terrestrial-coordinate-tool-1
  #:terrestrial-coordinate-tool {:query-type :one-by-name
                                 :query-args-candidates []
                                 :query-args []
                                 :query-result []

                                 :tool/name "terrestrial-coordinate-tool"
                                 :tool/chinese-name "地球坐标系工具"
                                 :tool/icon "/image/moxool/terrestrial-coordinate.jpg"
                                 :tool/type :terrestrial-coordinate-tool
                                 :entity/type :terrestrial-coordinate-tool})


(def dataset1 [universe-tool-1 clock-tool1 info-tool
               ppt-tool goto-tool-1 constellation-tool-1 atmosphere-tool-1
               horizon-coordinate-tool astronomical-coordinate-tool terrestrial-coordinate-tool-1])