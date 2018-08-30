package com.whirly.guice.example.impl;

import com.whirly.guice.example.IAnimal;

/**
 * @program: guice
 * @description:
 * @author: 赖键锋
 * @create: 2018-08-29 20:53
 **/
public class IAnimalImpl implements IAnimal {
    @Override
    public void work() {
        System.out.println("animals can also do work");
    }
}
