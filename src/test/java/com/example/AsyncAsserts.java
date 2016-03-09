package com.example;

import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;

public class AsyncAsserts {

    private static final Logger LOGGER = getLogger(AsyncAsserts.class);

    public static <T> T asyncAssert(CompletableFuture<T> completableFuture, Consumer<T> asserter) throws InterruptedException, ExecutionException {
        final CompletableFuture<Void> future = completableFuture.thenAccept(asserter);

        while (!future.isDone()) {
            LOGGER.info("Waiting for completable future to complete");
            MILLISECONDS.sleep(100);
        }

        return completableFuture.get();
    }
}
