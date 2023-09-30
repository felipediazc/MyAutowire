package org.example.beans;

public class MamboNumberFiveImpl implements MamboNumberFive {

    private String name;

    @Override
    public String getName() {
        return "MamboNumberFiveImpl: " + name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
