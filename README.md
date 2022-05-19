# spring-cloud-gateway

### [Ref 1](https://medium.com/@niral22/spring-cloud-gateway-tutorial-5311ddd59816)

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