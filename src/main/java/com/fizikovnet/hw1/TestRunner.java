package com.fizikovnet.hw1;

import com.fizikovnet.hw1.annotations.*;
import com.fizikovnet.hw1.exceptions.TestRunnerExceptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class TestRunner {

    public static void runTests(Class<?> testClass) {
        try {
            Class<?> klass = Class.forName(testClass.getName());

            validateTestClassStructure(klass);
            Method[] methods = klass.getDeclaredMethods();

            invokeAnnotatedMethods(methods, BeforeSuite.class);

            Map<Integer, List<Method>> testMethods = getTestMethodsByPriority(methods);
            Map<Integer, Method> beforeAfterMethods = getBeforeAfterMethods(methods);
            Object testInstance = klass.getDeclaredConstructor().newInstance();

            executeTestMethods(testInstance, testMethods, beforeAfterMethods);

            invokeAnnotatedMethods(methods, AfterSuite.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<Integer, Method> getBeforeAfterMethods(Method[] methods) {
        Map<Integer, Method> methodMap = new HashMap<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeTest.class)) {
                methodMap.put(0, method);
            } else if (method.isAnnotationPresent(AfterTest.class)) {
                methodMap.put(1, method);
            }
        }
        return methodMap;
    }

    private static Map<Integer, List<Method>> getTestMethodsByPriority(Method[] methods) {
        Map<Integer, List<Method>> testMethodsMap = new TreeMap<>(Collections.reverseOrder());
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                int priority = method.getAnnotation(Test.class).priority();
                testMethodsMap.computeIfAbsent(priority, k -> new ArrayList<>()).add(method);
            }
        }
        return testMethodsMap;
    }

    private static void invokeAnnotatedMethods(Method[] methods, Class<? extends Annotation> annotation) throws InvocationTargetException, IllegalAccessException {
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotation)) {
                method.invoke(null);
            }
        }
    }

    private static void executeTestMethods(Object testInstance, Map<Integer, List<Method>> testMethods, Map<Integer, Method> beforeAfterMethods) throws InvocationTargetException, IllegalAccessException {
        for (List<Method> methods : testMethods.values()) {
            for (Method method : methods) {
                if (beforeAfterMethods.containsKey(0)) {
                    beforeAfterMethods.get(0).invoke(null);
                }
                if (method.isAnnotationPresent(CsvSource.class)) {
                    String csvData = method.getAnnotation(CsvSource.class).value();
                    String[] values = csvData.split(",");
                    Object[] parameters = convertParameters(method.getParameterTypes(), values);
                    method.invoke(testInstance, parameters);
                } else {
                    method.invoke(testInstance);
                }
                if (beforeAfterMethods.containsKey(1)) {
                    beforeAfterMethods.get(1).invoke(null);
                }
            }
        }
    }

    private static Object[] convertParameters(Class<?>[] parameterTypes, String[] values) {
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            String value = values[i].trim();
            parameters[i] = switch (parameterTypes[i].getName()) {
                case "int", "java.lang.Integer" -> Integer.parseInt(value);
                case "boolean", "java.lang.Boolean" -> Boolean.parseBoolean(value);
                case "byte", "java.lang.Byte" -> Byte.parseByte(value);
                case "short", "java.lang.Short" -> Short.parseShort(value);
                case "long", "java.lang.Long" -> Long.parseLong(value);
                case "float", "java.lang.Float" -> Float.parseFloat(value);
                case "double", "java.lang.Double" -> Double.parseDouble(value);
                case "char", "java.lang.Character" -> value.charAt(0);
                case "java.lang.String" -> value;
                default -> throw new IllegalArgumentException("Unsupported parameter type: " + parameterTypes[i]);
            };
        }
        return parameters;
    }

    private static void validateTestClassStructure(Class<?> klass) {
        Method[] methods = klass.getDeclaredMethods();
        boolean hasBeforeSuite = false;
        boolean hasAfterSuite = false;

        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                validateStaticMethod(method, "BeforeSuite");
                if (hasBeforeSuite) {
                    throw new TestRunnerExceptions("Test class has more than one BeforeSuite annotations");
                }
                hasBeforeSuite = true;
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                validateStaticMethod(method, "AfterSuite");
                if (hasAfterSuite) {
                    throw new TestRunnerExceptions("Test class has more than one AfterSuite annotations");
                }
                hasAfterSuite = true;
            }
            if (method.isAnnotationPresent(Test.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new TestRunnerExceptions("Method "+method.getName()+" should not be a static");
                }
            }
        }
    }

    private static void validateStaticMethod(Method method, String annotationName) {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new TestRunnerExceptions("Method " + method.getName() + " with " + annotationName + " annotation should be static");
        }
    }
}
