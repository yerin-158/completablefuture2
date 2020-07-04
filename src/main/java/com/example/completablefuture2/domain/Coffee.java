package com.example.completablefuture2.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Coffee {

    private String name;
    private int price;

    @Builder
    public Coffee(String name, int price){
        this.name = name;
        this.price = price;
    }


}
