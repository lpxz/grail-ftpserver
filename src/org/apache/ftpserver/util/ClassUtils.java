package org.apache.ftpserver.util;

public class ClassUtils {

    public static boolean extendsClass(final Class<?> clazz, String className) {
        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null) {
            if (superClass.getName().equals(className)) {
                return true;
            }
            superClass = superClass.getSuperclass();
        }
        return false;
    }
}
