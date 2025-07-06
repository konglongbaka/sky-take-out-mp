package com.sky.utils;

import java.util.Random;

public class RandomTimeUtil {
    static Random random =new Random();

    public static Integer getRandom(Integer min, Integer max){
       return random.nextInt(max-min)+min;
    }

    public static int getRandom(){
        return random.nextInt(101)+50;
    }
}
