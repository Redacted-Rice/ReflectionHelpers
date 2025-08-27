package redactedrice.reflectionhelpers.utils;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.jupiter.api.Test;

import support.SimpleObject;

class ReflectionUtilsTests {
    @Test
    void getVariable()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        int expected = 3;
        SimpleObject so = new SimpleObject("test obj", 3);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);

        // primative
        assertEquals(expected, ReflectionUtils.getVariable(so, "intField"));
        assertEquals(expected, ReflectionUtils.getVariable(so, "getIntField()"));

        // List
        assertEquals(so.list, ReflectionUtils.getVariable(so, "list"));
        assertEquals(so.list, ReflectionUtils.getVariable(so, "getList()"));

        assertNull(ReflectionUtils.getVariable(so, "obj"));
    }

    @Test
    void getVariable_nested()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        int expectedSo = 3;
        int expectedSo2 = 7;
        SimpleObject so = new SimpleObject("test obj", expectedSo);
        SimpleObject so2 = new SimpleObject("test obj2", expectedSo2);
        so.obj = so2;
        so2.obj = so;

        assertEquals(expectedSo2, ReflectionUtils.getVariable(so, "obj.intField"));
        assertEquals(expectedSo, ReflectionUtils.getVariable(so, "obj.obj.getObj().obj.intField"));
        assertEquals(expectedSo2,
                ReflectionUtils.getVariable(so, "getObj().getObj().obj.getIntField()"));
    }

    @Test
    void getVariable_badInputs()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);

        assertThrows(NoSuchFieldException.class, () -> ReflectionUtils.getVariable(so, ""));
        assertThrows(NoSuchFieldException.class, () -> ReflectionUtils.getVariable(so, "unfound"));
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.getVariable(so, "unfound()"));
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.getVariable(so, "uncomparableObj()"));
        assertThrows(NoSuchFieldException.class,
                () -> ReflectionUtils.getVariable(so, "getUncomparableObject"));

        assertThrows(NullPointerException.class,
                () -> ReflectionUtils.getVariable(so, "obj.unfound.unfound2"));

        so.obj = new SimpleObject("test obj2", 0);
        so.obj.obj = so;

        assertThrows(NoSuchFieldException.class,
                () -> ReflectionUtils.getVariable(so, "obj.unfound.unfound2"));
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.getVariable(so, "obj.unfound()"));

        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.getVariable(so, "getObj().obj.intIsEqualTo()"));
    }

    @Test
    void getVariableStream()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);
        so.map.put(4, "14");
        so.map.put(5, "15");

        // get a single field
        List<?> fieldStreamArray = ReflectionUtils.getVariableStream(so, "intField").toList();
        assertEquals(1, fieldStreamArray.size());
        assertEquals(3, fieldStreamArray.get(0));

        // collections/maps
        assertIterableEquals(so.list, ReflectionUtils.getVariableStream(so, "list").toList());
        assertIterableEquals(so.map.values(),
                ReflectionUtils.getVariableStream(so, "map").toList());
        assertIterableEquals(so.map.keySet(),
                ReflectionUtils.getVariableStream(so, "map.keySet()").toList());
    }

    @Test
    void getMapVariableStream()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);
        so.map.put(4, "14");
        so.map.put(5, "15");

        assertIterableEquals(so.map.keySet(),
                ReflectionUtils.getMapVariableKeysStream(so, "map").toList());
        assertIterableEquals(so.map.values(),
                ReflectionUtils.getMapVariableValuesStream(so, "map").toList());
    }

    @Test
    void getMapVariableStream_badInputs()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);
        so.map.put(4, "14");
        so.map.put(5, "15");

        // Non map
        assertThrows(ClassCastException.class,
                () -> ReflectionUtils.getMapVariableKeysStream(so, "list"));
        assertThrows(ClassCastException.class,
                () -> ReflectionUtils.getMapVariableKeysStream(so, "intField"));
        assertThrows(NoSuchFieldException.class,
                () -> ReflectionUtils.getMapVariableKeysStream(so, "unused"));
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.getMapVariableKeysStream(so, "list()"));
        assertThrows(NullPointerException.class,
                () -> ReflectionUtils.getMapVariableKeysStream(so, "obj.unused"));
    }

    @Test
    void getFromGetter()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        int expected = 3;
        SimpleObject so = new SimpleObject("test obj", 3);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);
        so.boolField = true;
        so.stringField = "test";

        // primative
        assertEquals(expected, ReflectionUtils.getFromGetter(so, "intField"));
        assertEquals(so.stringField, ReflectionUtils.getFromGetter(so, "stringField"));
        assertEquals(so.boolField, ReflectionUtils.getFromGetter(so, "boolField"));

        // List
        assertEquals(so.list, ReflectionUtils.getFromGetter(so, "list"));

        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.getFromGetter(so, "stringNoGetter"));
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.getFromGetter(so, "getIntField"));
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.getFromGetter(so, "getIntField()"));
    }

    @Test
    void getFromField()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        int expected = 3;
        SimpleObject so = new SimpleObject("test obj", 3);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);
        so.boolField = true;
        so.stringField = "test";
        so.stringNoGetterSetter = "another test";
        so.setIntPrivate(expected);

        // primative
        assertEquals(expected, ReflectionUtils.getFromField(so, "intField"));
        assertEquals(so.stringField, ReflectionUtils.getFromField(so, "stringField"));
        assertEquals(so.stringNoGetterSetter,
                ReflectionUtils.getFromField(so, "stringNoGetterSetter"));
        assertNull(ReflectionUtils.getFromField(so, "obj"));

        // List
        assertEquals(so.list, ReflectionUtils.getFromField(so, "list"));

        assertThrows(NoSuchFieldException.class,
                () -> ReflectionUtils.getFromField(so, "intPrivate"));
        assertThrows(NoSuchFieldException.class,
                () -> ReflectionUtils.getFromField(so, "getIntField"));
        assertThrows(NoSuchFieldException.class,
                () -> ReflectionUtils.getFromField(so, "getIntField()"));

    }

    @Test
    void setVariable()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);

        // objects
        assertNull(ReflectionUtils.setVariable(so, "stringField", "1"));
        assertEquals("1", so.stringField);
        assertNull(ReflectionUtils.setVariable(so, "setStringField()", "2"));
        assertEquals("2", so.stringField);

        // object null value
        assertNull(ReflectionUtils.setVariable(so, "stringField", null));
        assertNull(so.stringField);
        assertNull(ReflectionUtils.setVariable(so, "setStringField()", null));
        assertNull(so.stringField);

        // primitives
        assertNull(ReflectionUtils.setVariable(so, "intField", 2));
        assertEquals(2, so.intField);
        assertNull(ReflectionUtils.setVariable(so, "setIntField()", 1));
        assertEquals(1, so.intField);

        // Test return vals
        assertTrue((boolean) ReflectionUtils.setVariable(so, "setIntFieldReturn()", 7));
        assertEquals(7, so.intField);
        assertFalse((boolean) ReflectionUtils.setVariable(so, "setIntFieldReturn()", -4));
        assertEquals(-4, so.intField); // sets anyway

        assertTrue((boolean) ReflectionUtils.setVariable(so, "setIntFieldReturnBoxed()", 6));
        assertEquals(6, so.intField);
        assertNull(ReflectionUtils.setVariable(so, "setIntFieldReturnBoxed()", 0)); // returns null
        assertEquals(0, so.intField); // sets anyway
        assertFalse((boolean) ReflectionUtils.setVariable(so, "setIntFieldReturnBoxed()", -2));
        assertEquals(-2, so.intField); // sets anyway
    }

    @Test
    void setVariable_nested()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        so.obj = new SimpleObject("test obj2", 1);
        so.obj.obj = so;

        // objects
        assertNull(ReflectionUtils.setVariable(so, "obj.obj.stringField", "1"));
        assertEquals("1", so.stringField);
        assertNull(ReflectionUtils.setVariable(so, "obj.obj.setStringField()", "2"));
        assertEquals("2", so.stringField);

        // primitives
        assertNull(ReflectionUtils.setVariable(so, "obj.obj.intField", 2));
        assertEquals(2, so.intField);
        assertNull(ReflectionUtils.setVariable(so, "obj.obj.setIntField()", 1));
        assertEquals(1, so.intField);
    }

    @Test
    void setVariable_badInput() {
        SimpleObject so = new SimpleObject("test obj", 3);

        assertThrows(NullPointerException.class,
                () -> ReflectionUtils.setVariable(so, "obj.unused", "2"));

        so.obj = new SimpleObject("test obj2", 1);
        so.obj.obj = so;

        assertThrows(NoSuchFieldException.class,
                () -> ReflectionUtils.setVariable(so, "unused", "2"));
        assertThrows(NoSuchFieldException.class,
                () -> ReflectionUtils.setVariable(so, "obj.unused", "2"));
        assertThrows(NoSuchFieldException.class,
                () -> ReflectionUtils.setVariable(so, "obj.recurse.unused", "2"));

        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.setVariable(so, "obj()", "2"));
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.setVariable(so, "obj.obj.unused()", "2"));
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.setVariable(so, "getIntField()", "2")); // wrong
        // params
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.setVariable(so, "getIntField()", null)); // wrong
        // params

        assertThrows(IllegalArgumentException.class,
                () -> ReflectionUtils.setVariable(so, "intField", "2")); // param
        // of
        // wrong
        // type

        assertThrows(IllegalArgumentException.class,
                () -> ReflectionUtils.setVariable(so, "stringField", 2));
        assertThrows(IllegalArgumentException.class,
                () -> ReflectionUtils.setVariable(so, "intField", null)); // null
        // val
        // for
        // primitive
    }

    @Test
    void setWithSetter()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);

        // objects
        ReflectionUtils.setWithSetter(so, "stringField", "1");
        assertEquals("1", so.stringField);
        // objects
        ReflectionUtils.setWithSetter(so, "stringField", "2");
        assertEquals("2", so.stringField);
        ReflectionUtils.setWithSetter(so, "stringField", null);
        assertNull(so.stringField);

        // primitives
        ReflectionUtils.setWithSetter(so, "intField", 2);
        assertEquals(2, so.intField);
        ReflectionUtils.setWithSetter(so, "intField", 2);
        assertEquals(2, so.intField);
        ReflectionUtils.setWithSetter(so, "boolSetterWithNoSet", true);
        assertTrue(so.boolSetterWithNoSet);

        String expected = "Some Value";
        so.stringNoGetterSetter = expected;
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.setWithSetter(so, "stringNoGetterSetter", "test"));
        assertEquals(expected, so.stringNoGetterSetter);
    }

    @Test
    void invoke()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);

        // objects
        assertNull(ReflectionUtils.invoke(so, "setStringField()", "2"));
        assertEquals("2", so.stringField);
        assertEquals("2", ReflectionUtils.invoke(so, "getStringField()"));

        // object null value
        assertNull(ReflectionUtils.invoke(so, "setStringField()", (String) null));
        assertNull(so.stringField);

        // primitives
        assertNull(ReflectionUtils.invoke(so, "setIntField()", 1));
        assertEquals(1, so.intField);
        assertEquals(1, ReflectionUtils.invoke(so, "getIntField()"));

        // Test return vals
        assertTrue((boolean) ReflectionUtils.invoke(so, "setIntFieldReturn()", 7));
        assertEquals(7, so.intField);
        assertFalse((boolean) ReflectionUtils.invoke(so, "setIntFieldReturn()", -4));
        assertEquals(-4, so.intField); // sets anyway
        assertNull(ReflectionUtils.invoke(so, "setIntFieldReturnBoxed()", 0)); // returns null
        assertEquals(0, so.intField); // sets anyway

        // multi args
        assertNull(ReflectionUtils.invoke(so, "setIntAndStringField()", 2, "multi")); // returns
                                                                                      // null
        assertEquals(2, so.intField); // sets anyway
        assertEquals("multi", so.stringField); // sets anyway
    }

    @Test
    void invoke_nested()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        so.obj = new SimpleObject("test obj2", 1);
        so.obj.obj = so;

        // objects
        assertNull(ReflectionUtils.invoke(so, "obj.obj.setStringField()", "2"));
        assertEquals("2", so.stringField);
        assertEquals("2", ReflectionUtils.invoke(so, "getStringField()"));

        // primitives
        assertNull(ReflectionUtils.invoke(so, "obj.obj.setIntField()", 1));
        assertEquals(1, so.intField);
        assertEquals(1, ReflectionUtils.invoke(so, "getIntField()"));

        // multi args
        assertNull(ReflectionUtils.invoke(so, "getObj().obj.setIntAndStringField()", 2, "multi")); // returns
        // null
        assertEquals(2, so.intField); // sets anyway
        assertEquals("multi", so.stringField); // sets anyway
    }

    @Test
    void invoke_overloadedFn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);

        // objects
        assertNull(ReflectionUtils.invoke(so, "setField()", "2"));
        assertEquals("2", so.stringField);
        assertEquals("2", ReflectionUtils.invoke(so, "getStringField()"));

        // primitives
        assertNull(ReflectionUtils.invoke(so, "setField()", 1));
        assertEquals(1, so.intField);
        assertEquals(1, ReflectionUtils.invoke(so, "getIntField()"));
    }

    @Test
    void invoke_badInput()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);

        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.invoke(so, "setVariable()", 2, "3"));
        assertThrows(IllegalArgumentException.class,
                () -> ReflectionUtils.invoke(so, "setVariable", 2));

        // null for primitive arg
        assertThrows(IllegalArgumentException.class,
                () -> ReflectionUtils.invoke(so, "setIntField()", (Integer) null));

        // wrong primitive to test all branches
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionUtils.invoke(so, "setVariable()", 2.1));
    }
}