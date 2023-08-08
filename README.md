# watch-cat
  > 封装api开发过程中的常用功能（日志打印、加解密、验签加签、接口频率限制、数据脱敏等。持续更新中）

| 功能            | 简介                   | 使用说明                        |
|-|----------------------|-----------------------------|
| @LogCat       | api 请求和响应日志打印、持久化    | -                           |
| @LimitCat     | api 频率限制（成功调用、失败尝试等） | [@LimitCat](./LIMIT-CAT.md) |
| @SecretCat    | api 请求解密 和 响应加密      | -                           |
| @SensitiveCat | api 响应中的敏感参数脱敏       | -                           |
| @SignCat      | api 验签加签             | -                           |


# 使用说明

## @LogCat 注解
> api 请求和响应日志打印、持久化。「 _参数验证、类型转换等异常无法获取日志_ 」

### 参数清单

| 参数          | 默认值     | 说明                 |
|-------------|--|--------------------|
| bid         | -       | 自定义参数（支持 SpEL）     |
| actionGroup | default | 操作分组               |
| action      | default | 操作                 |
| callback    | false   | 是否启用日志通知（用于持久化日志等） |
| print       | true    | 是否打印日志             |
| printOrig   | true    | 是否在日志中打印原始请求       |

### 引入依赖
```xml
<dependency>
    <groupId>fun.tusi</groupId>
    <artifactId>log-cat-spring-boot-starter</artifactId>
    <version>1.0.2-sb2</version>
</dependency>
```

### 在代码中使用
```java
@LogCat
@PostMapping("login")
public Result<AdminLoginPong> loginByAccount(@Valid AdminLoginPing adminLoginPing) {
    
    return Result.ok().data(...);
    
}
```


