package com.example.completablefuture2.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CompletableFuture;

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

}
