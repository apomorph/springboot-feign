# springboot-feign
feign进一步封装

添加maven依赖

```
  <dependency>
      <groupId>io.github.openfeign</groupId>
      <artifactId>feign-okhttp</artifactId>
      <version>10.1.0</version>
  </dependency>
  <dependency>
      <groupId>io.github.openfeign</groupId>
      <artifactId>feign-jackson</artifactId>
      <version>10.1.0</version>
  </dependency>
```
<<<<<<< HEAD

```
Tips:
  @RequestLine注解不能指定host为ip:port的方式 :会被编码
  @Body注解的value值不能为null或空串
  如果使用okhttp作为client实现 则GET方法不能使用@Body注解
  @Param注解在bean上 无法通过模板获取到对应的bean属性值
```
=======
Problems:   
&emsp; feign集成okhttp后不支持GET请求
>>>>>>> 71652ea4f638c2b2e2351c06a1d61d1e1490236c
