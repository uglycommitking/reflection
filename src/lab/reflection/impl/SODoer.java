package lab.reflection.impl;

import lab.reflection.interfaces.SomeOtherInterface;

public class SODoer implements SomeOtherInterface {
    @Override
    public void doSomeOther() {
        System.out.print("C");
    }
}