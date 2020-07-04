package com.example.completablefuture2.repository;

import com.example.completablefuture2.domain.Coffee;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CoffeeRepository {

    private List<Coffee> coffees = new ArrayList<>();

    @PostConstruct
    public void init(){
        coffees.add(Coffee.builder().name("latte").price(1100).build());
        coffees.add(Coffee.builder().name("mocha").price(1300).build());
        coffees.add(Coffee.builder().name("americano").price(900).build());
    }

    public int getPriceByName(String name){

        try{
            Thread.sleep(1000); // 커피 가격을 조회하기 위해선 최소 1초가 걸린다.
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        for (Coffee coffee : coffees){
            if (coffee.getName().equals(name)){
                return coffee.getPrice();
            }
        }

        return -1;
    }

}
