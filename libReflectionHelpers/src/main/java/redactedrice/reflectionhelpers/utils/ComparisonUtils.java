package redactedrice.reflectionhelpers.utils;


public class ComparisonUtils {
    private ComparisonUtils() {
        throw new IllegalStateException("No constructor - utility class");
    }

    @SuppressWarnings("unchecked")
    public static int safeCompare(Object a, Object b) {
        if (a == null && b == null)
            return 0;
        if (a == null)
            return -1;
        if (b == null)
            return 1;

        try {
            return ((Comparable<Object>) a).compareTo(b);
        } catch (NullPointerException | ClassCastException e) {
            throw new IllegalArgumentException("Cannot compare " + a + " to " + b, e);
        }
    }
}
