package lab.reflection;

import lab.reflection.bean.SomeBean;
public class Main 
{
    public static void main(String[] args) 
    {
        System.out.println("Test 1: should print AC");
        SomeBean sb1 = new Injector().inject(new SomeBean());
        sb1.foo();
        System.out.println();

        System.out.println("Test 2: should print BC");
        SomeBean sb2 = new Injector("injection_other.properties").inject(new SomeBean());
        sb2.foo();
        System.out.println();
    }
}