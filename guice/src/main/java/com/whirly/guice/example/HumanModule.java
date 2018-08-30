package com.whirly.guice.example;

import com.google.inject.AbstractModule;

/**
 * @program: guice
 * @description:
 * @author: 赖键锋
 * @create: 2018-08-29 21:01
 **/
public class HumanModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Person.class).asEagerSingleton();
    }
}
