package com.fizikovnet.hw1;

import com.fizikovnet.hw1.annotations.*;
import com.fizikovnet.hw1.exceptions.TestRunnerExceptions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class TestRunner {

    public static void runTests(Class c) {
        try {
            Class klass = Class.forName(c.getName());

            validateStructureTestClass(klass);
            Method[] methods = klass.getDeclaredMethods();

            invokeBeforeSuite(methods);

            Map<Integer, List<Method>> orderedMethodsToInvoke = makeTestMethodsMap(methods);
            Map<Integer, Method> forEachMethods = makeBeforeAfterTestMethodsMap(methods);
            Object testInstance = klass.getDeclaredConstructor().newInstance();
            for (List<Method> methodList : orderedMethodsToInvoke.values()) {
                for (Method method : methodList) {
                    if (forEachMethods.containsKey(0)) {
                        forEachMethods.get(0).invoke(null);
                    }
                    method.invoke(testInstance);
                    if (forEachMethods.containsKey(1)) {
                        forEachMethods.get(1).invoke(null);
                    }
                }
            }

            invokeAfterSuite(methods);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<Integer, Method> makeBeforeAfterTestMethodsMap(Method[] methods) {
        Map<Integer, Method> result = new HashMap<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeTest.class)) {
                result.put(0, method);
            }
            if (method.isAnnotationPresent(AfterTest.class)) {
                result.put(1, method);
            }
        }
        return result;
    }

    private static void invokeAfterSuite(Method[] methods) throws InvocationTargetException, IllegalAccessException {
        for (Method m : methods) {
            if (m.isAnnotationPresent(AfterSuite.class)) {
                m.invoke(null);
            }
        }
    }

    private static Map<Integer, List<Method>> makeTestMethodsMap(Method[] methods) {
        Map<Integer, List<Method>> map = new TreeMap<>(Collections.reverseOrder());
        for (Method m : methods) {
            if (m.isAnnotationPresent(Test.class)) {
                int priority = m.getAnnotation(Test.class).priority();
                if (map.containsKey(priority)) {
                    List<Method> l = new ArrayList<>(map.get(priority));
                    l.add(m);
                    map.put(priority, l);
                } else {
                    map.put(priority, List.of(m));
                }
            }
        }
        return map;
    }

    private static void invokeBeforeSuite(Method[] methods) throws IllegalAccessException, InvocationTargetException {
        for (Method m : methods) {
            if (m.isAnnotationPresent(BeforeSuite.class)) {
                m.invoke(null);
            }
        }
    }

    private static void validateStructureTestClass(Class klass) {
        Method[] methods = klass.getDeclaredMethods();
        int countBeforeSuiteMethods = 0;
        int countAfterSuiteMethods = 0;
        for (Method m : methods) {
            if (m.isAnnotationPresent(BeforeSuite.class)) {
                if (!Modifier.isStatic(m.getModifiers())) {
                    throw new TestRunnerExceptions("Method "+m.getName()+" should be a static");
                }
                if (countBeforeSuiteMethods > 0) {
                    throw new TestRunnerExceptions("Test class has more than one BeforeSuite annotations");
                }
                countBeforeSuiteMethods++;
            }
            if (m.isAnnotationPresent(AfterSuite.class)) {
                if (!Modifier.isStatic(m.getModifiers())) {
                    throw new TestRunnerExceptions("Method "+m.getName()+" should be a static");
                }
                if (countAfterSuiteMethods > 0) {
                    throw new TestRunnerExceptions("Test class has more than one AfterSuite annotations");
                }
                countAfterSuiteMethods++;
            }
            if (m.isAnnotationPresent(Test.class)) {
                if (Modifier.isStatic(m.getModifiers())) {
                    throw new TestRunnerExceptions("Method "+m.getName()+" should not be a static");
                }
            }
        }
    }
}
