# OKsimple：简单易用，扩展性强，基于okhttp5.X版本和kotlin的网络请求框架。
### 目前实现的功能
- get，post，postjson，表单提交等常规请求
- 支持HEAD，DELETE，PATCH，PUT等其他http method
- 支持同步异步请求
- 强大的请求策略系统，支持定时请求，轮询请求，请求失败切换域名等等复杂操作
- 多文件下载，支持进度监听，支持断点续传
- 多文件上传，支持进度监听
- 支持接入glide实现glide图片加载进度监听
- 全局请求头和全局参数的添加
- 每次请求默认添加tag，支持自定义tag
- 每次请求支持自定义CacheControl
- 对短时间多次相同的请求进行了拦截处理，有效防止用户多次点击导致的重复请求
- 在没有网络的情况下，可以添加force cache
- 基于kotlin但也对java做了支持
- 同步okhttp最新版本，支持brotli compression


### 项目介绍
强大好用的网络请求框架。基于okhttp5.X和kotlin，目前更新到okttp5.0.0-alpha.12。采用链式回调，不用再写一堆接口和类，轻量级，扩展性强，欢迎star，也欢迎提issue，pr或者其他建议。

### minSdkVersion
Oksimple目前minsdkversion是21，一些用我这个lib朋友希望我能支持到19，我的想法是oksimple没有支持19的打算。首先okhttp 3.13以后的版本，对minsdkversion的版本要求都变成21了。原因的话，官方有给出解释，大意是说出于https安全方面的考虑，具体可以参考[这篇文章](https://medium.com/square-corner-blog/okhttp-3-13-requires-android-5-818bb78d07ce)。如果你还想兼容android 5.0以下的版本，可以使用okhttp的3.12.x版本。但oksimple目前是基于5.X开发，没有针对 okhttp 3.12版本重写的打算。这里说一点题外话，其实像微信的最新版，minsdkversion都变成21了。支持到19，中间的20是kitkat wear，是针对智能手表一类的设备的。如果你做的是针对手机的app，那kitkat wear其实和你没什么关系。所以21到19，看似多支持了2个版本，其实只是多支持了一个版本。为了19这个已经落后很多年的版本，有些时候你需要多写很多代码和适配，我个人感觉是没有必要的。

### demo
github上的demo演示里，用的都是第三方的地址。但功能的话，都是经过实际使用测试的。如果确实有问题的话，欢迎提issue。


### 接入方法 以最新版的Android studio为例，你需要在settings.gradle.kts中进行配置，主要是引入jitpack
```
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}
```
然后在app的build.gradle
```
implementation 'com.github.GateOfTruth:OkSimple:3.4.1'
```
##### 或者你也可以fork一下，自己编译，oksimple只依赖了okhttp，没有其他依赖，编译出来的aar包也只有30几KB，同时，Oksimple采用api引入okhttp，所以你引入了oksimple的话，就不用重新引入okhttp了。

### 使用方法
使用方法现移动到[wiki](https://github.com/AllenXiao1994/OkSimple/wiki)，更加直观和便于查阅。请点击查看。

### 更新日志
[点击这里](https://github.com/GateOfTruth/OkSimple/releases)查看每次版本更新


License
-------

```
Copyright 2019 GateOfTruth, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```




