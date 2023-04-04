# moxool

## Project Description:

- This project is a demonstration model of the solar system's planetary system, with an error margin of approximately an hour for solar and lunar eclipses within 100 years.
- This was a hobby project during the summer of 2021, using code scripts that were utilized while recording [the Astronomy History Series videos](https://space.bilibili.com/502118781/channel/seriesdetail?sid=758365). The project encapsulates some typical scenarios for astronomy enthusiasts to learn and communicate.
- The 3D models in this project are downloaded from [NASA's official website](https://solarsystem.nasa.gov/solar-system/our-solar-system/overview/), and the project is for learning and communication purposes only. Commercial use is prohibited.
- Desktop versions are downloaded from [here](https://github.com/itarck/moxool/releases/tag/0.1.0-alpha) by selecting the corresponding operating system.

## Development-related:
- The project is written in ClojureScript, using libraries such as [react-three-fiber](https://github.com/pmndrs/react-three-fiber), [reagent](https://github.com/reagent-project/reagent), [datascript](https://github.com/tonsky/datascript), [posh](https://github.com/denistakeda/posh), etc.
- The current version has an improper state management method for the 3D models, which leads to multiple mountings of the 3D models, resulting in performance issues. Later, it was discovered that using React hooks for state management could improve performance by 10-50 times, but the old code is not yet modified.


## 项目说明
- 本项目是一个太阳系行星系统的演示模型，100年内的日月食误差大约在小时级别。
- 这是在2021年暑假的业余项目，录制[天文学史系列视频](https://space.bilibili.com/502118781/channel/seriesdetail?sid=758365)时用到过的代码脚本，封装了一些典型场景。并非成熟软件，仅供天文爱好者学习交流。
- 本项目中的3D模型从[NASA官网](https://solarsystem.nasa.gov/solar-system/our-solar-system/overview/)下载，本项目仅供学习交流，禁止商用。
- 桌面版本从[这里](https://github.com/itarck/moxool/releases/tag/0.1.0-alpha)选择对应操作系统下载，相关可交互的[场景说明](https://github.com/itarck/moxool/blob/main/SCENES.md).

## 开发相关：
- 用了clojurescript编写, 用到了[react-three-fiber](https://github.com/pmndrs/react-three-fiber), [reagent](https://github.com/reagent-project/reagent), [datascript](https://github.com/tonsky/datascript), [posh](https://github.com/denistakeda/posh)等库
- 当前版本对3D模型的状态管理方式不合理，导致3D模型多次挂载，存在性能问题。后来发现如果用react hooks来管理状态变更，可以提升10-50倍，只是老代码暂时不想改了。


