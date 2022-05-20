# spring-cloud-gateway with Eureka

### [Filter](https://medium.com/@niral22/spring-cloud-gateway-tutorial-5311ddd59816)

### PreFilter -> Filter 1
If we return request -> Then PreFilter
```
return chain.filter(exchange.mutate().request(mutatedHttpRequest).build());
```

### PostFilter -> Filter 2
If we return response -> Then PostFilter
```
//Skip Controller and directly return response
return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(response.getBytes())));
```
```chain.filter``` will pass the request to the Controller

### Creating feign client
GatewayFeign in service-one calls service-two via **api gateway**. service-two does not have an implementation
for /get2 url, but it gets intercepted by filter and filter returns response
