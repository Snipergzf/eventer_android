package com.eventer.app.util;

import java.util.Random;

/**
 * Created by LiuNana on 2016/2/26.
 */
public class RandomUtil {

    public static String getRandomNick()
    {
        StringBuilder val = new StringBuilder();
        Random random = new Random();
        //随机生成9位数字
        for(int i = 0; i < 8; i++) {
            val.append(String.valueOf(random.nextInt(10)));
        }
        //随机生成3个字母
        for(int i = 0; i < 3; i++) {
            val.append((char)(random.nextInt(26) + 97));
        }
        return val.toString();
    }
}
