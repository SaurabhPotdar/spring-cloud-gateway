# spring-cloud-gateway with Eureka

## [Filters](https://medium.com/@niral22/spring-cloud-gateway-tutorial-5311ddd59816)

### PreFilter -> Filter_1
If we return filter chain -> Then PreFilter\
```chain.filter``` will pass the request to the Controller
```
return chain.filter(exchange.mutate().request(mutatedHttpRequest).build());  //Passes request to controller
```

### PostFilter -> Filter_2
If we return response -> Then PostFilter
```
//Skip Controller and directly return response
return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(response.getBytes())));
```
```
chain.filter(exchange).**then**(Mono.fromRunnable(() -> exchange.getResponse().getHeaders().remove(config.getName())));
```

### Filter 3 -> uses RequestDecorator class
Read request body and return response

### Global Filter -> used for exception handling
```@Order()``` can be used for ordering filters\
...,-2,-1,0,1,2,...\
In our example Filter_3 has ```@Order(0)``` and GlobalFilter has ```@Order(1)```

## Creating feign client
GatewayFeign in service-one calls service-two via **api gateway** ```@FeignClient("gateway-service")```. service-two does not have an implementation
for /get2 url, but it gets intercepted by filter and filter returns response.

For testing on Postman call ```localhost:9090/ms2/get2```
