# @SignatureCat 注解
> api 请求和响应日志打印、持久化。「 _参数验证、类型转换等异常无法获取日志_ 」

- 支持Hash 和 非对称算法验证签名
- 支持对响应数据做签名，并使用配置文件做成动态开关
- 支持使用外部接口方式获取 签名和验签密钥
- 使用配置文件方式，支持

## 注解参数
| 参数          | 类型      | 默认值    | 说明            |
|-------------|---------|--------|---------------|
| checkSign    | Boolean | true   | 是否启用签名验证      |
| jsonTarget | Class   | Object | json 提交时的接收对象 |

## 配置参数
```properties
watchcat.signature.enabled = true
watchcat.signature.scenes.AAA.algorithm =
watchcat.signature.scenes.AAA.tolerant =
watchcat.signature.scenes.AAA.publicKey =
watchcat.signature.scenes.AAA.privateKey =

watchcat.signature.scenes.BBB.algorithm =
watchcat.signature.scenes.BBB.tolerant =
watchcat.signature.scenes.BBB.appid =
watchcat.signature.scenes.BBB.secret =

# 非对称签名算法
watchcat.signature.symmetric.algorithm =
watchcat.signature.symmetric.tolerant =
watchcat.signature.symmetric.publicKey =
watchcat.signature.symmetric.privateKey =

# sha签名算法
watchcat.signature.sha.enabled =
watchcat.signature.sha.algorithm =
watchcat.signature.sha.tolerant =
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
watchcat.signature.enabled = true

# 非对称签名算法
watchcat.signature.symmetric.algorithm =
watchcat.signature.symmetric.tolerant =
watchcat.signature.symmetric.publicKey =
watchcat.signature.symmetric.privateKey =
```

### 3. 使用注解
```java
@SignatureCat
@PostMapping("login")
public Result<AdminLoginPong> loginByAccount(@Valid AdminLoginPing adminLoginPing) {

        return Result.ok().data(...);

}
```