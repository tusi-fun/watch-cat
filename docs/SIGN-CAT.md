# @SignCat 注解
> api 请求和响应日志打印、持久化。「 _参数验证、类型转换等异常无法获取日志_ 」

## 注解参数
| 参数          | 类型      | 默认值    | 说明            |
|-------------|---------|--------|---------------|
| checkSign    | Boolean | true   | 是否启用签名验证      |
| jsonTarget | Class   | Object | json 提交时的接收对象 |

## 配置参数
```properties
watchcat.sign.enabled = true

# 非对称签名算法
watchcat.sign.symmetric.algorithm =
watchcat.sign.symmetric.tolerant =
watchcat.sign.symmetric.publicKey =
watchcat.sign.symmetric.privateKey =

# sha签名算法
watchcat.sign.sha.enabled =
watchcat.sign.sha.algorithm =
watchcat.sign.sha.tolerant =
```

## 使用示例
### 1. 引入依赖
```xml
<dependency>
    <groupId>fun.tusi</groupId>
    <artifactId>signature-cat-spring-boot-starter</artifactId>
    <version>1.0.2-sb2</version>
</dependency>
```

### 2. 添加配置
```properties
watchcat.sign.enabled = true

# 非对称签名算法
watchcat.sign.symmetric.algorithm =
watchcat.sign.symmetric.tolerant =
watchcat.sign.symmetric.publicKey =
watchcat.sign.symmetric.privateKey =
```

### 3. 使用注解
```java
@SignCat
@PostMapping("login")
public Result<AdminLoginPong> loginByAccount(@Valid AdminLoginPing adminLoginPing) {

        return Result.ok().data(...);

}
```