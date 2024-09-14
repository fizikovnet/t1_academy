package com.fizikovnet.hw1;

import com.fizikovnet.hw1.annotations.*;

public class TestService {

    @BeforeSuite
    public static void setUp() {
        System.out.println("setUp method is invoked!");
    }


    @AfterSuite
    public static void tearDown() {
        System.out.println("tearDown method is invoked!");
    }

    @BeforeTest
    public static void beforeEachTest() {
        System.out.println("beforeEachTest method is invoked!");
    }

    @AfterTest
    public static void afterEachTest() {
        System.out.println("afterEachTest method is invoked!");
    }

    @Test
    public void testMethod2() {
        System.out.println("testMethod2 method is invoked!");
    }

    @Test(priority = 1)
    public void testMethod3() {
        System.out.println("testMethod3 method is invoked!");

    }

    @Test(priority = 10)
    public void testMethod1() {
        System.out.println("testMethod1 method is invoked!");

    }


}
