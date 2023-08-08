
## @LimitCat 注解
> api 访问频率限制（发送短信、登录失败重新尝试等场景）

### 参数清单
| 参数                  | 默认值  | 说明                                |
|---------------------|------|-----------------------------------|
| scene               | 方法名  | 频率限制场景                            |
| key                 | -    | 频率 key（保证全局唯一，支持SpEL）             |
| triggerFor          | -    | 触发频率限制的异常集合（RuntimeException 子类）  |
| triggerForCodeField | code | 触发频率限制的异常字段（code、errcode、status等） |
| triggerForCode      | -    | 触发频率限制的异常字段值集合（与 triggerFor 同时使用） |
| message             | -    | 频率超限提示                            |
| rules               | -    | 使用代码配置规则（优先级：代码指定>配置文件指定）         |

### rules 参数清单
| 参数        | 默认值  | 说明                             |
|-----------|------|--------------------------------|
| interval  | -    | 周期（秒）                          |
| frequency | -    | 允许执行次数                         |
| message   | -    | 频率超限提示                         |

### 引入依赖
```xml
<dependency>
    <groupId>fun.tusi</groupId>
    <artifactId>limit-cat-spring-boot-starter</artifactId>
    <version>1.0.2-sb2</version>
</dependency>
```

### 方式一：配置文件指定规则

```properties
# ----------------------------------------------------------------------
# 规则格式：watchcat.limit.scenes.场景（String）.频率（Duration）= 次数（Long）
# ----------------------------------------------------------------------


# -----------------------------------
# 场景一：获取短信验证码频率限制
# -----------------------------------

# 5分钟 5次
watchcat.limit.scenes.GET_SMSCODE.5m=5
# 1小时 10次
watchcat.limit.scenes.GET_SMSCODE.1h=10
# 1天 15次
watchcat.limit.scenes.GET_SMSCODE.1d=15


# -----------------------------------
# 场景二：短信验证码验证失败频率限制
# -----------------------------------
# 5分钟 5次
watchcat.limit.scenes.SMSCODE_FAIL.5m=5
# 1小时 10次
watchcat.limit.scenes.SMSCODE_FAIL.1h=10
# 1天 15次
watchcat.limit.scenes.SMSCODE_FAIL.1d=15

```
#### 代码中使用

```java
// 场景一：获取短信验证码频率限制
@LimitCat(scene = "GET_SMSCODE", key = "#getSmsCodePing.phone")
public Result<GetSmsCodePong> getSmsCode(@Valid GetSmsCodePing getSmsCodePing) {
    
    return Result.ok().data(...);
    
}


// 场景二：短信验证码验证失败频率限制
// 指定异常
@LimitCat(scene = "SMSCODE_FAIL", key = "#checkSmsCodePing.smsCode", triggerFor = BusinessException.class)
public Result<CheckSmsCodePong> CheckSmsCode(@Valid CheckSmsCodePing checkSmsCodePing) {

    return Result.ok().data(...);

}

// 指定异常 + 指定异常Code
@LimitCat(scene = "SMSCODE_FAIL", key = "#checkSmsCodePing.smsCode", triggerFor = BusinessException.class, triggerForCode = {"1000","1001"})
public Result<CheckSmsCodePong> CheckSmsCode(@Valid CheckSmsCodePing checkSmsCodePing) {

    return Result.ok().data(...);

}
```

### 方式二：代码方式指定规则（不需要在配置文件编写配置）
```java
// 指定异常触发
@LimitCat(scene = "SMSCODE_FAIL", key = "#checkSmsCodePing.smsCode", triggerFor = BusinessException.class, 
        rules = {
            @LimitCatRule(interval = 60 , frequency = 1 , message = "1分钟只能错误验证1次"),
            @LimitCatRule(interval = 300, frequency = 10, message = "5分钟只能错误验证10次")
        }
)
public Result<CheckSmsCodePong> CheckSmsCode(@Valid CheckSmsCodePing checkSmsCodePing) {

        return Result.ok().data(...);

}
```