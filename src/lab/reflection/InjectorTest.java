package lab.reflection;

import lab.reflection.bean.SomeBean;
import lab.reflection.impl.OtherImpl;
import lab.reflection.impl.SODoer;
import lab.reflection.impl.SomeImpl;
import lab.reflection.interfaces.SomeInterface;
import lab.reflection.interfaces.SomeOtherInterface;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class InjectorTest 
{

    private static int passed = 0;
    private static int failed = 0;
    private static final List<String> failures = new ArrayList<>();

    public static void main(String[] args) 
    {
        test_inject_returnsSameObject();
        test_field1_isSomeImpl_defaultProps();
        test_field2_isSODoer_defaultProps();
        test_field1_isOtherImpl_alternateProps();
        test_foo_outputsAC();
        test_foo_outputsBC();
        test_throws_whenPropertiesFileMissing();
        test_throws_whenMappingMissing();
        test_throws_whenClassNotFound();
        test_fieldsImplementCorrectInterfaces();
        System.out.println("\n Results: " + passed + " passed, " + failed + " failed ");
        if (!failures.isEmpty()) {
            System.out.println("FAILURES:");
            failures.forEach(f -> System.out.println("  FAIL: " + f));
            System.exit(1);
        } else {
            System.out.println("All tests passed!");
        }
    }

    static void test_inject_returnsSameObject() 
    {
        try 
        {
            SomeBean bean = new SomeBean();
            SomeBean result = new Injector().inject(bean);
            assertTrue("inject() must return same object", bean == result);
            pass("inject_returnsSameObject");
        } catch (Throwable e) { fail("inject_returnsSameObject", e); }
    }

    static void test_field1_isSomeImpl_defaultProps() 
    {
        try 
        {
            SomeBean bean = new Injector().inject(new SomeBean());
            assertNotNull("field1 must not be null", bean.getField1());
            assertInstanceOf("field1 should be SomeImpl", SomeImpl.class, bean.getField1());
            pass("field1_isSomeImpl_defaultProps");
        } catch (Throwable e) { fail("field1_isSomeImpl_defaultProps", e); }
    }
    static void test_field2_isSODoer_defaultProps() 
    {
        try 
        {
            SomeBean bean = new Injector().inject(new SomeBean());
            assertNotNull("field2 must not be null", bean.getField2());
            assertInstanceOf("field2 should be SODoer", SODoer.class, bean.getField2());
            pass("field2_isSODoer_defaultProps");
        } catch (Throwable e) { fail("field2_isSODoer_defaultProps", e); }
    }
    static void test_field1_isOtherImpl_alternateProps() 
    {
        try 
        {
            SomeBean bean = new Injector("injection_other.properties").inject(new SomeBean());
            assertNotNull("field1 must not be null", bean.getField1());
            assertInstanceOf("field1 should be OtherImpl", OtherImpl.class, bean.getField1());
            pass("field1_isOtherImpl_alternateProps");
        } catch (Throwable e) { fail("field1_isOtherImpl_alternateProps", e); }
    }



    static void test_foo_outputsAC() 
    {
        try 
        {
            String out = capture(() -> new Injector().inject(new SomeBean()).foo());
            assertEquals("Expected AC", "AC", out);
            pass("foo_outputsAC");
        } catch (Throwable e) { fail("foo_outputsAC", e); }
    }

    static void test_foo_outputsBC() 
    {
        try 
        {
            String out = capture(() ->
                new Injector("injection_other.properties").inject(new SomeBean()).foo());
            assertEquals("Expected BC", "BC", out);
            pass("foo_outputsBC");
        } catch (Throwable e) { fail("foo_outputsBC", e); }
    }

    static void test_throws_whenPropertiesFileMissing() 
    {
        try 
        {
            assertThrows(InjectionException.class,
                () -> new Injector("nonexistent.properties"));
            pass("throws_whenPropertiesFileMissing");
        } catch (Throwable e) { fail("throws_whenPropertiesFileMissing", e); }
    }
    static void test_throws_whenMappingMissing() 
    {
        try 
        {
            assertThrows(InjectionException.class,
                () -> new Injector("injection_incomplete.properties").inject(new SomeBean()));
            pass("throws_whenMappingMissing");
        } catch (Throwable e) { fail("throws_whenMappingMissing", e); }
    }

    static void test_throws_whenClassNotFound() 
    {
        try 
        {
            assertThrows(InjectionException.class,
                () -> new Injector("injection_bad.properties").inject(new SomeBean()));
            pass("throws_whenClassNotFound");
        } catch (Throwable e) { fail("throws_whenClassNotFound", e); }
    }

    static void test_fieldsImplementCorrectInterfaces() 
    {
        try 
        {
            SomeBean bean = new Injector().inject(new SomeBean());
            assertInstanceOf("field1 must implement SomeInterface",
                SomeInterface.class, bean.getField1());
            assertInstanceOf("field2 must implement SomeOtherInterface",
                SomeOtherInterface.class, bean.getField2());
            pass("fieldsImplementCorrectInterfaces");
        } catch (Throwable e) { fail("fieldsImplementCorrectInterfaces", e); }
    }


    static String capture(Runnable action) 
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream orig = System.out;
        System.setOut(new PrintStream(baos));
        try { action.run(); } finally { System.setOut(orig); }
        return baos.toString().trim();
    }



    static void assertTrue(String msg, boolean v) 
    {
        if (!v) throw new AssertionError(msg);
    }

    static void assertNotNull(String msg, Object o) 
    {
        if (o == null) throw new AssertionError(msg + " (was null)");
    }

    static void assertEquals(String msg, Object expected, Object actual) 
    {
        if (!expected.equals(actual))
            throw new AssertionError(msg + " — expected: " + expected + ", got: " + actual);
    }

    static void assertInstanceOf(String msg, Class<?> type, Object obj) 
    {
        if (!type.isInstance(obj))
            throw new AssertionError(msg + " — actual: " + obj.getClass().getName());
    }



    @FunctionalInterface
    interface ThrowingRunnable { void run() throws Exception; }

    static void assertThrows(Class<? extends Throwable> expected, ThrowingRunnable action) {
        try {
            action.run();
            throw new AssertionError("Expected " + expected.getSimpleName() + " but nothing thrown");
        } catch (Throwable t) {
            if (!expected.isInstance(t))
                throw new AssertionError("Expected " + expected.getSimpleName()
                    + " but got " + t.getClass().getSimpleName());
        }
    }

    static void pass(String name) 
    {
        passed++;
        System.out.println("  OK  " + name);
    }

    static void fail(String name, Throwable e) 
    {
        failed++;
        failures.add(name + " — " + e.getMessage());
        System.out.println("  FAIL " + name + " — " + e.getMessage());
    }
}