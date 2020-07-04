package com.example.completablefuture2.service;

import com.example.completablefuture2.repository.CoffeeRepository;
import com.example.completablefuture2.service.interfaces.CoffeeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoffeeService implements CoffeeUseCase {

    private final CoffeeRepository coffeeRepository;
    Executor executor = Executors.newFixedThreadPool(10);
    //private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public int getPriceSync(String name) {
        log.info("동기 호출 방식으로 가격 조회 시작");
        return coffeeRepository.getPriceByName(name);
    }

    @Override
    public CompletableFuture<Integer> getPriceAsync(String name) {
        log.info("비동기 호출 방식으로 가격 조회 시작");

        CompletableFuture<Integer> future = new CompletableFuture<>();

        new Thread(() -> {
            log.info("새로운 쓰레드로 작업 시작");
            Integer price = coffeeRepository.getPriceByName(name);
            future.complete(price);
        }).start();

        return future;
    }

    // getPriceAsync와 동일한 작업을 수행하지만
    // 파라미터는 없지만 반환값이 있는 함수형 인터페이스인 supplyAsync를 사용하여
    // 깔끔하게 코드를 작성한다.
    @Override
    public CompletableFuture<Integer> getPriceAsyncWithSupplyAsync(String name) {
        log.info("비동기 호출 방식으로 가격 조회 시작");
        return CompletableFuture.supplyAsync(() -> {
            log.info("새로운 쓰레드로 작업 시작");
            return coffeeRepository.getPriceByName(name);
        }, executor);
        // info.
        // supplyAsync로 수행하는 로직은 Common Pool을 사용한다.
        // executor를 파라미터로 추가하면 Common Pool 대신 별도의 쓰레드 풀에서 동작한다.
    }

    @Override
    public CompletableFuture<Integer> getDiscountPriceAsync(Integer price) {
        log.info("비동기 호출 방식으로 가격 변경 시작");
        return CompletableFuture.supplyAsync(() -> {
            log.info("가격 변경 메소드 쓰레드 작업 시작 ");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return (int) (price * 0.9);
        }, executor);
    }
}
