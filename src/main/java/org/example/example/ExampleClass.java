package org.example.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.annotations.MyAutoWire;
import org.example.beans.MamboNumberFive;

public class ExampleClass implements FrameworkMainClass {

    @MyAutoWire
    MamboNumberFive mamboNumberFive;

    private static final Logger log = LogManager.getLogger(ExampleClass.class.getName());

    public void main() {
        mamboNumberFive.setName(". We are setting this from ExampleClass");
        log.info(mamboNumberFive.getName() + ". We are getting this from ExampleClass");
    }

}
