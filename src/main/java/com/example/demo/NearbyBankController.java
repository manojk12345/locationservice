package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class NearbyBankController {

    @Autowired
    private locationservice locationService;

    private final WebClient webClient = WebClient.create();
    
    @GetMapping("/test")
    public String testEndpoint() {
        return "Hello, Spring Boot!";
    }
    @GetMapping("/banks")
    public Mono<List<String>> getNearbyBanks(@RequestParam String zipcode) {
        return locationService.getLatLongFromZip(zipcode)
                .flatMap(coords -> {
                    double userLat = coords[0];
                    double userLon = coords[1];

                    String uri = UriComponentsBuilder
                            .fromHttpUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                            .queryParam("location", userLat + "," + userLon)
                            .queryParam("radius", 16093) // 10 miles
                            .queryParam("type", "bank")
                            .queryParam("key", "AIzaSyAFcl4b1NXC1JTj92CQgjH0bdIBae5GtcA")
                            .build()
                            .toUriString();

                    return webClient.get()
                            .uri(uri)
                            .retrieve()
                            .bodyToMono(Bankresult.class) // Jackson will do the mapping here
                            .map(result -> result.getResults()
                                    .stream()
                                    .map(Place::getName)
                                    .collect(Collectors.toList()));
                });
    }
}

