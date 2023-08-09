# @LogCat 注解
> api 请求和响应日志打印、持久化。「 _参数验证、类型转换等异常无法获取日志_ 」
---
## 注解参数
| 参数          | 类型      | 默认值     | 说明                 |
|-------------|---------|--|--------------------|
| bid         | String  | -       | 自定义参数（支持 SpEL）     |
| actionGroup | String  | default | 操作分组               |
| action      | String  | default | 操作                 |
| callback    | Boolean | false   | 是否启用日志通知（用于持久化日志等） |
| print       | Boolean       | true    | 是否打印日志             |
| printOrig   | Boolean       | true    | 是否在日志中打印原始请求       |

## 配置参数
```properties
# 是否启用 LogCat （默认false）
watchcat.log.enabled = false

# 获取客户端真实ip的key request.getHeader("ipKey") ，为空则默认使用 request.getRemoteAddr() 获取
watchcat.log.ip-key = 
```

## 使用示例
### 1. 引入依赖
```xml
<dependency>
    <groupId>fun.tusi</groupId>
    <artifactId>log-cat-spring-boot-starter</artifactId>
    <version>1.0.2-sb2</version>
</dependency>
```

### 2. 添加配置
```properties
# 启用 LogCat
watchcat.log.enabled = true
```

### 3. 使用注解
```java
@LogCat
@PostMapping("login")
public Result<AdminLoginPong> loginByAccount(@Valid AdminLoginPing adminLoginPing) {

        return Result.ok().data(...);

}
```