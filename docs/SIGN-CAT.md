# @SignatureCat 注解
> api 请求和响应日志打印、持久化。「 _参数验证、类型转换等异常无法获取日志_ 」

- 支持Hash 和 非对称算法验证签名
- 支持对响应数据做签名，并使用配置文件做成动态开关
- 支持使用外部接口方式获取 签名和验签密钥
- 使用配置文件方式，支持

## 注解参数
| 参数          | 类型      | 默认值    | 说明            |
|-------------|---------|--------|---------------|
| jsonTarget | Class   | Object | json 提交时的接收对象 |

## 配置参数
```properties
# 是否启用签名验证，默认 true
watchcat.signature.enabled = true

# 签名算法，默认 HmacSHA256
watchcat.signature.digest.algorithm = HmacSHA256

# 时间戳前后宽容时间，默认 300s
watchcat.signature.digest.tolerant = 300s

# 应用列表
watchcat.signature.digest.apps.appidA = secretA
watchcat.signature.digest.apps.appidB = secretB
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

watchcat.signature.digest.apps.appidA = secretA
```

### 3. 使用注解
```java

/**
 * 表单方式提交
 */
@SignatureCat
@PostMapping("form")
public Result<Object> formSubmit(@Valid AdminLoginPing adminLoginPing) {

        return Result.ok().data(...);

}

/**
 * json方式提交（注意：不能使用 @RequestBody 和 @Valid 注解）
 */
@SignatureCat(jsonTarget = SignCatObjPing.class)
@PostMapping("json")
public Result<Object> jsonSubmit(SignCatObjPing signCatObjPing) {

        return Result.ok().data(...);

}
```