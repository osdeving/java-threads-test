package com.telefonica.willams.threadsdemo.controllers;

@Slf4j
@RestController
public class TestCallsForApisController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping("/test-combined")
    public Mono<String> testCombined() {
        Mono<String> userProfileResponse = getUserProfile();
        Mono<String> subscribedResponse = getSubscribed();
        Mono<String> hbaseResponse = getHbase();
        Mono<String> elasticResponse = getElastic();
        Mono<String> operation1Response = getOperation1(); // Simulando operação SOAP com uma chamada REST para testar latência em vez de tipo de chamada
        Mono<String> operation2Response = getOperation2(); // Simulando operação SOAP com uma chamada REST para testar latência em vez de tipo de chamada

        return userProfileResponse
            .zipWith(subscribedResponse)
            .flatMap(tuple -> {
                if ("FAIL".equals(tuple.getT1()) || "FAIL".equals(tuple.getT2())) {
                    return Mono.just("Error: userProfile or subscribed failed!");
                }
                return Mono.zip(hbaseResponse, elasticResponse, operation1Response, operation2Response)
                    .map(tuple2 -> "Combined Response: " + tuple.getT1() + " + " + tuple.getT2() + " + "
                                   + tuple2.getT1() + " + " + tuple2.getT2() + " + " + tuple2.getT3() + " + " + tuple2.getT4());
            });
    }

    private Mono<String> getUserProfile() {
        return webClientBuilder.build().get().uri("http://localhost:1080/userProfile")
            .retrieve().bodyToMono(String.class);
    }

    private Mono<String> getSubscribed() {
        return webClientBuilder.build().get().uri("http://localhost:1080/subscribed")
            .retrieve().bodyToMono(String.class);
    }

    private Mono<String> getHbase() {
        return webClientBuilder.build().get().uri("http://localhost:1080/hbase")
            .retrieve().bodyToMono(String.class);
    }

    private Mono<String> getElastic() {
        return webClientBuilder.build().get().uri("http://localhost:1080/elastic")
            .retrieve().bodyToMono(String.class);
    }

    private Mono<String> getOperation1() {
        // Simulando operação SOAP com uma chamada REST para testar latência em vez de tipo de chamada
        return webClientBuilder.build().get().uri("http://localhost:1080/operation1")
            .retrieve().bodyToMono(String.class);
    }

    private Mono<String> getOperation2() {
        // Simulando operação SOAP com uma chamada REST para testar latência em vez de tipo de chamada
        return webClientBuilder.build().get().uri("http://localhost:1080/operation2")
            .retrieve().bodyToMono(String.class);
    }
}

