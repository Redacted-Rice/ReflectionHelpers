package redactedrice.reflectionhelpers.objects;


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

class ReflectionObjectTests {
    @Test
    void create() {
        SimpleObject so = new SimpleObject("test obj", 3);
        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
        assertEquals(so, ro.getObject());

        assertNull(ReflectionObject.create(null));
    }

    @Test
    void forceNonNull() {
        assertNull(ReflectionObject.create(null));

        SimpleObject so = new SimpleObject("test obj", 3);
        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
        assertFalse(ro.setObject(null));
        assertEquals(so, ro.getObject()); // Ensure not changed

        SimpleObject so2 = new SimpleObject("test obj", 3);
        assertTrue(ro.setObject(so2));
        assertEquals(so2, ro.getObject()); // Ensure changed
    }

    @Test
    void getField()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        int expected = 3;
        SimpleObject so = new SimpleObject("test obj", 3);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);
        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);

        // primative
        assertEquals(expected, ro.getField("intField"));
        assertEquals(expected, ro.getField("getIntField()"));

        // List
        assertEquals(so.list, ro.getField("list"));
        assertEquals(so.list, ro.getField("getList()"));

        assertNull(ro.getField("obj"));
    }

    @Test
    void getField_nested()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        int expectedSo = 3;
        int expectedSo2 = 7;
        SimpleObject so = new SimpleObject("test obj", expectedSo);
        SimpleObject so2 = new SimpleObject("test obj2", expectedSo2);
        so.obj = so2;
        so2.obj = so;

        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
        assertEquals(expectedSo2, ro.getField("obj.intField"));
        assertEquals(expectedSo, ro.getField("obj.obj.getObj().obj.intField"));
        assertEquals(expectedSo2, ro.getField("getObj().getObj().obj.getIntField()"));
    }

    @Test
    void getField_badInputs()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        ReflectionObject<SimpleObject> ro = ReflectionObject
                .create(new SimpleObject("test obj", 3));

        assertThrows(NoSuchFieldException.class, () -> ro.getField(""));
        assertThrows(NoSuchFieldException.class, () -> ro.getField("unfound"));
        assertThrows(NoSuchMethodException.class, () -> ro.getField("unfound()"));
        assertThrows(NoSuchMethodException.class, () -> ro.getField("uncomparableObj()"));
        assertThrows(NoSuchFieldException.class, () -> ro.getField("getUncomparableObject"));

        assertThrows(NullPointerException.class, () -> ro.getField("obj.unfound.unfound2"));

        ro.getObject().obj = new SimpleObject("test obj2", 0);
        ro.getObject().obj.obj = ro.getObject();

        assertThrows(NoSuchFieldException.class, () -> ro.getField("obj.unfound.unfound2"));
        assertThrows(NoSuchMethodException.class, () -> ro.getField("obj.unfound()"));

        assertThrows(NoSuchMethodException.class, () -> ro.getField("getObj().obj.intIsEqualTo()"));
    }

    @Test
    void getFieldStream()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);
        so.map.put(4, "14");
        so.map.put(5, "15");

        // get a single field
        List<?> fieldStreamArray = ro.getFieldStream("intField").toList();
        assertEquals(1, fieldStreamArray.size());
        assertEquals(3, fieldStreamArray.get(0));

        // collections/maps
        assertIterableEquals(so.list, ro.getFieldStream("list").toList());
        assertIterableEquals(so.map.values(), ro.getFieldStream("map").toList());
        assertIterableEquals(so.map.keySet(), ro.getFieldStream("map.keySet()").toList());
    }

    @Test
    void getMapFieldStream()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);
        so.map.put(4, "14");
        so.map.put(5, "15");

        assertIterableEquals(so.map.keySet(), ro.getMapFieldKeysStream("map").toList());
        assertIterableEquals(so.map.values(), ro.getMapFieldValuesStream("map").toList());
    }

    @Test
    void getMapFieldStream_badInputs()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);
        so.list.add(1);
        so.list.add(2);
        so.list.add(3);
        so.map.put(4, "14");
        so.map.put(5, "15");

        // Non map
        assertThrows(ClassCastException.class, () -> ro.getMapFieldKeysStream("list"));
        assertThrows(ClassCastException.class, () -> ro.getMapFieldKeysStream("intField"));
        assertThrows(NoSuchFieldException.class, () -> ro.getMapFieldKeysStream("unused"));
        assertThrows(NoSuchMethodException.class, () -> ro.getMapFieldKeysStream("list()"));
        assertThrows(NullPointerException.class, () -> ro.getMapFieldKeysStream("obj.unused"));
    }

    @Test
    void setField()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);

        // objects
        assertNull(ro.setField("stringField", "1"));
        assertEquals("1", so.stringField);
        assertNull(ro.setField("setStringField()", "2"));
        assertEquals("2", so.stringField);

        // object null value
        assertNull(ro.setField("stringField", null));
        assertNull(so.stringField);
        assertNull(ro.setField("setStringField()", null));
        assertNull(so.stringField);

        // primitives
        assertNull(ro.setField("intField", 2));
        assertEquals(2, so.intField);
        assertNull(ro.setField("setIntField()", 1));
        assertEquals(1, so.intField);

        // Test return vals
        assertTrue((boolean) ro.setField("setIntFieldReturn()", 7));
        assertEquals(7, so.intField);
        assertFalse((boolean) ro.setField("setIntFieldReturn()", -4));
        assertEquals(-4, so.intField); // sets anyway

        assertTrue((boolean) ro.setField("setIntFieldReturnBoxed()", 6));
        assertEquals(6, so.intField);
        assertNull(ro.setField("setIntFieldReturnBoxed()", 0)); // returns null
        assertEquals(0, so.intField); // sets anyway
        assertFalse((boolean) ro.setField("setIntFieldReturnBoxed()", -2));
        assertEquals(-2, so.intField); // sets anyway
    }

    @Test
    void setField_nested()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        so.obj = new SimpleObject("test obj2", 1);
        so.obj.obj = so;

        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);

        // objects
        assertNull(ro.setField("obj.obj.stringField", "1"));
        assertEquals("1", so.stringField);
        assertNull(ro.setField("obj.obj.setStringField()", "2"));
        assertEquals("2", so.stringField);

        // primitives
        assertNull(ro.setField("obj.obj.intField", 2));
        assertEquals(2, so.intField);
        assertNull(ro.setField("obj.obj.setIntField()", 1));
        assertEquals(1, so.intField);
    }

    /// TODO: Multiple matching methods

    // TODO: Invoke

    @Test
    void setField_badInput() {
        SimpleObject so = new SimpleObject("test obj", 3);
        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);

        assertThrows(NullPointerException.class, () -> ro.setField("obj.unused", "2"));

        so.obj = new SimpleObject("test obj2", 1);
        so.obj.obj = so;

        assertThrows(NoSuchFieldException.class, () -> ro.setField("unused", "2"));
        assertThrows(NoSuchFieldException.class, () -> ro.setField("obj.unused", "2"));
        assertThrows(NoSuchFieldException.class, () -> ro.setField("obj.recurse.unused", "2"));

        assertThrows(NoSuchMethodException.class, () -> ro.setField("obj()", "2"));
        assertThrows(NoSuchMethodException.class, () -> ro.setField("obj.obj.unused()", "2"));
        assertThrows(NoSuchMethodException.class, () -> ro.setField("getIntField()", "2")); // wrong
                                                                                            // params
        assertThrows(NoSuchMethodException.class, () -> ro.setField("getIntField()", null)); // wrong
                                                                                             // params

        assertThrows(IllegalArgumentException.class, () -> ro.setField("intField", "2")); // param
                                                                                          // of
                                                                                          // wrong
                                                                                          // type

        assertThrows(IllegalArgumentException.class, () -> ro.setField("stringField", 2));
        assertThrows(IllegalArgumentException.class, () -> ro.setField("intField", null)); // null
                                                                                           // val
                                                                                           // for
                                                                                           // primitive
    }

    @Test
    void invoke()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);

        // objects
        assertNull(ro.invoke("setStringField()", "2"));
        assertEquals("2", so.stringField);
        assertEquals("2", ro.invoke("getStringField()"));

        // object null value
        assertNull(ro.invoke("setStringField()", (String) null));
        assertNull(so.stringField);

        // primitives
        assertNull(ro.invoke("setIntField()", 1));
        assertEquals(1, so.intField);
        assertEquals(1, ro.invoke("getIntField()"));

        // Test return vals
        assertTrue((boolean) ro.invoke("setIntFieldReturn()", 7));
        assertEquals(7, so.intField);
        assertFalse((boolean) ro.invoke("setIntFieldReturn()", -4));
        assertEquals(-4, so.intField); // sets anyway
        assertNull(ro.invoke("setIntFieldReturnBoxed()", 0)); // returns null
        assertEquals(0, so.intField); // sets anyway

        // multi args
        assertNull(ro.invoke("setIntAndStringField()", 2, "multi")); // returns null
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

        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);

        // objects
        assertNull(ro.invoke("obj.obj.setStringField()", "2"));
        assertEquals("2", so.stringField);
        assertEquals("2", ro.invoke("getStringField()"));

        // primitives
        assertNull(ro.invoke("obj.obj.setIntField()", 1));
        assertEquals(1, so.intField);
        assertEquals(1, ro.invoke("getIntField()"));

        // multi args
        assertNull(ro.invoke("getObj().obj.setIntAndStringField()", 2, "multi")); // returns
                                                                                  // null
        assertEquals(2, so.intField); // sets anyway
        assertEquals("multi", so.stringField); // sets anyway
    }

    @Test
    void invoke_overloadedFn()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);

        // objects
        assertNull(ro.invoke("setField()", "2"));
        assertEquals("2", so.stringField);
        assertEquals("2", ro.invoke("getStringField()"));

        // primitives
        assertNull(ro.invoke("setField()", 1));
        assertEquals(1, so.intField);
        assertEquals(1, ro.invoke("getIntField()"));
    }

    @Test
    void invoke_badInput()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, NoSuchFieldException {
        SimpleObject so = new SimpleObject("test obj", 3);
        ReflectionObject<SimpleObject> ro = ReflectionObject.create(so);

        assertThrows(NoSuchMethodException.class, () -> ro.invoke("setField()", 2, "3"));
        assertThrows(NoSuchMethodException.class, () -> ro.invoke("setField", 2));

        // null for primitive arg
        assertThrows(IllegalArgumentException.class,
                () -> ro.invoke("setIntField()", (Integer) null));

        // wrong primitive to test all branches
        assertThrows(NoSuchMethodException.class, () -> ro.invoke("setField()", 2.1));
    }
}