package redactedrice.reflectionhelpers.objects;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import redactedrice.reflectionhelpers.utils.ReflectionUtils;

// A wrapper for extending an existing objects dynamically in code
public class ExtendableObject {
    Object obj;
    HashMap<String, Object> attrMap;

    public static ExtendableObject create(Object obj) {
        if (obj == null) {
            return null;
        }
        return new ExtendableObject(obj);
    }

    protected ExtendableObject(Object obj) {
        this.obj = obj;
        attrMap = new HashMap<>();
    }

    protected ExtendableObject() {
        this.obj = this;
        attrMap = new HashMap<>();
    }

    public Object eoGet(String fieldName) {
        try {
            return ReflectionUtils.getFromGetter(obj, fieldName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        	// Not a method, try as a field
        }
        try {
            return ReflectionUtils.getFromField(obj, fieldName);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException
                | SecurityException e) {
        	// Not a field, fall through to try the extended field
        }
        return attrMap.get(fieldName);
    }

    public Object eoGet(String fieldName, Object defaultVal) {
        Object foundObj = eoGet(fieldName);
        if (foundObj == null) {
            foundObj = defaultVal;
        }
        return foundObj;
    }

    public void eoSet(String fieldName, Object val) {
        boolean set = setInternal(fieldName, val);
        if (!set) {
            attrMap.put(fieldName, val);
        }
    }

    public boolean eoSetIfExists(String fieldName, Object val) {
        boolean set = setInternal(fieldName, val);
        if (!set) {
            set = attrMap.computeIfPresent(fieldName, (key, prevVal) -> val) != null;
        }
        return set;
    }

    protected boolean setInternal(String fieldName, Object val) {
        try {
            ReflectionUtils.setWithSetter(obj, fieldName, val);
            return true;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        	// Not a method, try as a field
        }
        try {
            ReflectionUtils.setWithField(obj, fieldName, val);
            return true;
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException
                | SecurityException e) {
        	// Not a field, fall through which just returns false
        }
        return false;
    }

    public Object getObject() {
        return obj;
    }

    protected boolean setObject(Object obj) {
        if (obj != null) {
            this.obj = obj;
            return true;
        }
        return false;
    }
}
