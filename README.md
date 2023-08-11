# watch-cat (SpringBoot 2.x)
  > 封装基于 Spring Boot 开发 api 过程中的常用功能，日志打印、加解密、验签加签、接口频率限制、数据脱敏等。
  > 持续更新中。

| 功能            | 简介                   | 使用说明                        |
|-|----------------------|-----------------------------|
| @LogCat       | api 请求和响应日志打印、持久化    | [@LogCat](docs/LOG-CAT.md)     |
| @LimitCat     | api 频率限制（成功调用、失败尝试等） | [@LimitCat](docs/LIMIT-CAT.md) |
| @SecretCat    | api 请求解密 和 响应加密      | 文档完善中                       |
| @SensitiveCat | api 响应中的敏感参数脱敏       | 文档完善中                           |
| @SignatureCat      | api 验签加签             | [@SignatureCat](docs/SIGN-CAT.md)   |