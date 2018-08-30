package com.whirly.guice.example;

import com.google.inject.Injector;

/**
 * @program: guice
 * @description:
 * @author: 赖键锋
 * @create: 2018-08-29 21:10
 **/
public class Main {
    public static void main(String[] args) {
        CustomModuleBuilder moduleBuilder = new CustomModuleBuilder();
        moduleBuilder.add(new ToolModule());
        moduleBuilder.add(new HumanModule());
        Injector injector = moduleBuilder.createInjector();
        Person person = injector.getInstance(Person.class);
        person.startwork();
    }
}
