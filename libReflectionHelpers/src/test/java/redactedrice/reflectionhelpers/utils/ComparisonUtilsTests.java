package redactedrice.reflectionhelpers.utils;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ComparisonUtilsTests {
    @Test
    void testEqualStrings() {
        assertEquals(0, ComparisonUtils.safeCompare("abc", "abc"));
        assertTrue(ComparisonUtils.safeCompare("abc", "def") < 0);
        assertTrue(ComparisonUtils.safeCompare("def", "abc") > 0);

        assertEquals(0, ComparisonUtils.safeCompare(42, 42));
        assertTrue(ComparisonUtils.safeCompare(1, 2) < 0);
        assertTrue(ComparisonUtils.safeCompare(2, 1) > 0);
    }

    @Test
    void safeCompare_nullAndBadValues() {
        assertEquals(0, ComparisonUtils.safeCompare(null, null));
        assertEquals(-1, ComparisonUtils.safeCompare(null, "abc"));
        assertEquals(1, ComparisonUtils.safeCompare("abc", null));

        assertThrows(IllegalArgumentException.class, () -> ComparisonUtils.safeCompare("abc", 123));

        Object nonComparable = new Object();
        assertThrows(IllegalArgumentException.class,
                () -> ComparisonUtils.safeCompare(nonComparable, "abc"));
        assertThrows(IllegalArgumentException.class,
                () -> ComparisonUtils.safeCompare("abc", nonComparable));
    }
}
