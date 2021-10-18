# 项目笔记

记录本项目开发中一些关键技术点。

注意：
> 这些技术并不是绝对正确或者唯一，而是目前项目所采用。

后端钉钉CALLBACK报错必须MVN打包:
D:\lutecApp\litemall-admin-api>mvn install:install-file -Dfile=D:\lutecApp\litemall-admin-api\src\main\resources\lippi-oapi-encrpt.jar -DgroupId=com.taobao.top -DartifactId=lippi-oapi-encrpt -Dversion=dingtalk-SNAPSHOT -Dpackaging=jar
D:\lutecApp\litemall-admin-api>mvn install:install-file -Dfile=D:\lutecApp\litemall-admin-api\src\main\resources\taobao-sdk-java-auto_1479188381469-20210716.jar -DgroupId=com.taobao.top -DartifactId=top-api-sdk-dev -Dversion=dingtalk-SNAPSHOT -Dpackaging=jar
## 1. 前后端交互技术

## 2. 错误码
