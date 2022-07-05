# spring-cloud-gateway with Eureka

## [Filters](https://medium.com/@niral22/spring-cloud-gateway-tutorial-5311ddd59816)

### PreFilter -> Filter_1

If we return request -> Then it will go to next PreFilter or Controller

```
return chain.filter(exchange);
return chain.filter(exchange.mutate().request(mutatedHttpRequest).build());
```
**_We can only use this ```DataBufferUtils.join(exchange.getRequest().getBody())``` in PreFilter1. If we use in both PreFilter1,2 then only PreFilter1 will run.
So read request body in the first filter and add it to ```exchange.getAttributes()``` to read in the next filter._**\
Basically we can only use ```DataBufferUtils.join(exchange.getRequest().getBody())``` once in the chain.

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

GatewayFeign in service-one calls service-two via **api gateway** ```@FeignClient("gateway-service")```. service-two
does not have an implementation
for /get2 url, but it gets intercepted by filter and filter returns response.

For testing on Postman call ```localhost:9090/ms2/get2```

## Void return type
```
return jobService.markJobAsComplete(exchange)  //Webclient call
                        .map(jobId -> exchange)
                        .flatMap(chain::filter);
```
If markJobAsComplete returns void then, the chain terminates and does not go to next filter. So return String or something to keep chain going.
