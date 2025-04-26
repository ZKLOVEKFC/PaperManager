!# 期刊管理系统 (Paper Management System)
这是一个基于SpringBoot+Vue3开发的期刊管理系统。旨在学习和实践后端开发技术的同时，开发一个记录和管理个人的论文投稿情况的web应用。
该系统主要面向个人研究者设计。

## ✨ 主要功能 (Features)
* **文献管理:**
    * 记录上传、修删除目录信息。
    * 记录上传、修删除Paper信息。
* **期刊/文章浏览:** 按目录、状态检索Paper功能]。
* **个人中心:** 用户查看/修改个人信息。
* **特色功能：**AI总结Paper的创新点。

## 🛠️ 技术栈 (Technology Stack)
* **后端:**
    * Java 17
    * Spring Boot 3.4.3
    * MyBatis 3.0.4
    * Maven 3.6
* **数据库:**
    * MySQL 8.0.36
* **前端:**
    * HTML / CSS / JavaScript
    * Vue3
    * element-plus
* **其他:**
    *Lombok

## 🖼️ 系统截图

*将你的系统截图放在这里，可以展示登录页、主界面、稿件列表、用户管理等关键页面。*

*登录页面*
截图/登录.png

*个人面板*
截图/个人中心.png

*稿件列表页面*
截图/Paper管理.png

*AI总结摘要*
截图/AI总结摘要.png

其他功能截图都在文件夹《截图》下，此处不一一列举

## 🚀 安装与运行 (Installation & Setup)

**环境要求 (Prerequisites):**

* JDK >= 23
* Maven >= 3.6
* MySQL >= 8.0
* npm >= 10.9.2
* redis >= 5.0.14

**步骤 (Steps):**

1.  **克隆仓库:**
    ```bash
    git clone https://github.com/ZKLOVEKFC/PaperManager.git
    cd PaperManger

2.  **数据库配置:**
    * 在你的数据库中创建一个新的数据库，例如 `journal_db`。
    * 修改 `src/main/resources/application.yml文件中的数据库连接信息：
        ```properties
        # 例如 application.properties中
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/journal_db
    username: [你的账户]
    password: [你的密码]
  data:
    redis:
      host:***
      port:***
        # ... 你的redis配置信息，一般端口默认6379 ...

3.  **使用 Maven构建项目:**
    ```bash
    mvn clean install

4.  **运行项目 (Run the application):**
    ```bash
    # 使用 Maven Spring Boot 插件
    mvn spring-boot:run

    # 或者直接运行生成的 JAR 包
    java -jar target/[你的项目名]-[版本号].jar

    # 或者使用 Gradle
    ./gradlew bootRun
    ```
    
5.  **配置npm服务器（假设npm已安装）:**
      命令为run
      脚本为dev
    
6.  **访问系统 (Access the system):**
     分别启动PaperMangerApplication、redis和dev
    在浏览器中打开 `http://localhost:5173/`。



## 👤 作者 (Author)

* **[张凯]**
* GitHub:https://github.com/ZKLOVEKFC
* 邮箱：zklovekfc@163.com

---
