package redactedrice.reflectionhelpers.objects;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import redactedrice.reflectionhelpers.utils.ReflectionUtils;
import support.SimpleObject;

public class ExtendedObjectTests {
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

        try (MockedStatic<ReflectionUtils> mocked = mockStatic(ReflectionUtils.class)) {
            mocked.when(() -> ReflectionUtils.getFromGetter(any(), any())).thenReturn(EXPECTED);
            assertEquals(EXPECTED, eo.eoGet(FIELD));
            assertEquals(EXPECTED, eo.eoGet(FIELD, DEFAULT));

            mocked.when(() -> ReflectionUtils.getFromGetter(any(), any()))
                    .thenThrow(new NoSuchMethodException());
            mocked.when(() -> ReflectionUtils.getFromField(any(), any())).thenReturn(EXPECTED);
            assertEquals(EXPECTED, eo.eoGet(FIELD));
            assertEquals(EXPECTED, eo.eoGet(FIELD, DEFAULT));

            mocked.when(() -> ReflectionUtils.getFromField(any(), any()))
                    .thenThrow(new NoSuchFieldException());
            assertNull(eo.eoGet(FIELD));
            assertEquals(DEFAULT, eo.eoGet(FIELD, DEFAULT));

            eo.attrMap.put(FIELD, EXPECTED);
            assertEquals(EXPECTED, eo.eoGet(FIELD));
            assertEquals(EXPECTED, eo.eoGet(FIELD, DEFAULT));
        }
    }
}
