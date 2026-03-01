package com.ppp;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

public class ScopedValueHttpLoad {
    public static void main(String[] args) throws Exception {
        final int REQUESTS = 3000;        // tweak as needed
        final int TIMEOUT_S = 5;

        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Instant start = Instant.now();

        try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
            CountDownLatch latch = new CountDownLatch(REQUESTS);

            for (int i = 0; i < REQUESTS; i++) {
                exec.submit(() -> {
                    try {
                        var req = HttpRequest.newBuilder(URI.create("http://localhost:8081/api"))
                                .timeout(Duration.ofSeconds(TIMEOUT_S))
                                .GET()
                                .build();
                        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
                        if (res.statusCode() != 200) {
                            System.err.println("Non-200: " + res.statusCode());
                        }
                        if (ThreadLocalRandom.current().nextInt(500) == 0)
                            System.out.println(res.body());//not guaranteed, as you know
                    } catch (Exception e) {
                        System.err.println("Err: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                    return null;
                });
            }

            latch.await();
        }

        long tookMs = Duration.between(start, Instant.now()).toMillis();
        System.out.println("Sent " + REQUESTS + " requests in ~" + tookMs + " ms");
    }
}