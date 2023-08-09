# @LogCat 注解
> api 请求和响应日志打印、持久化。「 _参数验证、类型转换等异常无法获取日志_ 」

## @LogCat 注解说明
| 参数          | 默认值     | 说明                 |
|-------------|--|--------------------|
| bid         | -       | 自定义参数（支持 SpEL）     |
| actionGroup | default | 操作分组               |
| action      | default | 操作                 |
| callback    | false   | 是否启用日志通知（用于持久化日志等） |
| print       | true    | 是否打印日志             |
| printOrig   | true    | 是否在日志中打印原始请求       |

## 引入依赖
```xml
<dependency>
    <groupId>fun.tusi</groupId>
    <artifactId>log-cat-spring-boot-starter</artifactId>
    <version>1.0.2-sb2</version>
</dependency>
```

## 在代码中使用
```java
@LogCat
@PostMapping("login")
public Result<AdminLoginPong> loginByAccount(@Valid AdminLoginPing adminLoginPing) {

        return Result.ok().data(...);

        }
```