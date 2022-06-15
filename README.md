# spring-cloud-gateway with Eureka

## [Filters](https://medium.com/@niral22/spring-cloud-gateway-tutorial-5311ddd59816)

### PreFilter -> Filter_1
If we return request -> Then it will fo to next PreFilter or Controller
```
return chain.filter(exchange);
return chain.filter(exchange.mutate().request(mutatedHttpRequest).build());
```

### Filter_2
Skip Controller and directly return response -> exchange.getResponse().writeWith()
```
return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(response.getBytes())));
```
```chain.filter``` will pass the request to the Controller

### Filter 3 -> uses RequestDecorator class
Read request body and return response

### PostFilter -> Return response once chain.filter has completed using then()
```
return chain.filter(exchange).**then**(Mono.fromRunnable(() -> {
  logger.info("Global Post Filter executed");
}));
```

### Global Filter -> used for exception handling
```@Order()``` can be used for ordering filters\
...,-2,-1,0,1,2,...\
In our example Filter_3 has ```@Order(0)``` and GlobalFilter has ```@Order(1)```

## Creating feign client
GatewayFeign in service-one calls service-two via **api gateway** ```@FeignClient("gateway-service")```. service-two does not have an implementation
for /get2 url, but it gets intercepted by filter and filter returns response.

For testing on Postman call ```localhost:9090/ms2/get2```
