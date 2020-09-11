# neoframework-cloud-demo
演示 Spring Cloud 基础功能和若干增强，如注册中心，配置中心， 认证中心等。

## 特性
* 以 Spring Cloud starter 项目为例。
* Gradle 构建，多层模块结构。同时演示一份微服务模块的代码结构。
* 构建时，自动解释版本号，计算和设定版本信息，添加相关编译信息；提供版本查询 REST 接口。
* styleguide 一份代码风格规范及说明。
* auth-server 认证中心增强：集成认证，多端登录检测，扫码登录等。

## 缘由
项目 [anilallewar/microservices-basics-spring-boot](https://github.com/anilallewar/microservices-basics-spring-boot) 较丰富的文档很好地诠释了微服务的概念及相关，同时可作为生产可用的微服务框架项目基础骨架。当然，从任何一个骨架项目到成熟的生产版本仍然有大量的组件内容需要补充或扩展，另外还可能有现成的开发框架和工具需要整合，而本项目为此给出一些提示和参考样例。

## 用法

### 微服务概念和文档
建议从上面 `microservices-basics-spring-boot` 的文档开始了解微服务相关的概念及其实现。（为了方便国内访问，正常查看配图，目前在 Gitee 做了一份克隆： [ahming/microservices-basics-spring-boot](https://gitee.com/m1024ing/microservices-basics-spring-boot)）。其他有关的文档有：

* [实施微服务需要哪些基础框架](https://mp.weixin.qq.com/s?__biz=MjM5MDE0Mjc4MA==&mid=400645575&idx=1&sn=da55d75db55117046c520de88dde1123&3rd=MzA3MDU4NTYzMw==&scene=6#rd)
* [Spring Cloud Netflix 概览和架构设计](http://codin.im/2016/12/15/spring-cloud-architect-intro/)
* [spring.io/projects/spring-cloud](https://spring.io/projects/spring-cloud) 注意 Spring Cloud 可理解为一整套基于 Spring 的微服务架构实现，背后由众多可选的组件组成。对这些组件有一个基本认识有助于更好的应用 Spring Cloud 。同时可留意 Spring Cloud Netflix 和后来者 Spring Cloud Alibaba 的异同，它们都提供了一站式微服务方案实现。

### 编译和使用
（1） microservices-basics-spring-boot 有一个整体介绍，见 "Using the application" 部分，还有各模块下的 README.md 也有很好的运行提示。如果在本地运行，也可参考下面几点说明。

（2） build-all-projects.sh 直接运行 ```bash build-all-projects.sh``` 或参考其中的编译命令单独编译。

    #!/bin/sh

    # build_type 有 release 或 test 等

    cd config-server; ./gradlew clean build; cd ..
    cd webservice-registry; ./gradlew clean build; cd ..
    cd base-common; ./gradlew clean build publishToMavenLocal -P build_type=release; cd ..
    cd auth-server-common; ./gradlew clean build; cd ..
    cd auth-server; ./gradlew clean build; cd ..
    cd trace-service; ./gradlew clean build -P build_type=release; cd ..

（3）拉取外部配置仓库。
本项目的 config-server 配置中心做了一个小调整，支持从本地文件系统指定外部配置目录。 local 环境约定为 config/local 相对路径，其他环境
可指定为其他路径。调整见 config-server/src/main/resources/application.yml 。

    cd config/
    git clone https://github.com/mingt/neoframework-cloud-demo-config.git local

（4）在运行业务模块前把配置中心和注册中心、认证中心模块先启动。

    打开一个终端窗口
    cd config-server; bash run-local.sh
    再打开新终端窗口：
    cd webservice-registry; bash run-local.sh
    类似，认证中心：
    cd auth-server; bash run-local.sh

（5） 运行业务模块 trace-service 。请参照 ```trace-service/run-local.sh``` 的启动命令行，可能需要调整 jar 的版本号。

    cd trace-service; bash run-local.sh
    或者:
    java -jar build/libs/basic-trace-webservice-0.0.1.jar
    或根据实现版本号调整：
    java -jar build/libs/basic-trace-webservice-1.0.0.jar

（6） 访问版本查询接口获取版本信息（这个接口无需 access token 认证就能访问，能访问表示模块正常启动） ```curl http://localhost:8085/trace/sys/version```

    {
        "version": "2.9.0",
        "versionDetail": "2.9.0 gcf79aeb b202008131107",
        "versionCode": 20900,
        "name": "trace-service"
    }

其中， versionDetail 包括编译时间 b202008131107 （前缀为 b）， 当前编译时 Git 最新提交ID gcf79aeb （前缀为 g）。
versionCode 为自动计算的整型版本号，方便版本上报、版本比较等。

需要使用 access token 访问的测试接口可见 [docs/scripts/demo](docs/scripts/demo) 小脚本。 Access token 的发放和请求获取可参考 [auth-server/README.md](auth-server/README.md) 。

（7）在部署统一网关之前，本地或线上部署可以借助 Nginx 。一个样例配置可见另一项目详细说明： [docs-best-practice](https://github.com/mingt/docs-best-practice)

    docs-best-practice 是开发项目 docs 目录的一些最佳实践：常用文档、结构、工具、脚本，Java 代码风格规范等

### FAQ

（1） 什么情况下版本查询接口显示 unknown version ？

读取版本信息依赖于 version.properties 的自动生成内容。这个文档只有在 Gradle 或 Maven 构建过程中带上 `-P build_type=` 参数指定值才生成，参见如
Gradle 的 processResources.doLast 任务。如果从 IDE 如 Intellij IDEA 的 Spring Boot Run/Debug Configurations 启动 trace-service 则因为
缺少 version.properties ，所以显示 unknown version 。如果需要这种运行也能显示有意义的值，可以在使用编译脚本构建后
从 trace-service/build/resources/main/version.properties 复制一份到
trace-service/src/main/resources/version.properties ，重新运行。但注意这个 version.properties 仅供测试参考，不需要提交到代码库。

（2） 运行日志不停刷新 Connection refused: connect 及 Cannot execute request on any known server 错误？

前者部分与 org.springframework.cloud:spring-cloud-starter-config 配置中心依赖有关。为了演示方便，resources 下增加了 application-local. yml 可
略去配置中心启动，同时也没有外部运行配置中心，所以默认尝试连接配置中心 Fetching config from server at: http://localhost:8888 时报错。

前者另一部分和后者与注册中心有关。 注解 EnableEurekaClient 决定了模块作为客户端注册到注册中心，目前没有外部运行注册中心，所以不断尝试重连而报错。

如果的确需要去除这些运行报错，请按参考文档把配置中心、注册中心等先运行起来。

（3） 下一步还能做什么？

* base-common 整理为两部分： Spring 相关和非 Spring 相关部分，即系统与业务领域严格独立分开，减少模块耦合。
* 样例模块 trace-service 整理为两部分： 独立的 SDK 模块，只包含业务相关 Model 类，Service 声明类等。微服务框架下，各业务模块完整性自备，不必要把自身的业务 Model 类等统一放在大而杂的 Common 模块。
* 统一网关 Gateway。注意 Spring Cloud Netflix Zuul 和 Spring Cloud Gateway 的异同及转变。
* 升级版本 SpringBootVersion 从 1.5.X 到 2.X 。
* 更多的面向或接近生产可用的组件。

## 其他

### Maven 版本
见另一个代码库。

### Spring Cloud starter
* 注册中心，配置中心，认证中心等可略去启动。 本项目的业务模块配置增加 application-local. yml 支持本地 Spring Boot 配置项。线上启动不使用 local 启动不会有影响。
* 源于项目： [anilallewar/microservices-basics-spring-boot](https://github.com/anilallewar/microservices-basics-spring-boot) 致谢原作者，其最近更新也为 k8s 部署提供了一个方向。
* 为了方便国内访问，正常查看配图，目前在 Gitee 做了一份克隆： [ahming/microservices-basics-spring-boot](https://gitee.com/m1024ing/microservices-basics-spring-boot)

### 测试请求演示

<pre>
cd docs/scripts/demo/
bash curl-sys-version.sh
bash curl-trace-statCrashLog-without-token.sh
bash curl-trace-statCrashLog.sh
</pre>

### 代码风格 styleguide
详情见 [docs/styleguide/README.md](/docs/styleguide/README.md) 。
但它只是 [docs-best-practice](https://github.com/mingt/docs-best-practice) 相关内容的一个历史版本，建议跳转到后者查看可能更新的内容。

    docs-best-practice 是开发项目 docs 目录的一些最佳实践：常用文档、结构、工具、脚本，Java 代码风格规范等
