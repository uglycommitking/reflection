package lab.reflection.impl;

import lab.reflection.interfaces.SomeInterface;

public class SomeImpl implements SomeInterface {
    @Override
    public void doSomething() {
        System.out.print("A");
    }
}