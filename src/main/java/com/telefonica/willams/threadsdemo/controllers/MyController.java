package com.telefonica.willams.threadsdemo.controllers;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MyController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping("/test-with-reactor")
    public String testWithReactor() {
        Mono<String> api1Response = webClientBuilder.build().get().uri("http://localhost:1080/api1")
                .retrieve().bodyToMono(String.class)
                .timeout(Duration.ofMillis(200), Mono.just("API1 Timeout"))
                .doOnError(error -> log.error("API1 Error: ", error));

        Mono<String> api2Response = webClientBuilder.build().get().uri("http://localhost:1080/api2")
                .retrieve().bodyToMono(String.class)
                .timeout(Duration.ofSeconds(2), Mono.just("API2 Timeout"))
                .doOnError(error -> log.error("API2 Error: ", error));

        String combinedResponse;
        try {
            combinedResponse = Mono.zip(api1Response, api2Response)
                    .map(tuple -> "Combined Response: " + tuple.getT1() + " + " + tuple.getT2())
                    .block();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }

        return combinedResponse;
    }

    @GetMapping("/test-with-threads")
    public String testWithThreads() {
    CompletableFuture<String> api1Response = CompletableFuture.supplyAsync(() -> {
        try {
            return webClientBuilder.build().get().uri("http://localhost:1080/api1").retrieve().bodyToMono(String.class).block();
        } catch (Exception e) {
            log.error("Error calling API1", e);
            throw new RuntimeException("API1 Error: " + e.getMessage());
        }
    });

    CompletableFuture<String> api2Response = CompletableFuture.supplyAsync(() -> {
        try {
            return webClientBuilder.build().get().uri("http://localhost:1080/api2").retrieve().bodyToMono(String.class).block();
        } catch (Exception e) {
            log.error("Error calling API2", e);
            throw new RuntimeException("API2 Error: " + e.getMessage());
        }
    });

    String combinedResponse;
    try {
        combinedResponse = api1Response.thenCombine(api2Response, (response1, response2) -> 
            "Combined Response: " + response1 + " + " + response2
        ).get(5, TimeUnit.SECONDS); //seconds timeout for the combined response
    } catch (TimeoutException e) {
        return "Error: Combined response took too long!";
    } catch (Exception e) {
        return "Error: " + e.getMessage();
    }

    return combinedResponse;
}

}
