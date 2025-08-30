package redactedrice.reflectionhelpers.utils;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ConversionUtilsTests {
    @Test
    void convertToWrapperClass() {
        assertEquals(Byte.class, ConversionUtils.convertPrimitiveToWrapperClass(byte.class));
        assertEquals(Short.class, ConversionUtils.convertPrimitiveToWrapperClass(short.class));
        assertEquals(Integer.class, ConversionUtils.convertPrimitiveToWrapperClass(int.class));
        assertEquals(Long.class, ConversionUtils.convertPrimitiveToWrapperClass(long.class));
        assertEquals(Float.class, ConversionUtils.convertPrimitiveToWrapperClass(float.class));
        assertEquals(Double.class, ConversionUtils.convertPrimitiveToWrapperClass(double.class));
        assertEquals(Boolean.class, ConversionUtils.convertPrimitiveToWrapperClass(boolean.class));
        assertEquals(Character.class, ConversionUtils.convertPrimitiveToWrapperClass(char.class));
        assertEquals(Void.class, ConversionUtils.convertPrimitiveToWrapperClass(void.class));

        assertNull(ConversionUtils.convertPrimitiveToWrapperClass(Object.class));
        assertNull(ConversionUtils.convertPrimitiveToWrapperClass(null));
    }

    @Test
    void convertToPrimitiveClass() {
        assertEquals(byte.class, ConversionUtils.convertWrapperToPrimitiveClass(Byte.class));
        assertEquals(short.class, ConversionUtils.convertWrapperToPrimitiveClass(Short.class));
        assertEquals(int.class, ConversionUtils.convertWrapperToPrimitiveClass(Integer.class));
        assertEquals(long.class, ConversionUtils.convertWrapperToPrimitiveClass(Long.class));
        assertEquals(float.class, ConversionUtils.convertWrapperToPrimitiveClass(Float.class));
        assertEquals(double.class, ConversionUtils.convertWrapperToPrimitiveClass(Double.class));
        assertEquals(boolean.class, ConversionUtils.convertWrapperToPrimitiveClass(Boolean.class));
        assertEquals(char.class, ConversionUtils.convertWrapperToPrimitiveClass(Character.class));
        assertEquals(void.class, ConversionUtils.convertWrapperToPrimitiveClass(Void.class));

        assertNull(ConversionUtils.convertWrapperToPrimitiveClass(Object.class));
        assertNull(ConversionUtils.convertWrapperToPrimitiveClass(null));
    }

    @Test
    void convertPrimitiveArrayToStream() {
        byte[] inputByte = {1, 2, 3, 4, 5};
        short[] inputShort = {1, 2, 3, 4, 5};
        int[] inputInt = {1, 2, 3, 4, 5};
        long[] inputLong = {1, 2, 3, 4, 5};
        float[] inputFloat = {1, 2, 3, 4, 5};
        double[] inputDouble = {1, 2, 3, 4, 5};
        boolean[] inputBool = {true, false, true, true, false};
        char[] inputChar = {1, 2, 3, 4, 5};

        Collection<Byte> expectedByte = new LinkedList<>();
        Collection<Short> expectedShort = new LinkedList<>();
        Collection<Integer> expectedInt = new LinkedList<>();
        Collection<Long> expectedLong = new LinkedList<>();
        Collection<Float> expectedFloat = new LinkedList<>();
        Collection<Double> expectedDouble = new LinkedList<>();
        Collection<Boolean> expectedBool = new LinkedList<>();
        Collection<Character> expectedChar = new LinkedList<>();

        for (int i = 0; i < inputByte.length; i++) {
            expectedByte.add(inputByte[i]);
            expectedShort.add(inputShort[i]);
            expectedInt.add(inputInt[i]);
            expectedLong.add(inputLong[i]);
            expectedFloat.add(inputFloat[i]);
            expectedDouble.add(inputDouble[i]);
            expectedBool.add(inputBool[i]);
            expectedChar.add(inputChar[i]);
        }

        assertIterableEquals(expectedByte,
                ConversionUtils.convertPrimitiveArrayToStream(inputByte, byte.class).toList());
        assertIterableEquals(expectedByte,
                ConversionUtils.convertPrimitiveArrayToStream((Object) inputByte, byte.class).toList());
        assertIterableEquals(expectedByte,
                ConversionUtils.convertArrayToStream(inputByte, byte.class).toList());

        assertIterableEquals(expectedShort,
                ConversionUtils.convertPrimitiveArrayToStream(inputShort, short.class).toList());
        assertIterableEquals(expectedShort,
                ConversionUtils.convertPrimitiveArrayToStream((Object) inputShort, short.class).toList());
        assertIterableEquals(expectedShort,
                ConversionUtils.convertArrayToStream(inputShort, short.class).toList());

        assertIterableEquals(expectedInt,
                ConversionUtils.convertPrimitiveArrayToStream(inputInt, int.class).toList());
        assertIterableEquals(expectedInt,
                ConversionUtils.convertPrimitiveArrayToStream((Object) inputInt, int.class).toList());
        assertIterableEquals(expectedInt, ConversionUtils.convertArrayToStream(inputInt, int.class).toList());

        assertIterableEquals(expectedLong,
                ConversionUtils.convertPrimitiveArrayToStream(inputLong, long.class).toList());
        assertIterableEquals(expectedLong,
                ConversionUtils.convertPrimitiveArrayToStream((Object) inputLong, long.class).toList());
        assertIterableEquals(expectedLong,
                ConversionUtils.convertArrayToStream(inputLong, long.class).toList());

        assertIterableEquals(expectedFloat,
                ConversionUtils.convertPrimitiveArrayToStream(inputFloat, float.class).toList());
        assertIterableEquals(expectedFloat,
                ConversionUtils.convertPrimitiveArrayToStream((Object) inputFloat, float.class).toList());
        assertIterableEquals(expectedFloat,
                ConversionUtils.convertArrayToStream(inputFloat, float.class).toList());

        assertIterableEquals(expectedDouble,
                ConversionUtils.convertPrimitiveArrayToStream(inputDouble, double.class).toList());
        assertIterableEquals(expectedDouble,
                ConversionUtils.convertPrimitiveArrayToStream((Object) inputDouble, double.class).toList());
        assertIterableEquals(expectedDouble,
                ConversionUtils.convertArrayToStream(inputDouble, double.class).toList());

        assertIterableEquals(expectedBool,
                ConversionUtils.convertPrimitiveArrayToStream(inputBool, boolean.class).toList());
        assertIterableEquals(expectedBool,
                ConversionUtils.convertPrimitiveArrayToStream((Object) inputBool, boolean.class).toList());
        assertIterableEquals(expectedBool,
                ConversionUtils.convertArrayToStream(inputBool, boolean.class).toList());

        assertIterableEquals(expectedChar,
                ConversionUtils.convertPrimitiveArrayToStream(inputChar, char.class).toList());
        assertIterableEquals(expectedChar,
                ConversionUtils.convertPrimitiveArrayToStream((Object) inputChar, char.class).toList());
        assertIterableEquals(expectedChar,
                ConversionUtils.convertArrayToStream(inputChar, char.class).toList());
    }
    @Test
    void convertPrimitiveArrayToStreamBadCases() {
        String[] inputStr = {"th", "hr", "ro", "ow", "ws"};
        
        // Non array
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(5, int.class).count());
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(null, int.class).count());
        // Not primitive
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(inputStr, String.class).count());
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(inputStr, char.class).count());
    }

    @Test
    void convertArrayToStream() {
        String[] inputStr = {"th", "hr", "ro", "ow", "ws"};

        Collection<String> expectedStr = new LinkedList<>();
        for (int i = 0; i < inputStr.length; i++) {
            expectedStr.add(inputStr[i]);
        }

        assertEquals(0, ConversionUtils.convertArrayToStream(null, char.class).count());
        assertEquals(0, ConversionUtils.convertArrayToStream("not an array", char.class).count());
        // Wrong class
        assertEquals(0, ConversionUtils.convertArrayToStream(inputStr, char.class).count());

        assertIterableEquals(expectedStr, ConversionUtils.convertArrayToStream(inputStr).toList());
        assertIterableEquals(expectedStr, ConversionUtils.convertArrayToStream(inputStr, String.class).toList());
        assertIterableEquals(expectedStr,
                ConversionUtils.convertArrayToStream((Object) inputStr, String.class).toList());
    }

    @Test
    void convertToStream() {
        String[] inputStr = {"th", "hr", "ro", "ow", "ws"};

        Collection<String> expectedStr = new LinkedList<>();
        Collection<Object> expectedStrObj = new LinkedList<>();
        for (int i = 0; i < inputStr.length; i++) {
            expectedStr.add(inputStr[i]);
            expectedStrObj.add(inputStr[i]);
        }

        assertEquals(0, ConversionUtils.convertToStream(null, char.class).count());
        assertEquals(0, ConversionUtils.convertToStream("not an array", char.class).count());
        // Wrong class
        assertEquals(0, ConversionUtils.convertToStream(inputStr, char.class).count());

        assertIterableEquals(expectedStr, ConversionUtils.convertToStream(inputStr, String.class).toList());
        assertIterableEquals(expectedStrObj, ConversionUtils.convertToStream(inputStr).toList());

        assertIterableEquals(expectedStr,
                ConversionUtils.convertToStream((Object) inputStr, String.class).toList());
        assertIterableEquals(expectedStrObj,
                ConversionUtils.convertToStream((Object) inputStr).toList());

        assertIterableEquals(expectedStr, ConversionUtils.convertToStream(inputStr, String.class).toList());
        assertIterableEquals(expectedStrObj, ConversionUtils.convertToStream(inputStr).toList());
        
        assertIterableEquals(expectedStr,
                ConversionUtils.convertToStream(Arrays.asList(inputStr), String.class).toList());
        assertIterableEquals(expectedStrObj,
                ConversionUtils.convertToStream(Arrays.asList(inputStr)).toList());

        Map<Integer, String> inputMap = new HashMap<>();
        for (int i = 0; i < inputStr.length; i++) {
            inputMap.put(i, inputStr[i]);
        }
        assertIterableEquals(expectedStr, ConversionUtils.convertToStream(inputMap, String.class).toList());
        assertIterableEquals(expectedStrObj, ConversionUtils.convertToStream(inputMap).toList());

        assertEquals("t", ConversionUtils.convertToStream("t", String.class).findFirst().get());
        assertEquals("t", ConversionUtils.convertToStream("t").findFirst().get());
    }
}
