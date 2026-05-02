package lab.reflection;
import lab.reflection.annotation.AutoInjectable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class Injector {
    private static final String DEFAULT_PROPERTIES_FILE = "injection.properties";
    private final Properties properties;

    public Injector() 
    {
        this(DEFAULT_PROPERTIES_FILE);
    }

    public Injector(String propertiesFile) 
    {
        this.properties = loadProperties(propertiesFile);
    }

    public <T> T inject(T obj) 
    {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(AutoInjectable.class)) {
                continue;
            }
            String interfaceName = field.getType().getName();
            String implName = properties.getProperty(interfaceName);

            if (implName == null) {
                throw new InjectionException(
                    "No implementation found for: " + interfaceName
                );
            }
            try {
                Class<?> implClass = Class.forName(implName.trim());
                Object instance = implClass.getDeclaredConstructor().newInstance();
                field.setAccessible(true);
                field.set(obj, instance);

            } catch (ClassNotFoundException e) 
            {
                throw new InjectionException("Class not found: " + implName, e);
            } catch (NoSuchMethodException e) 
            {
                throw new InjectionException("No default constructor in: " + implName, e);
            } catch (Exception e) 
            {
                throw new InjectionException("Failed to inject '" + field.getName() + "': " + e.getMessage(), e);
            }
        }
        return obj;
    }

    private Properties loadProperties(String fileName) 
    {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) 
        {
            if (is == null) 
            {
                throw new InjectionException("Properties file not found: " + fileName);
            }
            props.load(is);
        } catch (IOException e) 
        {
            throw new InjectionException("Failed to load: " + fileName, e);
        }
        return props;
    }
}