# @LimitCat 注解
> api 访问频率限制（发送短信、登录失败重新尝试等场景）

## 注解参数
| 参数                 | 类型                                  | 默认值  | 说明                                |
|---------------------|-------------------------------------|------|-----------------------------------|
| scene               | String                              | 方法名  | 频率限制场景                            |
| key                 | String                              | -    | 频率 key（保证全局唯一，支持SpEL）             |
| triggerFor          | Class<? extends RuntimeException>[] | -    | 触发频率限制的异常集合（RuntimeException 子类）  |
| triggerForCodeField | String                              | code | 触发频率限制的异常字段（code、errcode、status等） |
| triggerForCode      | String[]                            | -    | 触发频率限制的异常字段值集合（与 triggerFor 同时使用） |
| message             | String                              | -    | 频率超限提示                            |
| rules               | LimitCatRule[]                      | -    | 使用代码配置规则（优先级：代码指定>配置文件指定）|

## LimitCatRule 注解参数
| 参数        | 类型    | 默认值 | 说明                             |
|-----------|-------|------|--------------------------------|
| interval  | Long  | -  | 周期（秒）                          |
| frequency | Long  | -| 允许执行次数                         |
| message   | String | -| 频率超限提示                         |

## 配置参数
```properties
# 是否启用 LimitCat （默认false）
watchcat.limit.enabled = false

# 频率限制规则
# ----------------------------------------------------------------------
# 规则格式：
# watchcat.limit.scenes.场景（String）.频率（Duration）.frequency = 次数（Long）
# watchcat.limit.scenes.场景（String）.频率（Duration）.message = 频率超限提示（String）
# ----------------------------------------------------------------------
watchcat.limit.scenes.xxx.5m.frequency=5
watchcat.limit.scenes.xxx.5m.message="操作太快，休息一会儿吧（5分钟内限制操作5次）"
```

## 使用示例一：配置文件指定规则
### 1. 引入依赖
```xml
<dependency>
    <groupId>fun.tusi</groupId>
    <artifactId>limit-cat-spring-boot-starter</artifactId>
    <version>1.0.2-sb2</version>
</dependency>
```

### 2. 添加配置
```properties
# 启用 LimitCat
watchcat.limit.enabled = true

# 场景一：获取短信验证码频率限制
# -----------------------------------
# 5分钟 5次
watchcat.limit.scenes.GET_SMSCODE.5m.frequency=5
watchcat.limit.scenes.GET_SMSCODE.5m.message="操作太快，休息一会儿吧（5分钟内限制发送5条）"

# 1小时 10次
watchcat.limit.scenes.GET_SMSCODE.1h.frequency=10
watchcat.limit.scenes.GET_SMSCODE.1h.message="操作太快，休息一会儿吧（1小时内限制发送10条）"

# 1天 15次
watchcat.limit.scenes.GET_SMSCODE.1d.frequency=15
watchcat.limit.scenes.GET_SMSCODE.1d.message="操作太快，休息一会儿吧（1天内限制发送15条）"

# 场景二：短信验证码验证失败频率限制
# -----------------------------------
# 5分钟 5次
watchcat.limit.scenes.SMSCODE_FAIL.5m.frequency=5
watchcat.limit.scenes.SMSCODE_FAIL.5m.message="操作太快，休息一会儿吧（5分钟内验证失败5次）"

# 1小时 10次
watchcat.limit.scenes.SMSCODE_FAIL.1h.frequency=10
watchcat.limit.scenes.SMSCODE_FAIL.1h.message="操作太快，休息一会儿吧（1小时内验证失败10次）"

# 1天 15次
watchcat.limit.scenes.SMSCODE_FAIL.1d.frequency=10
watchcat.limit.scenes.SMSCODE_FAIL.1d.message="操作太快，休息一会儿吧（1天内验证失败15次）"

```

### 3. 使用注解
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

## 使用示例二：代码方式指定规则（不需要在配置文件编写配置）
### 1. 引入依赖
```xml
<dependency>
    <groupId>fun.tusi</groupId>
    <artifactId>limit-cat-spring-boot-starter</artifactId>
    <version>1.0.2-sb2</version>
</dependency>
```

### 2. 添加配置
```properties
# 启用 LimitCat
watchcat.limit.enabled = true
```

### 3：使用注解
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