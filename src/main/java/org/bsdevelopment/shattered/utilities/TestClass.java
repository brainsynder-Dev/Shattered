package org.bsdevelopment.shattered.utilities;

public class TestClass {
    public static void main(String[] args) {
        System.out.println("Name: "+TestClass.class.getName());
        System.out.println("Package: "+TestClass.class.getPackageName());
        System.out.println("Simple: "+TestClass.class.getSimpleName());
        System.out.println("Canonical: "+TestClass.class.getCanonicalName());
    }
}
