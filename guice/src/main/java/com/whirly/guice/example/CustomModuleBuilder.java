package com.whirly.guice.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomModuleBuilder implements Iterable<Module> {

    private final List<Module> modules = new ArrayList<>();

    public CustomModuleBuilder add(Module... newModules) {
        for (Module module : newModules) {
            modules.add(module);
        }
        return this;
    }

    @Override
    public Iterator<Module> iterator() {
        return modules.iterator();
    }

    public Injector createInjector() {
        Injector injector = Guice.createInjector(modules);
        //Injectors.cleanCaches(injector);
        // in ES, we always create all instances as if they are eager singletons
        // this allows for considerable memory savings (no need to store construction info) as well as cycles
        //((InjectorImpl) injector).readOnlyAllSingletons();
        return injector;
    }


}

