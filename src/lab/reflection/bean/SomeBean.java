package lab.reflection.bean;
import lab.reflection.annotation.AutoInjectable;
import lab.reflection.interfaces.SomeInterface;
import lab.reflection.interfaces.SomeOtherInterface;

public class SomeBean {
    @AutoInjectable
    private SomeInterface field1;
    @AutoInjectable
    private SomeOtherInterface field2;

    public void foo() 
    {
        field1.doSomething();
        field2.doSomeOther();
    }

    public SomeInterface getField1() 
    { 
        return field1; 
    }
    public SomeOtherInterface getField2() 
    { 
        return field2; 
    }
}