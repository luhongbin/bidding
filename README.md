# bidding

集成钉钉的+T100 招投标系统。

bidding = Spring Boot后端 + Vue管理员前端 + 微信小程序用户前端 + Vue用户移动端+动态数据库+T100

## 项目实例

### 招投标实例

* 登录模块实例
![](./doc/pics/readme/PC端登录画面.png)
![](./doc/pics/readme/钉钉开发者后台.png)
> 注意：钉钉自动登录,需要钉钉免费配置(https://developers.dingtalk.com/)
> 钉钉用户信息 由定时发送模块完成 更新bidding用户信息数据库(https://github.com/luhongbin/sendweixin)

* 招标文件模板模块实例
![](./doc/pics/readme/招标模板.png)
![](./doc/pics/readme/招标商品浏览.png)
![](./doc/pics/readme/招标审批画面.png)

> 注意：各个审批环境 由状态设置 区分审批进程。

* 投标模块实例
![](./doc/pics/readme/投标报价.png)

> 注意：各个审批环境 由状态设置 区分审批进程。

### 微信小商场实例

* litemall-wx模块实例

![](./doc/pics/readme/扫码_搜索联合传播样式-微信标准绿版.png)

> 注意：此实例是测试小商场，开发者请不要尝试购买商品、付款、退款操作。


### 管理后台实例

![](./doc/pics/readme/admin-dashboard.png)

1. 浏览器打开，输入以下网址: [https://xx.175.134.14/#/login]

> 注意：此实例只是实际管理后台，不提供用户名密码

## 项目代码

* [GitHub](https://github.com/luhongbin/Bidding-)

## 项目架构
![](./doc/pics/readme/project-structure.png)

## 技术栈

> 1. Spring Boot
> 2. Vue
> 3. 微信小程序

![](doc/pics/readme/technology-stack.png)

## 功能

### 小商城功能

* 首页
* 专题列表、专题详情
* 分类列表、分类详情
* 品牌列表、品牌详情
* 新品首发、人气推荐
* 优惠券列表、优惠券选择
* 团购
* 搜索
* 商品详情、商品评价、商品分享
* 购物车
* 下单
* 订单列表、订单详情、订单售后
* 地址、收藏、足迹、意见反馈
* 客服

### 管理平台功能

* 采购员管理
* 供应商管理
* 会员管理
* 商城管理
* 商品管理
* 推广管理
* 系统管理
* 配置管理
* 统计报表

## 快速启动

1. 配置最小开发环境：
    * [MySQL](https://dev.mysql.com/downloads/mysql/)
    * [JDK1.8或以上](http://www.oracle.com/technetwork/java/javase/overview/index.html)
    * [Maven](https://maven.apache.org/download.cgi)
    * [Nodejs](https://nodejs.org/en/download/)
    * [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
    
2. 数据库依次导入litemall-db/sql下的数据库文件
    * litemall_schema.sql
    * litemall_table.sql
    * litemall_data.sql

3. 启动小商场和管理后台的后端服务

    打开命令行，输入以下命令
    ```bash
    cd litemall
    mvn install
    mvn clean package
    java -Dfile.encoding=UTF-8 -jar litemall-all/target/litemall-all-0.1.0-exec.jar
    ```
    
4. 启动管理后台前端

    打开命令行，输入以下命令
    ```bash
    npm install -g cnpm --registry=https://registry.npm.taobao.org
    cd litemall/litemall-admin
    cnpm install
    cnpm run dev
    ```
    此时，浏览器打开，输入网址`http://localhost:9527`, 此时进入管理后台登录页面。
    
    必须设置相关参数,比如钉钉的,通过寻找corpid,设置有关参数 才能完成钉钉自动登录 发送消息等动作
    
    同理微信发送消息 等相关参数都要查阅对方官方文档 填写必要信息
    
5. 启动小商城前端
   
   这里存在两套小商场前端litemall-wx和renard-wx，开发者可以分别导入和测试：
   
   1. 微信开发工具导入litemall-wx项目;
   2. 项目配置，启用“不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书”
   3. 点击“编译”，即可在微信开发工具预览效果；
   4. 也可以点击“预览”，然后手机扫描登录（但是手机需开启调试功能）。
      
   注意：
   > 这里只是最简启动方式，而小商场的微信登录、微信支付等功能需开发者设置才能运行，

        
## 开发计划

目前项目开发中，存在诸多不足，以下是目前规划的开发计划。

V 1.0.0 完成以下目标：

1. 除了部分功能（如优惠券等），小商城的优化和改进基本结束；
2. 管理后台基本实现所有表的CRUD操作；
3. 后端服务能够对参数进行检验。
4. 小商城和管理后台完成所有基本业务；
5. 管理后台实现统计功能、日志功能、权限功能；
6. 业务代码和细节代码进行调整优化；
7. 实现招投标的制作 审批功能,实现与阿里钉钉的集成,自动登录 与发送审批通知；

V 2.0.0 完成以下目标：

1. 管理后台一些辅助功能
2. 后端服务加强安全功能、配置功能
3. 缓存功能以及优化一些性能

## 警告

> 1. 本项目仅用于学习练习
> 2. 本项目还不完善，仍处在开发中，不承担任何使用后果
> 3. 本项目代码开源[MIT](./LICENSE)，项目文档采用 [署名-禁止演绎 4.0 国际协议许可](https://creativecommons.org/licenses/by-nd/4.0/deed.zh)

## 致谢

本项目基于或参考以下项目：

1. [nideshop-mini-program](https://github.com/tumobi/nideshop-mini-program)

   项目介绍：基于Node.js+MySQL开发的开源微信小程序商城（微信小程序）

   项目参考：
   
   1. bidding项目数据库基于nideshop-mini-program项目数据库；
   2. bidding项目的litemall-wx模块基于nideshop-mini-program开发。

2. [vue-element-admin](https://github.com/PanJiaChen/vue-element-admin)
  
   项目介绍： 一个基于Vue和Element的后台集成方案
  
   项目参考：litemall项目的litemall-admin模块的前端框架基于vue-element-admin项目修改扩展。

3. [mall-admin-web](https://github.com/macrozheng/mall-admin-web)

   项目介绍：mall-admin-web是一个电商后台管理系统的前端项目，基于Vue+Element实现。

   项目参考：bidding项目的litemall-admin模块的一些页面布局样式参考了mall-admin-web项目。

4. [biu](https://github.com/CaiBaoHong/biu)

   项目介绍：管理后台项目开发脚手架，基于vue-element-admin和springboot搭建，前后端分离方式开发和部署。

   项目参考：bidding项目的权限管理功能参考了biu项目。

5. [litemall](https://github.com/linlinjava/litemall)

   项目介绍：基于有赞 litemall 的后台管理。

   项目参考：bidding项目基于litemall项目开发。


## 问题

![](doc/pics/readme/bidding群二维码.png)

 * 开发者有问题或者好的建议可以用Issues反馈交流，请给出详细信息
 * 在开发交流群中应讨论开发、业务和合作问题
 * 如果真的需要QQ群657774700里提问，请在提问前先完成以下过程：
    * 请阅读[提问的智慧](https://github.com/ryanhanwu/How-To-Ask-Questions-The-Smart-Way/blob/master/README-zh_CN.md)；
    * 请百度或谷歌相关技术；
    * 请查看相关技术的官方文档，例如微信小程序的官方文档；
    * 请提问前尽可能做一些DEBUG或者思考分析，然后提问时给出详细的错误相关信息以及个人对问题的理解。

## License

[MIT](https://github.com/luhongbin/Bidding-/blob/master/LICENSE)
Copyright (c) 2021-present luhongbin
