package org.example.context;

import com.google.common.reflect.ClassPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.annotations.MyAutoWire;
import org.example.example.FrameworkMainClass;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class AppContext {

    private static AppContext appContextInstance;
    private static Map<Class, List<Class>> implementations = new HashMap<>();
    private static Map<Class, Object> objects = new HashMap<>();

    private static final Logger log = LogManager.getLogger(AppContext.class.getName());

    private AppContext() throws SecurityException, IllegalArgumentException, IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        List<Class> classList = getListClasses();
        setImplementations(classList);
        setObjects(classList);
        for (Object myObject : objects.values()) {
            Field[] fields = myObject.getClass().getDeclaredFields();
            for (Field field : fields) {
                MyAutoWire annotation = field.getAnnotation(MyAutoWire.class);
                if (annotation != null) {
                    log.info("Annotation is : {}", annotation);
                    List<Class> classList2 = implementations.get(field.getType());
                    if (!classList2.isEmpty()) {
                        Class implementationClass = classList2.get(0);
                        log.info("Implementation class is : {}", implementationClass);
                        field.setAccessible(true);
                        field.set(myObject, objects.get(implementationClass));
                        log.info("Successfully injection of {} into {}", implementationClass, myObject.getClass());
                    }
                }

            }
        }
    }

    private List<Class> getListClasses() throws IOException {
        List<Class> classList = new ArrayList<>();
        Set<String> classes = findAllClassesUsingGoogleGuice("");
        for (String className : classes) {
            if (className.contains("org.example")) {
                Class myClass = getClass(className);
                if (myClass != null) {
                    classList.add(myClass);
                }
            }
        }
        log.info("Project classes are : {}", classList);
        return classList;
    }

    private void setInterfaces(List<Class> classList) throws IOException {
        for (Class myClass : classList) {
            if (myClass.isInterface()) {
                implementations.put(myClass, new ArrayList<>());
            }
        }
        log.info("Project interfaces are : {}", implementations);
    }

    private void setImplementations(List<Class> classList) throws IOException {
        setInterfaces(classList);
        for (Class myClass : classList) {
            Class<Object>[] classInterfaces = myClass.getInterfaces();
            for (int i = 0; i < classInterfaces.length; i++) {
                List<Class> classList2 = implementations.get(classInterfaces[i]);
                if (classList2 != null) {
                    classList2.add(myClass);
                    implementations.put(classInterfaces[i], classList2);
                }
            }
        }
        log.info("Project implementations are : {}", implementations);
    }

    private Boolean isClass(Class myClass) {
        return !myClass.isInterface() && !myClass.isEnum() && !myClass.isAnnotation() && !myClass.isAnonymousClass() && !this.getClass().equals(myClass);
    }

    private void setObjects(List<Class> classList) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for (Class myClass : classList) {
            if (isClass(myClass)) {
                objects.put(myClass, myClass.getDeclaredConstructor().newInstance());
            }
        }
        log.info("Project objects are : {}", objects);
    }

    private Set<String> findAllClassesUsingGoogleGuice(String packageName) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName().compareTo("") != 0)
                .map(clazz -> clazz.getName())
                .collect(Collectors.toSet());
    }

    private Class getClass(String className) {
        try {
            Class myClass = Class.forName(className);
            return myClass;
        } catch (ClassNotFoundException e) {
            log.error("Exception on getClass {}", e);
        }
        return null;
    }

    public static synchronized AppContext getInstance() throws SecurityException,
            IllegalArgumentException, IllegalAccessException, IOException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        if (appContextInstance == null) {
            appContextInstance = new AppContext();
        }
        return appContextInstance;
    }

    public Object getObjectInstance(Class myClass) {
        return objects.get(myClass);
    }

    public static void run(Class myClass) throws IOException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        AppContext appContext = AppContext.getInstance();
        FrameworkMainClass object = (FrameworkMainClass) appContext.getObjectInstance(myClass);
        object.main();
    }

}
