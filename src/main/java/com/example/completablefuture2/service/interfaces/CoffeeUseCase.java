package com.example.completablefuture2.service.interfaces;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface CoffeeUseCase {
    int getPriceSync(String name);
    CompletableFuture<Integer> getPriceAsync(String name);
    CompletableFuture<Integer> getDiscountPriceAsync(Integer price);
}
