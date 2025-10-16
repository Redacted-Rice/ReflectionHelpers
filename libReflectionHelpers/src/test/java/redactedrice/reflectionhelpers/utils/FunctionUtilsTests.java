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

class FunctionUtilsTests {
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
        assertEquals(expected, FunctionUtils.getVariable(so, "intField"));
        assertEquals(expected, FunctionUtils.getVariable(so, "getIntField()"));

        // List
        assertEquals(so.list, FunctionUtils.getVariable(so, "list"));
        assertEquals(so.list, FunctionUtils.getVariable(so, "getList()"));

        assertNull(FunctionUtils.getVariable(so, "obj"));
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

        assertEquals(expectedSo2, FunctionUtils.getVariable(so, "obj.intField"));
        assertEquals(expectedSo, FunctionUtils.getVariable(so, "obj.obj.getObj().obj.intField"));
        assertEquals(expectedSo2,
                FunctionUtils.getVariable(so, "getObj().getObj().obj.getIntField()"));
    }

    @Test
    void getVariable_badInputs() {
        SimpleObject so = new SimpleObject("test obj", 3);

        assertThrows(NoSuchFieldException.class, () -> FunctionUtils.getVariable(so, ""));
        assertThrows(NoSuchFieldException.class, () -> FunctionUtils.getVariable(so, "unfound"));
        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.getVariable(so, "unfound()"));
        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.getVariable(so, "uncomparableObj()"));
        assertThrows(NoSuchFieldException.class,
                () -> FunctionUtils.getVariable(so, "getUncomparableObject"));

        assertThrows(NullPointerException.class,
                () -> FunctionUtils.getVariable(so, "obj.unfound.unfound2"));

        so.obj = new SimpleObject("test obj2", 0);
        so.obj.obj = so;

        assertThrows(NoSuchFieldException.class,
                () -> FunctionUtils.getVariable(so, "obj.unfound.unfound2"));
        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.getVariable(so, "obj.unfound()"));

        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.getVariable(so, "getObj().obj.intIsEqualTo()"));
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
        List<?> fieldStreamArray = FunctionUtils.getVariableStream(so, "intField").toList();
        assertEquals(1, fieldStreamArray.size());
        assertEquals(3, fieldStreamArray.get(0));

        // collections/maps
        assertIterableEquals(so.list, FunctionUtils.getVariableStream(so, "list").toList());
        assertIterableEquals(so.map.values(),
                FunctionUtils.getVariableStream(so, "map").toList());
        assertIterableEquals(so.map.keySet(),
                FunctionUtils.getVariableStream(so, "map.keySet()").toList());
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
                FunctionUtils.getMapVariableKeysStream(so, "map").toList());
        assertIterableEquals(so.map.values(),
                FunctionUtils.getMapVariableValuesStream(so, "map").toList());
    }

    @Test
    void getMapVariableStream_badInputs()
            throws IllegalArgumentException {
        SimpleObject so = new SimpleObject("test obj", 3);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);
        so.map.put(4, "14");
        so.map.put(5, "15");

        // Non map
        assertThrows(ClassCastException.class,
                () -> FunctionUtils.getMapVariableKeysStream(so, "list"));
        assertThrows(ClassCastException.class,
                () -> FunctionUtils.getMapVariableKeysStream(so, "intField"));
        assertThrows(NoSuchFieldException.class,
                () -> FunctionUtils.getMapVariableKeysStream(so, "unused"));
        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.getMapVariableKeysStream(so, "list()"));
        assertThrows(NullPointerException.class,
                () -> FunctionUtils.getMapVariableKeysStream(so, "obj.unused"));
    }

    @Test
    void getFromGetter()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        int expected = 3;
        SimpleObject so = new SimpleObject("test obj", 3);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);
        so.boolField = true;
        so.stringField = "test";

        // primative
        assertEquals(expected, FunctionUtils.getFromGetter(so, "intField"));
        assertEquals(so.stringField, FunctionUtils.getFromGetter(so, "stringField"));
        assertEquals(so.boolField, FunctionUtils.getFromGetter(so, "boolField"));

        // List
        assertEquals(so.list, FunctionUtils.getFromGetter(so, "list"));

        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.getFromGetter(so, "stringNoGetter"));
        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.getFromGetter(so, "getIntField"));
        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.getFromGetter(so, "getIntField()"));
    }

    @Test
    void getFromField()
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, 
            IllegalAccessException {
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
        assertEquals(expected, FunctionUtils.getFromField(so, "intField"));
        assertEquals(so.stringField, FunctionUtils.getFromField(so, "stringField"));
        assertEquals(so.stringNoGetterSetter,
                FunctionUtils.getFromField(so, "stringNoGetterSetter"));
        assertNull(FunctionUtils.getFromField(so, "obj"));

        // List
        assertEquals(so.list, FunctionUtils.getFromField(so, "list"));

        assertThrows(NoSuchFieldException.class,
                () -> FunctionUtils.getFromField(so, "intPrivate"));
        assertThrows(NoSuchFieldException.class,
                () -> FunctionUtils.getFromField(so, "getIntField"));
        assertThrows(NoSuchFieldException.class,
                () -> FunctionUtils.getFromField(so, "getIntField()"));

    }

    @Test
    void setVariable()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);

        // objects
        assertNull(FunctionUtils.setVariable(so, "stringField", "1"));
        assertEquals("1", so.stringField);
        assertNull(FunctionUtils.setVariable(so, "setStringField()", "2"));
        assertEquals("2", so.stringField);

        // object null value
        assertNull(FunctionUtils.setVariable(so, "stringField", null));
        assertNull(so.stringField);
        assertNull(FunctionUtils.setVariable(so, "setStringField()", null));
        assertNull(so.stringField);

        // primitives
        assertNull(FunctionUtils.setVariable(so, "intField", 2));
        assertEquals(2, so.intField);
        assertNull(FunctionUtils.setVariable(so, "setIntField()", 1));
        assertEquals(1, so.intField);

        // Test return vals
        assertTrue((boolean) FunctionUtils.setVariable(so, "setIntFieldReturn()", 7));
        assertEquals(7, so.intField);
        assertFalse((boolean) FunctionUtils.setVariable(so, "setIntFieldReturn()", -4));
        assertEquals(-4, so.intField); // sets anyway

        assertTrue((boolean) FunctionUtils.setVariable(so, "setIntFieldReturnBoxed()", 6));
        assertEquals(6, so.intField);
        assertNull(FunctionUtils.setVariable(so, "setIntFieldReturnBoxed()", 0)); // returns null
        assertEquals(0, so.intField); // sets anyway
        assertFalse((boolean) FunctionUtils.setVariable(so, "setIntFieldReturnBoxed()", -2));
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
        assertNull(FunctionUtils.setVariable(so, "obj.obj.stringField", "1"));
        assertEquals("1", so.stringField);
        assertNull(FunctionUtils.setVariable(so, "obj.obj.setStringField()", "2"));
        assertEquals("2", so.stringField);

        // primitives
        assertNull(FunctionUtils.setVariable(so, "obj.obj.intField", 2));
        assertEquals(2, so.intField);
        assertNull(FunctionUtils.setVariable(so, "obj.obj.setIntField()", 1));
        assertEquals(1, so.intField);
    }

    @Test
    void setVariable_badInput() {
        SimpleObject so = new SimpleObject("test obj", 3);

        assertThrows(NullPointerException.class,
                () -> FunctionUtils.setVariable(so, "obj.unused", "2"));

        so.obj = new SimpleObject("test obj2", 1);
        so.obj.obj = so;

        assertThrows(NoSuchFieldException.class,
                () -> FunctionUtils.setVariable(so, "unused", "2"));
        assertThrows(NoSuchFieldException.class,
                () -> FunctionUtils.setVariable(so, "obj.unused", "2"));
        assertThrows(NoSuchFieldException.class,
                () -> FunctionUtils.setVariable(so, "obj.recurse.unused", "2"));

        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.setVariable(so, "obj()", "2"));
        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.setVariable(so, "obj.obj.unused()", "2"));
        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.setVariable(so, "getIntField()", "2")); // wrong
        // params
        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.setVariable(so, "getIntField()", null)); // wrong
        // params

        assertThrows(IllegalArgumentException.class,
                () -> FunctionUtils.setVariable(so, "intField", "2")); // param
        // of
        // wrong
        // type

        assertThrows(IllegalArgumentException.class,
                () -> FunctionUtils.setVariable(so, "stringField", 2));
        assertThrows(IllegalArgumentException.class,
                () -> FunctionUtils.setVariable(so, "intField", null)); // null
        // val
        // for
        // primitive
    }

    @Test
    void setWithSetter()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        SimpleObject so = new SimpleObject("test obj", 3);

        // objects
        FunctionUtils.setWithSetter(so, "stringField", "1");
        assertEquals("1", so.stringField);
        // objects
        FunctionUtils.setWithSetter(so, "stringField", "2");
        assertEquals("2", so.stringField);
        FunctionUtils.setWithSetter(so, "stringField", null);
        assertNull(so.stringField);

        // primitives
        FunctionUtils.setWithSetter(so, "intField", 2);
        assertEquals(2, so.intField);
        FunctionUtils.setWithSetter(so, "intField", 2);
        assertEquals(2, so.intField);
        FunctionUtils.setWithSetter(so, "boolSetterWithNoSet", true);
        assertTrue(so.boolSetterWithNoSet);

        String expected = "Some Value";
        so.stringNoGetterSetter = expected;
        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.setWithSetter(so, "stringNoGetterSetter", "test"));
        assertEquals(expected, so.stringNoGetterSetter);
    }

    @Test
    void invoke()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);

        // objects
        assertNull(FunctionUtils.invoke(so, "setStringField()", "2"));
        assertEquals("2", so.stringField);
        assertEquals("2", FunctionUtils.invoke(so, "getStringField()"));

        // object null value
        assertNull(FunctionUtils.invoke(so, "setStringField()", (String) null));
        assertNull(so.stringField);

        // primitives
        assertNull(FunctionUtils.invoke(so, "setIntField()", 1));
        assertEquals(1, so.intField);
        assertEquals(1, FunctionUtils.invoke(so, "getIntField()"));
        // Test passing an empty array of args
        assertEquals(1, FunctionUtils.invoke(so, "getIntField()", new Object[] {}));

        // Test return vals
        assertTrue((boolean) FunctionUtils.invoke(so, "setIntFieldReturn()", 7));
        assertEquals(7, so.intField);
        assertFalse((boolean) FunctionUtils.invoke(so, "setIntFieldReturn()", -4));
        assertEquals(-4, so.intField); // sets anyway
        assertNull(FunctionUtils.invoke(so, "setIntFieldReturnBoxed()", 0)); // returns null
        assertEquals(0, so.intField); // sets anyway

        // multi args
        assertNull(FunctionUtils.invoke(so, "setIntAndStringField()", 2, "multi")); // returns
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
        assertNull(FunctionUtils.invoke(so, "obj.obj.setStringField()", "2"));
        assertEquals("2", so.stringField);
        assertEquals("2", FunctionUtils.invoke(so, "getStringField()"));

        // primitives
        assertNull(FunctionUtils.invoke(so, "obj.obj.setIntField()", 1));
        assertEquals(1, so.intField);
        assertEquals(1, FunctionUtils.invoke(so, "getIntField()"));

        // multi args
        assertNull(FunctionUtils.invoke(so, "getObj().obj.setIntAndStringField()", 2, "multi")); // returns
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
        assertNull(FunctionUtils.invoke(so, "setField()", "2"));
        assertEquals("2", so.stringField);
        assertEquals("2", FunctionUtils.invoke(so, "getStringField()"));

        // primitives
        assertNull(FunctionUtils.invoke(so, "setField()", 1));
        assertEquals(1, so.intField);
        assertEquals(1, FunctionUtils.invoke(so, "getIntField()"));
    }

    @Test
    void invoke_badInput() {
        SimpleObject so = new SimpleObject("test obj", 3);

        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.invoke(so, "setVariable()", 2, "3"));
        assertThrows(IllegalArgumentException.class,
                () -> FunctionUtils.invoke(so, "setVariable", 2));

        // null for primitive arg
        assertThrows(IllegalArgumentException.class,
                () -> FunctionUtils.invoke(so, "setIntField()", (Integer) null));

        // wrong primitive to test all branches
        assertThrows(NoSuchMethodException.class,
                () -> FunctionUtils.invoke(so, "setVariable()", 2.1));
    }
}