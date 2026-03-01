package com.ppp;

import java.util.concurrent.StructuredTaskScope;

public class ScopedValueBasic {
    static final ScopedValue<String> USER = ScopedValue.newInstance();

    public static void main(String[] args) {
        ScopedValue.where(USER, "Hello World")
                .run(() -> {
                    System.out.println("Main sees USER: " + USER.get());
                    try (var scope = StructuredTaskScope.open()) {
                        scope.fork(() -> {
                            System.out.println("Child sees USER: " + USER.get());
                            return null;
                        });
                        scope.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        try {
            System.out.println("Outside scope: " + USER.get());
        } catch (Exception e) {
            System.out.println("Outside scope: " + e);
        }
    }


}
