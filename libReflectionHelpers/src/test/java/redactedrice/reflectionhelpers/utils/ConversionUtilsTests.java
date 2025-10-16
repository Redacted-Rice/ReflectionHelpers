package redactedrice.reflectionhelpers.utils;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Stream;

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

	static void testPrimitivePermutations(Class<?> clazz, Collection<?> expected, Object array, Object emptyArray) {
        assertIterableEquals(expected,
                ConversionUtils.convertPrimitiveArrayToStream(array, clazz).toList());
        assertIterableEquals(expected,
                ConversionUtils.convertPrimitiveArrayToStreamOrNull(array, clazz).toList());
        
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(emptyArray, clazz).count());
        assertNull(ConversionUtils.convertPrimitiveArrayToStreamOrNull(emptyArray, clazz));
        
        assertIterableEquals(expected,
                ConversionUtils.convertArrayToStream(array, clazz).toList());
        assertIterableEquals(expected,
                ConversionUtils.convertArrayToStreamOrNull(array, clazz).toList());
        
        assertEquals(0, ConversionUtils.convertArrayToStream(emptyArray, clazz).count());
        assertNull(ConversionUtils.convertArrayToStreamOrNull(emptyArray, clazz));
        
        // Null input
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(null, clazz).count());
        assertNull(ConversionUtils.convertPrimitiveArrayToStreamOrNull(null, clazz));
        
        // Single item
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(expected.iterator().next(), clazz).count());
        assertNull(ConversionUtils.convertPrimitiveArrayToStreamOrNull(expected.iterator().next(), clazz));
    }
    
    @Test
    void convertPrimitiveArrayToStream_wholeNumerics() {
        byte[] inputByte = {1, 2, 3, 4, 5};
        short[] inputShort = {1, 2, 3, 4, 5};
        int[] inputInt = {1, 2, 3, 4, 5};
        long[] inputLong = {1, 2, 3, 4, 5};

        Collection<Byte> expectedByte = new LinkedList<>();
        Collection<Short> expectedShort = new LinkedList<>();
        Collection<Integer> expectedInt = new LinkedList<>();
        Collection<Long> expectedLong = new LinkedList<>();

        for (int i = 0; i < inputByte.length; i++) {
            expectedByte.add(inputByte[i]);
            expectedShort.add(inputShort[i]);
            expectedInt.add(inputInt[i]);
            expectedLong.add(inputLong[i]);
        }
        
        testPrimitivePermutations(byte.class, expectedByte, inputByte, new byte[] {});
        assertIterableEquals(expectedByte,
                ConversionUtils.convertPrimitiveArrayToStream(inputByte).toList());
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(new byte[] {}).count());
        assertIterableEquals(expectedByte,
                ConversionUtils.convertPrimitiveArrayToStreamOrNull(inputByte).toList());
        
        testPrimitivePermutations(short.class, expectedShort, inputShort, new short[] {});
        assertIterableEquals(expectedShort,
                ConversionUtils.convertPrimitiveArrayToStream(inputShort).toList());
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(new short[] {}).count());
        assertIterableEquals(expectedShort,
                ConversionUtils.convertPrimitiveArrayToStreamOrNull(inputShort).toList());
        
        testPrimitivePermutations(int.class, expectedInt, inputInt, new int[] {});
        assertIterableEquals(expectedInt,
                ConversionUtils.convertPrimitiveArrayToStream(inputInt).toList());
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(new int[] {}).count());
        assertIterableEquals(expectedInt,
                ConversionUtils.convertPrimitiveArrayToStreamOrNull(inputInt).toList());
        
        testPrimitivePermutations(long.class, expectedLong, inputLong, new long[] {});
        assertIterableEquals(expectedLong,
                ConversionUtils.convertPrimitiveArrayToStream(inputLong).toList());
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(new long[] {}).count());
        assertIterableEquals(expectedLong,
                ConversionUtils.convertPrimitiveArrayToStreamOrNull(inputLong).toList());
    }

    @Test
    void convertPrimitiveArrayToStream_others() {
        float[] inputFloat = {1, 2, 3, 4, 5};
        double[] inputDouble = {1, 2, 3, 4, 5};
        boolean[] inputBool = {true, false, true, true, false};
        char[] inputChar = {1, 2, 3, 4, 5};

        Collection<Float> expectedFloat = new LinkedList<>();
        Collection<Double> expectedDouble = new LinkedList<>();
        Collection<Boolean> expectedBool = new LinkedList<>();
        Collection<Character> expectedChar = new LinkedList<>();

        for (int i = 0; i < inputFloat.length; i++) {
            expectedFloat.add(inputFloat[i]);
            expectedDouble.add(inputDouble[i]);
            expectedBool.add(inputBool[i]);
            expectedChar.add(inputChar[i]);
        }
        
        testPrimitivePermutations(float.class, expectedFloat, inputFloat, new float[] {});
        assertIterableEquals(expectedFloat,
                ConversionUtils.convertPrimitiveArrayToStream(inputFloat).toList());
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(new float[] {}).count());
        assertIterableEquals(expectedFloat,
                ConversionUtils.convertPrimitiveArrayToStreamOrNull(inputFloat).toList());
        
        testPrimitivePermutations(double.class, expectedDouble, inputDouble, new double[] {});
        assertIterableEquals(expectedDouble,
                ConversionUtils.convertPrimitiveArrayToStream(inputDouble).toList());
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(new double[] {}).count());
        assertIterableEquals(expectedDouble,
                ConversionUtils.convertPrimitiveArrayToStreamOrNull(inputDouble).toList());
        
        testPrimitivePermutations(boolean.class, expectedBool, inputBool, new boolean[] {});
        assertIterableEquals(expectedBool,
                ConversionUtils.convertPrimitiveArrayToStream(inputBool).toList());
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(new boolean[] {}).count());
        assertIterableEquals(expectedBool,
                ConversionUtils.convertPrimitiveArrayToStreamOrNull(inputBool).toList());
        
        testPrimitivePermutations(char.class, expectedChar, inputChar, new char[] {});
        assertIterableEquals(expectedChar,
                ConversionUtils.convertPrimitiveArrayToStream(inputChar).toList());
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(new char[] {}).count());
        assertIterableEquals(expectedChar,
                ConversionUtils.convertPrimitiveArrayToStreamOrNull(inputChar).toList());
    }
    
    @Test
    void convertPrimitiveArrayToStream_badTypes() {
        int[] input = {1, 2, 3, 4, 5};

        // Wrong class (primitive)
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(input, long.class).count());
        assertNull(ConversionUtils.convertPrimitiveArrayToStreamOrNull(input, long.class));

        // Wrong class (non-primitive)
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(input, String.class).count());
        assertNull(ConversionUtils.convertPrimitiveArrayToStreamOrNull(input, String.class));
        
        // Wrapper of primitive
        assertEquals(0, ConversionUtils.convertPrimitiveArrayToStream(input, Integer.class).count());
        assertNull(ConversionUtils.convertPrimitiveArrayToStreamOrNull(input, Integer.class));
    }

    @Test
    void convertArrayToStream() {
        String[] inputStr = {"th", "hr", "ro", "ow", "ws"};

        Collection<String> expectedStr = new LinkedList<>();
        for (int i = 0; i < inputStr.length; i++) {
            expectedStr.add(inputStr[i]);
        }

        // Null input
        assertEquals(0, ConversionUtils.convertArrayToStream(null, char.class).count());
        assertEquals(0, ConversionUtils.convertArrayToStream(null).count());
        assertNull(ConversionUtils.convertArrayToStreamOrNull(null, char.class));
        assertNull(ConversionUtils.convertArrayToStreamOrNull(null));
        
        // Empty array 
        assertEquals(0, ConversionUtils.convertArrayToStream(new Object[] {}, char.class).count());
        assertEquals(0, ConversionUtils.convertArrayToStream(new Object[] {}).count());
        assertNull(ConversionUtils.convertArrayToStreamOrNull(new Object[] {}, Object.class));
        assertNull(ConversionUtils.convertArrayToStreamOrNull(new Object[] {}));
        
        // Single value
        assertEquals(0, ConversionUtils.convertArrayToStream("not an array", char.class).count());
        assertNull(ConversionUtils.convertArrayToStreamOrNull("not an array", char.class));
        
        // Wrong class
        assertEquals(0, ConversionUtils.convertArrayToStream(inputStr, char.class).count());
        assertNull(ConversionUtils.convertArrayToStreamOrNull(inputStr, char.class));

        // With/without class
        assertIterableEquals(expectedStr, ConversionUtils.convertArrayToStream(inputStr).toList());
        assertIterableEquals(expectedStr, ConversionUtils.convertArrayToStreamOrNull(inputStr).toList());
        assertIterableEquals(expectedStr, ConversionUtils.convertArrayToStream(inputStr, String.class).toList());
        assertIterableEquals(expectedStr, ConversionUtils.convertArrayToStreamOrNull(inputStr, String.class).toList());
        assertIterableEquals(expectedStr,
                ConversionUtils.convertArrayToStream((Object) inputStr, String.class).toList());
        assertIterableEquals(expectedStr,
                ConversionUtils.convertArrayToStreamOrNull((Object) inputStr, String.class).toList());
    }
    
    @Test
    void convertToStream_convertToCollection_streamArrayCollection() {
        String[] inputStr = {"no", "ot", "th", "hr", "ro", "ow", "ws"};

        Collection<String> expectedStr = new LinkedList<>();
        Collection<Object> expectedStrObj = new LinkedList<>();
        for (int i = 0; i < inputStr.length; i++) {
            expectedStr.add(inputStr[i]);
            expectedStrObj.add(inputStr[i]);
        }
        
        // Stream
        assertIterableEquals(expectedStr,  ConversionUtils.convertToStream(Stream.of(inputStr), String.class).toList());
        assertIterableEquals(expectedStr,  ConversionUtils.convertToStreamOrNull(Stream.of(inputStr), String.class).toList());
        assertIterableEquals(expectedStr,  ConversionUtils.convertToCollection(Stream.of(inputStr), String.class));
        
        assertIterableEquals(expectedStrObj,  ConversionUtils.convertToStream(Stream.of(inputStr)).toList());
        assertIterableEquals(expectedStrObj,  ConversionUtils.convertToStreamOrNull(Stream.of(inputStr)).toList());
        assertIterableEquals(expectedStrObj,  ConversionUtils.convertToCollection(Stream.of(inputStr)));
        
        // Array
        assertIterableEquals(expectedStr, ConversionUtils.convertToStream(inputStr, String.class).toList());
        assertIterableEquals(expectedStr, ConversionUtils.convertToStreamOrNull(inputStr, String.class).toList());
        assertIterableEquals(expectedStr, ConversionUtils.convertToCollection(inputStr, String.class));
        assertIterableEquals(expectedStrObj, ConversionUtils.convertToStream(inputStr).toList());
        assertIterableEquals(expectedStrObj, ConversionUtils.convertToStreamOrNull(inputStr).toList());
        assertIterableEquals(expectedStrObj, ConversionUtils.convertToCollection(inputStr));
        
        // List
        assertIterableEquals(expectedStr,
                ConversionUtils.convertToStream(Arrays.asList(inputStr), String.class).toList());
        assertIterableEquals(expectedStr,
                ConversionUtils.convertToStreamOrNull(Arrays.asList(inputStr), String.class).toList());
        assertIterableEquals(expectedStr,
                ConversionUtils.convertToCollection(Arrays.asList(inputStr), String.class));
        
        assertIterableEquals(expectedStrObj,
                ConversionUtils.convertToStream(Arrays.asList(inputStr)).toList());
        assertIterableEquals(expectedStrObj,
                ConversionUtils.convertToStreamOrNull(Arrays.asList(inputStr)).toList());
        assertIterableEquals(expectedStrObj,
                ConversionUtils.convertToCollection(Arrays.asList(inputStr)));
    }
    
    @Test
    void convertToStream_convertToCollection_generic() {
        String[] inputStr = {"no", "ot", "th", "hr", "ro", "ow", "ws"};

        Collection<String> expectedStr = new LinkedList<>();
        Collection<Object> expectedStrObj = new LinkedList<>();
        for (int i = 0; i < inputStr.length; i++) {
            expectedStr.add(inputStr[i]);
            expectedStrObj.add(inputStr[i]);
        }

        // As an object
        assertIterableEquals(expectedStr,
                ConversionUtils.convertToStream((Object) inputStr, String.class).toList());
        assertIterableEquals(expectedStr,
                ConversionUtils.convertToStreamOrNull((Object) inputStr, String.class).toList());
        assertIterableEquals(expectedStr,
                ConversionUtils.convertToCollection((Object) inputStr, String.class));
        assertIterableEquals(expectedStrObj,
                ConversionUtils.convertToStream((Object) inputStr).toList());
        assertIterableEquals(expectedStrObj,
                ConversionUtils.convertToStreamOrNull((Object) inputStr).toList());
        assertIterableEquals(expectedStrObj,
                ConversionUtils.convertToCollection((Object) inputStr));

        Map<Integer, String> inputMap = new HashMap<>();
        for (int i = 0; i < inputStr.length; i++) {
            inputMap.put(i, inputStr[i]);
        }
        assertIterableEquals(expectedStr, ConversionUtils.convertToStream(inputMap, String.class).toList());
        assertIterableEquals(expectedStr, ConversionUtils.convertToStreamOrNull(inputMap, String.class).toList());
        assertIterableEquals(expectedStr, ConversionUtils.convertToCollection(inputMap, String.class));
        assertIterableEquals(expectedStrObj, ConversionUtils.convertToStream(inputMap).toList());
        assertIterableEquals(expectedStrObj, ConversionUtils.convertToStreamOrNull(inputMap).toList());
        assertIterableEquals(expectedStrObj, ConversionUtils.convertToCollection(inputMap));
        
        // Single Objects
        assertEquals("t", ConversionUtils.convertToStream("t", String.class).findFirst().get());
        assertNull(ConversionUtils.convertToStreamOrNull("t", String.class));
        assertEquals("t", ConversionUtils.convertToCollection("t", String.class).iterator().next());
        
        assertEquals("t", ConversionUtils.convertToStream("t").findFirst().get());
        assertNull(ConversionUtils.convertToStreamOrNull("t"));
        assertEquals("t", ConversionUtils.convertToCollection("t").iterator().next());
    }
    
    @Test
    void convertToStream_convertToCollection_badCases() {
        String[] inputStr = {"th", "hr", "ro", "ow", "ws"};

        assertEquals(0, ConversionUtils.convertToStream(null, char.class).count());
        assertNull(ConversionUtils.convertToStreamOrNull(null, char.class));
        assertEquals(0, ConversionUtils.convertToCollection(null, char.class).size());
        assertEquals(0, ConversionUtils.convertToStream("not an array", char.class).count());
        assertNull(ConversionUtils.convertToStreamOrNull("not an array", char.class));
        assertEquals(0, ConversionUtils.convertToCollection("not an array", char.class).size());
        // Wrong class (as array)
        assertEquals(0, ConversionUtils.convertToStream(inputStr, int.class).count());
        assertNull(ConversionUtils.convertToStreamOrNull(inputStr, int.class));
        assertEquals(0, ConversionUtils.convertToCollection(inputStr, int.class).size());
        // Wrong class (as stream)
        assertEquals(0, ConversionUtils.convertToStream(Stream.of(inputStr), int.class).count());
        assertNull(ConversionUtils.convertToStreamOrNull(Stream.of(inputStr), int.class));
        assertEquals(0, ConversionUtils.convertToCollection(Stream.of(inputStr), int.class).size());
    }
}
