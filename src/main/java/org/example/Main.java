package org.example;

import org.example.context.AppContext;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws IllegalAccessException, IOException, InstantiationException, InvocationTargetException, NoSuchMethodException {

        AppContext.run(org.example.example.ExampleClass.class);
    }
}