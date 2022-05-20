# spring-cloud-gateway with Eureka

## [Filters](https://medium.com/@niral22/spring-cloud-gateway-tutorial-5311ddd59816)

### PreFilter -> Filter_1
If we return request -> Then PreFilter
```
return chain.filter(exchange.mutate().request(mutatedHttpRequest).build());
```

### PostFilter -> Filter_2
If we return response -> Then PostFilter
```
//Skip Controller and directly return response
return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(response.getBytes())));
```
```chain.filter``` will pass the request to the Controller

### _TODO_ Create global filter for exception handling

## Creating feign client
GatewayFeign in service-one calls service-two via **api gateway** ```@FeignClient("gateway-service")```. service-two does not have an implementation
for /get2 url, but it gets intercepted by filter and filter returns response.

For testing on Postman call ```localhost:9090/ms2/get2```
