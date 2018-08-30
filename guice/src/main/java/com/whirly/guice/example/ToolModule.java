package com.whirly.guice.example;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.whirly.guice.example.impl.IAnimalImpl;
import com.whirly.guice.example.impl.IToolImpl;

/**
 * @program: guice
 * @description:
 * @author: 赖键锋
 * @create: 2018-08-29 20:49
 **/
public class ToolModule extends AbstractModule {

    @Override
    protected void configure() {
        //此处注入的实例可以注入到其他类的构造函数中, 只要那个类使用@Inject进行注入即可
        bind(IAnimal.class).to(IAnimalImpl.class);
        bind(ITool.class).to(IToolImpl.class);

        // 注入Map实例
        MapBinder<String, String> mapBinder = MapBinder.newMapBinder(binder(), String.class, String.class);
        mapBinder.addBinding("test1").toInstance("test1");
        mapBinder.addBinding("test2").toInstance("test2");
    }
}
