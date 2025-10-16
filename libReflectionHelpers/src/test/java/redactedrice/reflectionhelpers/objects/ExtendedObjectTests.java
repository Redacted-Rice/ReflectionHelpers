package redactedrice.reflectionhelpers.objects;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import redactedrice.reflectionhelpers.utils.FunctionUtils;
import support.SimpleObject;

class ExtendedObjectTests {
    public class Extended extends ExtendableObject {}

    @Test
    void create() {
        SimpleObject so = new SimpleObject("test obj", 3);
        ExtendableObject eo = ExtendableObject.create(so);
        assertEquals(so, eo.getObject());

        assertNull(ExtendableObject.create(null));
    }

    @Test
    void forceNonNull() {
        assertNull(ExtendableObject.create(null));

        SimpleObject so = new SimpleObject("test obj", 3);
        ExtendableObject eo = ExtendableObject.create(so);
        assertFalse(eo.setObject(null));
        assertEquals(so, eo.getObject()); // Ensure not changed

        SimpleObject so2 = new SimpleObject("test obj", 3);
        assertTrue(eo.setObject(so2));
        assertEquals(so2, eo.getObject()); // Ensure changed
    }

    @Test
    void extendedConstructor() {
        Extended extended = new Extended();
        assertEquals(extended, extended.getObject());
    }

    @Test
    void eoGet() {
        final String EXPECTED = "expected";
        final String DEFAULT = "default";
        final String FIELD = "field";

        SimpleObject so = new SimpleObject("test obj", 3);
        ExtendableObject eo = ExtendableObject.create(so);

        try (MockedStatic<FunctionUtils> mocked = mockStatic(FunctionUtils.class)) {
            mocked.when(() -> FunctionUtils.getFromGetter(any(), any())).thenReturn(EXPECTED);
            assertEquals(EXPECTED, eo.eoGet(FIELD));
            assertEquals(EXPECTED, eo.eoGet(FIELD, DEFAULT));

            mocked.when(() -> FunctionUtils.getFromGetter(any(), any()))
                    .thenThrow(new NoSuchMethodException());
            mocked.when(() -> FunctionUtils.getFromField(any(), any())).thenReturn(EXPECTED);
            assertEquals(EXPECTED, eo.eoGet(FIELD));
            assertEquals(EXPECTED, eo.eoGet(FIELD, DEFAULT));

            mocked.when(() -> FunctionUtils.getFromField(any(), any()))
                    .thenThrow(new NoSuchFieldException());
            assertNull(eo.eoGet(FIELD));
            assertEquals(DEFAULT, eo.eoGet(FIELD, DEFAULT));

            eo.attrMap.put(FIELD, EXPECTED);
            assertEquals(EXPECTED, eo.eoGet(FIELD));
            assertEquals(EXPECTED, eo.eoGet(FIELD, DEFAULT));
        }
    }

    @Test
    void eoSet() {
        final String EXPECTED = "expected";
        final String EXPECTED2 = "expectedToo";
        final String FIELD = "field";

        SimpleObject so = new SimpleObject("test obj", 3);
        ExtendableObject eo = spy(ExtendableObject.create(so));

        doReturn(true).when(eo).setInternal(any(), any());
        eo.eoSet(FIELD, EXPECTED);
        assertTrue(eo.attrMap.isEmpty());
        assertTrue(eo.eoSetIfExists(FIELD, EXPECTED));
        assertTrue(eo.attrMap.isEmpty());

        doReturn(false).when(eo).setInternal(any(), any());
        assertFalse(eo.eoSetIfExists(FIELD, EXPECTED));
        assertTrue(eo.attrMap.isEmpty());
        eo.eoSet(FIELD, EXPECTED);
        assertEquals(1, eo.attrMap.size());
        assertEquals(EXPECTED, eo.attrMap.get(FIELD));

        assertTrue(eo.eoSetIfExists(FIELD, EXPECTED2));
        assertEquals(1, eo.attrMap.size());
        assertEquals(EXPECTED2, eo.attrMap.get(FIELD));

    }

    @Test
    void setInternal() {
        final String EXPECTED = "expected";
        final String FIELD = "field";

        SimpleObject so = new SimpleObject("test obj", 3);
        ExtendableObject eo = spy(ExtendableObject.create(so));

        try (MockedStatic<FunctionUtils> mocked = mockStatic(FunctionUtils.class)) {
            mocked.when(() -> FunctionUtils.setWithSetter(any(), any(), any()))
                    .thenAnswer(invocation -> null);
            assertTrue(eo.setInternal(FIELD, EXPECTED));

            mocked.when(() -> FunctionUtils.setWithSetter(any(), any(), any()))
                    .thenThrow(new NoSuchMethodException());
            mocked.when(() -> FunctionUtils.setWithField(any(), any(), any()))
                    .thenAnswer(invocation -> null);
            assertTrue(eo.setInternal(FIELD, EXPECTED));

            mocked.when(() -> FunctionUtils.setWithField(any(), any(), any()))
                    .thenThrow(new NoSuchFieldException());
            assertFalse(eo.setInternal(FIELD, EXPECTED));
        }
    }
}
