package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class locationservice {

    @Value("${google.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create();

    public Mono<double[]> getLatLongFromZip(String zipcode) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + zipcode + "&key=" + apiKey;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        // Simple parsing (in a real app, use a proper JSON parser)
                        int latIndex = response.indexOf("\"lat\"") + 7;
                        int lngIndex = response.indexOf("\"lng\"") + 7;
                        double lat = Double.parseDouble(response.substring(latIndex, response.indexOf(",", latIndex)));
                        double lng = Double.parseDouble(response.substring(lngIndex, response.indexOf("\n", lngIndex)).replaceAll("[^\\d.-]", ""));
                        return new double[]{lat, lng};
                    } catch (Exception e) {
                        return new double[]{0.0, 0.0};
                    }
                });
    }
}

