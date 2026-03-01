package com.ppp;

import java.lang.ScopedValue;
import java.util.concurrent.StructuredTaskScope;

public class RequestContextDemo {

    static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    public static void main(String[] args) throws Exception {

        ScopedValue.where(REQUEST_ID, "REQ-" + System.currentTimeMillis()).run(() -> {

            try (var scope = StructuredTaskScope.open()) {

                scope.fork(() -> {
                    System.out.println("Logger VT → " + REQUEST_ID.get());
                    return null;
                });

                scope.fork(() -> {
                    System.out.println("Processor VT → " + REQUEST_ID.get());
                    return null;
                });

                scope.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
