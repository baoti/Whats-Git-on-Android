Whats Git on Android
====================

一个用于 **跨提供商** 查看仓库/项目的 Android 应用.

一个 **烧脑** 的开源Android项目.


Source
------

由于使用了 **烧脑组合**, 不建议开发新手阅读项目源码.

源码特点:

    - 使用 Gradle 构建项目
    - 不兼容 Eclipse 结构 (懒得兼容)
    - 多模块. 最主要是 git 与 app, coding 与 oscGit 用于接入特定提供商
    - 使用 AccountManager 实现账号管理 .
    - XXX 和 WTF
    - 使用 Picasso 加载图片
      (目前只声明了依赖)
    - 使用 Retrofit 请求 RESTful API
    - 使用 Timber 显示 LOG
      (我很懒, 所以用它)
    - 使用 Butterknife 绑定 view 与 事件
      (我很懒, 所以用它)
    - 单元测试与指令测试(或指令测试) 共存
      (目前只声明了依赖)
    - 使用了烧脑库

已使用的烧脑库有:

    - Dagger by square: 依赖管理, 特点: 基于模块声明与注解. 烧脑指数 ???
    - RxJava by ReactiveX: Java响应式扩展, 特点: 函数式. 烧脑指数 ????


暂不考虑的设计有:

    - Mortar by square: All in one activity, Path Screen View Presenter.
    - MVC, MVP, MVVM


Pull Requests
-------------

项目代码尚未稳定, 如需 PR , 请先添加一条 讨论, 以说明意图.

欢迎 在 讨论中 讨论/吐槽 本项目.


题外话
------

以下是一些与本项目不相关的内容.


### 想看 MVP 项目?

我此前的一个用于探索 **烧脑Android** 的应用:

<https://github.com/baoti/Pioneer>

其中包含了: Data/Domain/UI 分层, MVP, 依赖注入, EventBus, AccountManager 等等.


### 想用 Gradle, 又得兼容 Eclipse ?

在此附一份源码结构的通用配置, 特别适用于为已存在的 Eclipse 项目添加 Gradle 构建:

    androidSourceSetsEclipseStyle = {
        manifest.srcFile 'AndroidManifest.xml'
        java.srcDirs = ['src']
        resources.srcDirs = ['src']
        aidl.srcDirs = ['src']
        renderscript.srcDirs = ['src']
        res.srcDirs = ['res']
        assets.srcDirs = ['assets']
        jni.srcDirs = []
        jniLibs.srcDirs = ['libs']
    }

使用方法:

    android {
        sourceSets {
            main androidSourceSetsEclipseStyle
        }
    }


License
-------

Coding is available under the MIT license. See the MIT-LICENSE.txt file for more info.
