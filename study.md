## 一、admin 登录模块
### 登录用户密码验证
SysLoginController SysLoginService 登录校验方法 在其他接口调用时会验证token是否存在
### 登录验证码的应用
####业务逻辑：首先判断是否启用验证码，而后利用加密、拼接等方法生成验证码存放到redis缓存中
1.CaptchaController 具体的验证码生成步骤 2.CaptchaConfig 验证码的图片大小设置 3.token 的创建
```xml
<!--验证码依赖-->
<dependency>
    <groupId>com.github.penggle</groupId>
       <artifactId>kaptcha</artifactId>
         <exclusions>
            <exclusion>
               <artifactId>javax.servlet-api</artifactId>
                  <groupId>javax.servlet</groupId>
            </exclusion>
         </exclusions>
</dependency>
```
## common 通用模块
## framework 项目配置模块
## generator mybatis逆向工程
## quartz 定时任务配置
## system 系统管理模块