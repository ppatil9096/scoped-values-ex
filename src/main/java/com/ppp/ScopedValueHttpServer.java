package com.ppp;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.StructuredTaskScope;

public class ScopedValueHttpServer {

    static final ScopedValue<String> REQ_ID = ScopedValue.newInstance();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        server.createContext("/api", exchange -> {
            String id = UUID.randomUUID().toString();

            ScopedValue.where(REQ_ID, id).run(() -> {
                try (var scope = StructuredTaskScope.open()) {
                    var userF = scope.fork(() -> simulate("fetchUser"));
                    var itemsF = scope.fork(() -> simulate("fetchItems"));

                    scope.join();

                    String body = """
                            {
                              "requestId": "%s",
                              "user": "%s",
                              "items": "%s"
                            }
                            """.formatted(REQ_ID.get(), userF.get(), itemsF.get());

                    byte[] b = body.getBytes();
                    exchange.sendResponseHeaders(200, b.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(b);
                    }
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        server.start();
        System.out.println("ScopedValue HTTP server running at http://localhost:8081/api");
    }

    static String simulate(String name) {
        try {
            Thread.sleep(150);
        } catch (InterruptedException ignored) {
        }
        return name + "@" + REQ_ID.get();
    }
}