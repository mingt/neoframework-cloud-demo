# config
本地或测试时，配置中心的配置建议统一检出到这里，方便随时查询。

## 说明
* 根据不同的 profile 目录对应不同的环境。
    - local 本地部署
    - test 测试服部署
    - prod 生产服部署
* 关于 local/test 等目录下需要为 git 管理的限制。这是当前版本 spring.cloud Dalston 版本的配置中心的默认设定。猜测可能可以调整配置来避免这个限制，只是这个项目当前未有特别的需要，所以保持不变，照做即可。
* 与上一次相关的，还有配置自动刷新的问题。目前，如果 config 下的配置后，已启动的模块中的相关并不会自动刷新。这个需要按照手册配置好几个相关措施才能实现。
* 已改造 /config-server/src/main/resources/application.yml ，包括：
    - 原直接采用 git 作为配置文件源，现在改为 file 本地文件源。因为考虑到生产服安全考虑可能严格控制外网访问，这样调整能避免影响。
    - 使用 local 和 非 local 针对不同的 profile 自动匹配不同的目录，部署时无需动态调整。这样，只需要在运行时指定 -Dspring.profiles.active=local 或其他 profile 就能自动匹配。
    - local file 源使用相对路径，非 local file 源使用绝对路径。前者使用相对路径时，要求在 local 本地运行 config-server 时应该在指定的位置即 config-server 目录下启动。习惯做法，可以 `cd config-server && bash run-local.sh` 来运行。
* 其他模块引用配置中心的设定位于如 trace-service/src/main/resources/bootstrap.yml
```
spring:
  application:
    name: trace-webservice
  cloud:
    config:
      # 要求 /etc/hosts 增加记录如： 172.18.xxx.yyy configserver 或本地 127.0.0.1 configserver
      uri: http://configserver:8888
```

## 特别说明
当前，个别模块在 /src/main/resources/ 增加了 application-local.yml ，**仅为临时措施**，仅为了本地测试时，不启动 config-server 的情况也有全部配置，从而正常启动模块。 application-local.yml 的内容实际上为 config / config-server 中 application.yml 和各模块自己的配置的并集。它可以被随时删除，只要删除后启动 config-server 并提供有效的模块配置即可。

**在线上部署时，建议去掉 application-local.yml 打包，直接删除就可以。或者保留打包时，保证线上启动不以 local profile 来启动。**
