package com.example.completablefuture2.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class CoffeeServiceTest {
    @Autowired
    private CoffeeService sut;

    @Test
    @DisplayName("동기 블로킹을 통한 이름으로 가격을 조회한다.")
    void syncTest1(){
        int expectedPrice = 1100;

        int resultPrice = sut.getPriceSync("latte");
        log.info("최종 가격 전달 받음");

        assertThat(expectedPrice, equalTo(resultPrice));
    }

    @Test
    @DisplayName("CompletableFuture를 이용한 비동기 블로킹을 통한 이름으로 가격 조회하기")
    void asyncTest1() throws InterruptedException {
        int expectedPrice = 1100;

        CompletableFuture<Integer> future = sut.getPriceAsync("latte");

        log.info("최종 데이터(price)를 전달 받지는 않았지만, 다른 작업 수행 가능");
        Thread.sleep(500);
        log.info("다른 작업에서 0.5s 소모");

        int resultPrice = future.join(); // 블록킹
        // info.
        // Future는 get을, CompletableFuture는 join을 사용해 최종 데이터를 조회할 수 있다.
        // 이 때 예외처리하는 방식이 약간 다르다.

        log.info("최종 가격 전달 받음");

        assertThat(expectedPrice, equalTo(resultPrice));
    }

    @Test
    @DisplayName("asyncTest1과 동일한 효과를 내지만 getPriceAsyncWithSupplyAsync를 사용한다.")
    void asyncTest2() throws InterruptedException {
        int expectedPrice = 1100;

        CompletableFuture<Integer> future = sut.getPriceAsyncWithSupplyAsync("latte");

        log.info("최종 데이터(price)를 전달 받지는 않았지만, 다른 작업 수행 가능");
        Thread.sleep(500);
        log.info("다른 작업에서 0.5s 소모");

        int resultPrice = future.join(); // 블록킹

        log.info("최종 가격 전달 받음");

        assertThat(expectedPrice, equalTo(resultPrice));
    }

    @Test
    @DisplayName("thenAccept 메서드를 사용해 콜백 반환없이 비동기 호출로 가격을 가져온다.")
    void asyncTest3(){
        int expectedPrice = 1100;

        CompletableFuture<Void> future
                = sut.getPriceAsync("latte")
                    .thenAccept(response -> {
                        log.info("\n>>>>>> callback function..\n>>>>>> 가격 = "+response+"원\n>>>>>> 단, future를 반환하지 않는다.");
                        assertThat(expectedPrice, equalTo(response));
                    });

        log.info("아직 최종 데이터 전달받지 않음. 다른 작업 수행가능.. >>> 논블로킹");

        // info.
        // 아래 구문이 없으면 main thread가 종료되며 thenAccept를 확인하기 전에 끝나버린다.
        // 테스트를 위해 메인 쓰레드가 종료되지 않도록 블로킹하여 대기하게 만든다.
        // future가 complete 되면 위에 작성한 thenAccept 코드가 실행된다.
        assertNull(future.join());
        log.info(">>>>> 테스트 종료");
    }

    @Test
    @DisplayName("thenApply 메서드를 사용해 콜백 반환이 포함된 비동기 호출로 가격을 가져온다.")
    void asyncTest4() {
        Integer expectedPrice = 1100 + 100;

        CompletableFuture<Void> future
                = sut.getPriceAsyncWithSupplyAsync("latte")
                    .thenApply(response -> {
                        log.info("같은 쓰레드에서 동작");
                        // 다른 쓰레드에서 동작하게 만들고 싶으면 thenApplyAsync, thenAcceptAsync 메서드를 사용하면 된다.
                        return response + 100;
                    }).thenAccept(updatedPrice -> {
                    log.info("\n>>>>>> callback function..\n>>>>>> thenApply에서 조정된 가격 = "+updatedPrice+"원\n>>>>>> 단, future를 반환하지 않는다.");
                    assertThat(expectedPrice, equalTo(updatedPrice));
                });
        log.info("아직 최종 데이터 전달받지 않음. 다른 작업 수행가능.. >>> 논블로킹");

        assertNull(future.join());
        log.info(">>>>> 테스트 종료");
    }

    @Test
    @DisplayName("asyncTest4와 동일한 작업이지만 다른 스레드에서 작업하도록 한다.")
    void asyncTest5() {
        Integer expectedPrice = 1100 + 100;
        Executor executor = Executors.newFixedThreadPool(3);

        CompletableFuture<Void> future
                = sut.getPriceAsyncWithSupplyAsync("latte")
                .thenApplyAsync(response -> {
                    log.info("다른 쓰레드에서 동작");
                    return response + 100;
                }, executor)
                .thenAcceptAsync(updatedPrice -> {
                    log.info("다른 쓰레드에서 동작하나 common pool을 사용함");
                    // 위처럼 executor를 추가해주면 다른 pool에서 작동한다.
                    log.info("\n>>>>>> callback function..\n>>>>>> thenApply에서 조정된 가격 = "+updatedPrice+"원\n>>>>>> 단, future를 반환하지 않는다.");
                    assertThat(expectedPrice, equalTo(updatedPrice));
                });
        log.info("아직 최종 데이터 전달받지 않음. 다른 작업 수행가능.. >>> 논블로킹");

        assertNull(future.join());
        log.info(">>>>> 테스트 종료");
    }
}
