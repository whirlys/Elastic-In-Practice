package com.whirly.guice.example;

import com.google.inject.Inject;

import java.util.Map;

/**
 * @program: guice
 * @description:
 * @author: 赖键锋
 * @create: 2018-08-29 20:58
 **/
public class Person {

    private IAnimal iAnimal;
    private ITool iTool;
    private Map<String, String> map;

    @Inject
    public Person(IAnimal iAnimal, ITool iTool, Map<String, String> map) {
        this.iAnimal = iAnimal;
        this.iTool = iTool;
        this.map = map;
    }

    public void startwork() {
        iTool.doWork();
        iAnimal.work();
        for (Map.Entry entry : map.entrySet()) {
            System.out.println("注入的map 是 " + entry.getKey() + " value " + entry.getValue());
        }
    }
}
