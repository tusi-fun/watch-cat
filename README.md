# watch-cat（看家猫）
* 封装-请求中 字符串格式的时间戳 转对应格式（LocalDateTime 、Localdate、LocalTime）
* ------------------------------
* @LogCat：api 请求和响应日志打印、持久化
* @LimitCat：api 流控
* @SecretCat：api 请求参数解密 和 响应加密
* @SensitiveCat：响应中的敏感参数脱敏
* ------------------------------
* AreaDetailConverter：将请求中的地区（省市县区）code，解析为地区对象
* StringTrimConverter：对请求中的String参数前后去空格

### 引入依赖
```xml
<dependency>
    <groupId>com.cat</groupId>
    <artifactId>spring-boot-starter-watch-cat</artifactId>
    <version>1.0.8</version>
</dependency>
```

### @LimitCat 使用说明
#### 方式一：配置文件指定规则
* 规则配置

规则：watchcat.limit.scenes.场景（String）.频率（Duration）=次数（Long）
```properties
#----- 场景一：发送短信验证码 -----
# 5分钟限制5次
watchcat.limit.scenes.smsSend.5m=5
# 1小时限制30次
watchcat.limit.scenes.smsSend.1h=30
# 1天限制50次
watchcat.limit.scenes.smsSend.1d=50

#----- 场景二：短信验证码错误 -----
# 5分钟限制3次
watchcat.limit.scenes.smsWrong.5m=3
```
* 代码中使用
```java
// Spel 表达式获取参数
// 无异常，触发
@LimitCat(scene = "smsWrong", key = "#loginSmsCodeVO.outId+'-'+#loginSmsCodeVO.smsCode")

// 指定异常触发
@LimitCat(scene = "smsWrong", key = "#loginSmsCodeVO.outId+'-'+#loginSmsCodeVO.smsCode", triggerFor = BusinessException.class)

// 指定异常 + 异常Code触发
@LimitCat(scene = "smsWrong", key = "#loginSmsCodeVO.outId+'-'+#loginSmsCodeVO.smsCode", triggerFor = BusinessException.class, triggerForCode = {"6000","6001"})
```
#### 方式二：代码方式
```java
// 指定异常触发
@LimitCat(scene = "smsWrong", key = "#loginSmsCodeVO.outId+'-'+#loginSmsCodeVO.smsCode", triggerFor = BusinessException.class, 
        rules = {
		    @LimitCatRule(interval = 60 ,frequency = 1 ,message = "1分钟只能错误验证1次"),
            @LimitCatRule(interval = 300,frequency = 10,message = "5分钟只能错误验证10次")
        }
)
```

* @LimitCat 注解说明
  - scene 和配置文件中的场景一一对应
  - key 流控依据参数 uid、token、phone 等，支持 Spel 表达式
  - triggerFor 触发流控计数的异常数组（必须是 RuntimeException 的子类）
  - triggerForCode 触发流控计数的异常 Code 数组（与 triggerFor 同时使用）
  - rules 流控规则配置（代码方式使用）

### @LogCat 使用说明

* 在代码中使用
```java
@LogCat(actionGroup = "订单管理功能组", action = "删除订单")

@LogCat(actionGroup = "订单管理功能组", action = "删除订单", enableEvent = false)
```
* @LogCat 参数说明
  - actionGroup 操作分组
  - action 操作
  - enableEvent 是否启用日志事件通知（默认启用，便于持久化日志）