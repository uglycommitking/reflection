package lab.reflection.impl;

import lab.reflection.interfaces.SomeInterface;

public class OtherImpl implements SomeInterface {
    @Override
    public void doSomething() {
        System.out.print("B");
    }
}