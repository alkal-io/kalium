package io.alkal.kalium.internals.utils;

import io.alkal.kalium.annotations.On;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ziv Salzman
 * Created on 20-Jan-2019
 */
public class ReflectionUtils {

    public static List<Method> getMethodsAnnotatedWithOn(Class<?> clazz) {
        List<Method> onMethods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (isOnAnnotatedMethod(method)) {
                onMethods.add(method);
            }
        }

        return onMethods;

    }

    public static boolean isOnAnnotatedMethod(Method method) {
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation.annotationType() == On.class) return true;
        }
        return false;
    }
}
