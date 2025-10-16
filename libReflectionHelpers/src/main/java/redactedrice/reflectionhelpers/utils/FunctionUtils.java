package redactedrice.reflectionhelpers.utils;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.stream.Stream;

public class FunctionUtils {
    private FunctionUtils() {
        throw new IllegalStateException("No constructor - utility class");
    }

    public static Object getVariable(Object obj, String pathToGetterOrField)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        return getObjectFromPath(obj, pathToGetterOrField, false);
    }

    public static Stream<Object> getVariableStream(Object obj, String pathToGetterOrField)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        return ConversionUtils.convertToStream(getVariable(obj, pathToGetterOrField));
    }

    public static Stream<Object> getMapVariableValuesStream(Object obj, String pathToGetterOrField)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        return getMapVariableStream(obj, pathToGetterOrField, true);
    }

    public static Stream<Object> getMapVariableKeysStream(Object obj, String pathToGetterOrField)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        return getMapVariableStream(obj, pathToGetterOrField, false);
    }

    // This cast is safe as its upcasting to object and wildcard and we don't need to worry
    // about primitives as they can't be used in a map
    @SuppressWarnings("unchecked")
    public static Stream<Object> getMapVariableStream(Object obj, String pathToGetterOrField,
            boolean valuesNotKeys)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        Object ret = getVariable(obj, pathToGetterOrField);
        if (valuesNotKeys) {
            return ConversionUtils.convertToStream(((Map<?, Object>) ret).values());
        } else {
            return ConversionUtils.convertToStream(((Map<Object, ?>) ret).keySet());
        }
    }

    public static Object getFromGetter(Object obj, String fieldName)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String capitalized = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

        // Check for a public getter
        Method getter = tryGetMethodByName(obj, "get" + capitalized);
        if (getter == null) {
            getter = tryGetMethodByName(obj, "is" + capitalized);
        }
        if (getter == null) {
            throw new NoSuchMethodException("Did not find appropriate getter for get" + capitalized
                    + " or is" + capitalized);
        }

        return getter.invoke(obj);
    }

    public static Object getFromField(Object obj, String fieldName) throws IllegalArgumentException,
            IllegalAccessException, NoSuchFieldException, SecurityException {
        return obj.getClass().getField(fieldName).get(obj);
    }

    // Invoke a call with multiple parameters (or just one too)
    // Doesn't work for fields
    public static Object invoke(Object obj, String pathToMethod, Object... values)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        Object owningObj = getObjectFromPath(obj, pathToMethod, true);
        int lastSeparator = pathToMethod.lastIndexOf('.');
        String lastPath = pathToMethod;
        if (lastSeparator >= 0) {
            lastPath = lastPath.substring(lastSeparator + 1);
        }

        if (lastPath.endsWith("()")) {
            Method method = getMethodByName(obj, lastPath.substring(0, lastPath.length() - 2),
                    values);
            return method.invoke(owningObj, values);
        } else {
            // Not a function. Should use setField instead
            throw new IllegalArgumentException("Did not pass a function - \"" + pathToMethod
                    + "\". Should end with () or if its not a function should use get/setField instead");
        }
    }

    // Set a field/parameter. Special case of invoke that handles fields or setters
    public static Object setVariable(Object obj, String pathToSetterOrField, Object value)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        Object owningObj = getObjectFromPath(obj, pathToSetterOrField, true);
        int lastSeparator = pathToSetterOrField.lastIndexOf('.');
        String lastPath = pathToSetterOrField;
        if (lastSeparator >= 0) {
            lastPath = lastPath.substring(lastSeparator + 1);
        }

        if (lastPath.endsWith("()")) {
            Method method = getMethodByName(obj, lastPath.substring(0, lastPath.length() - 2),
                    value);
            return method.invoke(obj, value);
        } else {
            setWithField(owningObj, lastPath, value);
        }
        return null;
    }

    public static void setWithSetter(Object obj, String fieldName, Object val)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String capitalized = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

        // Check for a public getter
        Method setter = tryGetMethodByName(obj, "set" + capitalized, val);
        if (setter == null) {
            setter = tryGetMethodByName(obj, fieldName, val);
        }
        if (setter == null) {
            throw new NoSuchMethodException(
                    "Did not find appropriate setter for set" + capitalized + " or " + fieldName);
        }
        setter.invoke(obj, val);
    }

    public static void setWithField(Object obj, String fieldName, Object val)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
            SecurityException {
        // getField only returns public functions. This is incorrectly tagged as an
        // accessibility bypass by SonarQube
        obj.getClass().getField(fieldName).set(obj, val); // NOSONAR
    }

    // Mainly intended as helper fns
    public static Method getMethodByName(Object obj, String methodName, Object... params)
            throws NoSuchMethodException {
        Method[] methods = obj.getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName) && doParamsMatch(m.getParameters(), params)) {
                return m;
            }
        }
        throw new NoSuchMethodException("Failed to find method " + methodName);
    }

    public static Method tryGetMethodByName(Object obj, String methodName, Object... params) {
        try {
            return getMethodByName(obj, methodName, params);
        } catch (NoSuchMethodException nsme) {
            return null;
        }
    }

    public static Object getObjectFromPath(Object obj, String path, boolean penultimate)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        String[] paths = path.split("\\.");
        if (paths.length <= 1 && penultimate) {
            return obj;
        }

        Object nextObj = getFromMethodOrField(obj, paths[0]);
        int lengths = !penultimate ? paths.length : paths.length - 1;
        for (int pathIndex = 1; pathIndex < lengths; pathIndex++) {
            nextObj = getFromMethodOrField(nextObj, paths[pathIndex]);
        }
        return nextObj;
    }

    public static Object getFromMethodOrField(Object obj, String methodOrField)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        if (methodOrField.endsWith("()")) {
            return getMethodByName(obj, methodOrField.substring(0, methodOrField.length() - 2))
                    .invoke(obj);
        } else {
            return obj.getClass().getField(methodOrField).get(obj);
        }
    }

    public static boolean doParamsMatch(Parameter[] methodParams, Object... params) {
        boolean match = true;
        if (methodParams.length != params.length) {
            match = false;
        } else {
            // same length - check their types
            match = true;
            for (int i = 0; i < methodParams.length; i++) {
                Class<?> mParamWrapped = ConversionUtils
                        .convertPrimitiveToWrapperClass(methodParams[i].getType());
                // if params are not null, we check they are the right type
                if (params[i] != null &&
                        // if they are not the right type
                        (!methodParams[i].getType().isInstance(params[i]) &&
                                // and they are not primitive types or the wrapped type does not
                                // match
                                (mParamWrapped == null || !mParamWrapped.isInstance(params[i])))) {
                    match = false;
                    break;
                }
            }
        }
        return match;
    }
}
