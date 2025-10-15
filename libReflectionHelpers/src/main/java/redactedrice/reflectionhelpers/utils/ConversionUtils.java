package redactedrice.reflectionhelpers.utils;


import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class ConversionUtils {
    private ConversionUtils() {
        throw new IllegalStateException("No constructor - utility class");
    }

    public static Class<?> convertPrimitiveToWrapperClass(Class<?> toCheck) {
        if (byte.class.equals(toCheck)) {
            return Byte.class;
        } else if (short.class.equals(toCheck)) {
            return Short.class;
        } else if (int.class.equals(toCheck)) {
            return Integer.class;
        } else if (long.class.equals(toCheck)) {
            return Long.class;
        } else if (float.class.equals(toCheck)) {
            return Float.class;
        } else if (double.class.equals(toCheck)) {
            return Double.class;
        } else if (boolean.class.equals(toCheck)) {
            return Boolean.class;
        } else if (char.class.equals(toCheck)) {
            return Character.class;
        } else if (void.class.equals(toCheck)) {
            return Void.class;
        }

        return null;
    }

    public static Class<?> convertWrapperToPrimitiveClass(Class<?> toCheck) {
        if (Byte.class.equals(toCheck)) {
            return byte.class;
        } else if (Short.class.equals(toCheck)) {
            return short.class;
        } else if (Integer.class.equals(toCheck)) {
            return int.class;
        } else if (Long.class.equals(toCheck)) {
            return long.class;
        } else if (Float.class.equals(toCheck)) {
            return float.class;
        } else if (Double.class.equals(toCheck)) {
            return double.class;
        } else if (Boolean.class.equals(toCheck)) {
            return boolean.class;
        } else if (Character.class.equals(toCheck)) {
            return char.class;
        } else if (Void.class.equals(toCheck)) {
            return void.class;
        }
        return null;
    }
    
    public static Collection<Object> convertToCollection(Object obj) {
    	return convertToCollection(obj, Object.class);
    }

    public static <T> Collection<T> convertToCollection(Object obj, Class<T> clazz) {
        return convertToStream(obj, clazz).toList();
    }

    public static <T> Stream<T> emptyIfNull(Stream<T> stream) {
    	if (stream == null) {
    		return Stream.empty();
    	}
    	return stream;
    }
    
    public static Stream<Object> convertToStream(Object obj) {
        return emptyIfNull(convertToStreamOrNull(obj, Object.class));
    }
    
    public static Stream<Object> convertToStreamOrNull(Object obj) {
        return convertToStreamOrNull(obj, Object.class);
    }

    public static <T> Stream<T> convertToStream(Object obj, Class<T> clazz) {
        if (obj == null) {
            return Stream.empty();
        }
        Stream<T> stream = convertToStreamOrNull(obj, clazz);
        if (stream != null) {
            return stream;
        }
    	return clazz.isInstance(obj) ? Stream.of(clazz.cast(obj)) : Stream.empty();
    }
    
    public static <T> Stream<T> convertToStreamOrNull(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        
        Supplier<Stream<T>> supplier;

        if (obj instanceof Stream<?> asStream) {
            supplier = () -> asStream.filter(clazz::isInstance).map(clazz::cast);
        } else if (obj instanceof Collection<?>) {
            supplier = () -> ((Collection<?>) obj).stream().filter(clazz::isInstance).map(clazz::cast);
        } else if (obj instanceof Map<?, ?>) {
            supplier = () -> ((Map<?, ?>) obj).values().stream().filter(clazz::isInstance).map(clazz::cast);
        } else if (obj.getClass().isArray()) {
             return convertArrayToStreamOrNull(obj, clazz);
        } else {
            return null;
        }

        Iterator<T> iterator = supplier.get().iterator();
        if (!iterator.hasNext()) {
            return null;
        }

        T first = iterator.next();
        return Stream.concat(Stream.of(first), StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(iterator, 0), false));
    }

    public static <T> Stream<T> convertArrayToStream(T[] array) {
        return Arrays.stream(array);
    }

    public static <T> Stream<T> convertArrayToStream(Object array, Class<T> clazz) {
    	return emptyIfNull(convertArrayToStreamOrNull(array, clazz));
    }
    
    // We do check the types for safety before casting
    @SuppressWarnings("unchecked")
    public static <T> Stream<T> convertArrayToStreamOrNull(Object array, Class<T> clazz) {
        if (array == null || !array.getClass().isArray()) {
            return null;
        }
        // Ensure the types match
        Class<?> primType = array.getClass().getComponentType();
        if (!clazz.isAssignableFrom(primType)) {
            return null;
        }
        if (array.getClass().getComponentType().isPrimitive()) {
            return convertPrimitiveArrayToStreamOrNull(array, clazz);
        } else {
            // We already ensured its a matching array. We can safely cast
        	T[] asArray = (T[]) array;
        	if (asArray.length < 1) {
        		return null;
        	}
            return Arrays.stream(asArray);
        }
    }

    public static <T> Stream<T> convertPrimitiveArrayToStream(Object primativeArray,
            Class<T> clazz) {
    	return emptyIfNull(convertPrimitiveArrayToStreamOrNull(primativeArray, clazz));
    }
    
    // Suppress unchecked warning - we do check first
    @SuppressWarnings("unchecked")
    public static <T> Stream<T> convertPrimitiveArrayToStreamOrNull(Object primativeArray,
            Class<T> clazz) {
        // Ensure its an array
        if (primativeArray == null || !primativeArray.getClass().isArray()) {
            return null;
        }
        // Ensure the types match
        Class<?> primType = primativeArray.getClass().getComponentType();
        if (!clazz.isAssignableFrom(primType)) {
            return null;
        }
        // We confirmed its an array and T matches. We can now safely cast both to a
        // primitive array and as a Stream<T>
        if (primType == byte.class) {
            return (Stream<T>) convertPrimitiveArrayToStreamOrNull((byte[]) primativeArray);
        } else if (primType == short.class) {
            return (Stream<T>) convertPrimitiveArrayToStreamOrNull((short[]) primativeArray);
        } else if (primType == int.class) {
            return (Stream<T>) convertPrimitiveArrayToStreamOrNull((int[]) primativeArray);
        } else if (primType == long.class) {
            return (Stream<T>) convertPrimitiveArrayToStreamOrNull((long[]) primativeArray);
        } else if (primType == float.class) {
            return (Stream<T>) convertPrimitiveArrayToStreamOrNull((float[]) primativeArray);
        } else if (primType == double.class) {
            return (Stream<T>) convertPrimitiveArrayToStreamOrNull((double[]) primativeArray);
        } else if (primType == boolean.class) {
            return (Stream<T>) convertPrimitiveArrayToStreamOrNull((boolean[]) primativeArray);
        } else if (primType == char.class) {
            return (Stream<T>) convertPrimitiveArrayToStreamOrNull((char[]) primativeArray);
        }
        return null;
    }

    public static Stream<Byte> convertPrimitiveArrayToStream(byte... primitiveArray) {
    	return emptyIfNull(convertPrimitiveArrayToStreamOrNull(primitiveArray));
    }
    
    public static Stream<Byte> convertPrimitiveArrayToStreamOrNull(byte... primitiveArray) {
    	if (primitiveArray.length < 1) {
    		return null;
    	}
        return IntStream.range(0, primitiveArray.length).mapToObj(idx -> primitiveArray[idx]);
    }


    public static Stream<Short> convertPrimitiveArrayToStream(short... primitiveArray) {
    	return emptyIfNull(convertPrimitiveArrayToStreamOrNull(primitiveArray));
    }
    
    public static Stream<Short> convertPrimitiveArrayToStreamOrNull(short... primitiveArray) {
    	if (primitiveArray.length < 1) {
    		return null;
    	}
        return IntStream.range(0, primitiveArray.length).mapToObj(idx -> primitiveArray[idx]);
    }


    public static Stream<Integer> convertPrimitiveArrayToStream(int... primitiveArray) {
    	return emptyIfNull(convertPrimitiveArrayToStreamOrNull(primitiveArray));
    }
    public static Stream<Integer> convertPrimitiveArrayToStreamOrNull(int... primitiveArray) {
    	if (primitiveArray.length < 1) {
    		return null;
    	}
        return Arrays.stream(primitiveArray).boxed();
    }


    public static Stream<Long> convertPrimitiveArrayToStream(long... primitiveArray) {
    	return emptyIfNull(convertPrimitiveArrayToStreamOrNull(primitiveArray));
    }
    public static Stream<Long> convertPrimitiveArrayToStreamOrNull(long... primitiveArray) {
    	if (primitiveArray.length < 1) {
    		return null;
    	}
        return IntStream.range(0, primitiveArray.length).mapToObj(idx -> primitiveArray[idx]);
    }


    public static Stream<Float> convertPrimitiveArrayToStream(float... primitiveArray) {
    	return emptyIfNull(convertPrimitiveArrayToStreamOrNull(primitiveArray));
    }
    public static Stream<Float> convertPrimitiveArrayToStreamOrNull(float... primitiveArray) {
    	if (primitiveArray.length < 1) {
    		return null;
    	}
        return IntStream.range(0, primitiveArray.length).mapToObj(idx -> primitiveArray[idx]);
    }


    public static Stream<Double> convertPrimitiveArrayToStream(double... primitiveArray) {
    	return emptyIfNull(convertPrimitiveArrayToStreamOrNull(primitiveArray));
    }
    public static Stream<Double> convertPrimitiveArrayToStreamOrNull(double... primitiveArray) {
    	if (primitiveArray.length < 1) {
    		return null;
    	}
        return Arrays.stream(primitiveArray).boxed();
    }


    public static Stream<Boolean> convertPrimitiveArrayToStream(boolean... primitiveArray) {
    	return emptyIfNull(convertPrimitiveArrayToStreamOrNull(primitiveArray));
    }
    public static Stream<Boolean> convertPrimitiveArrayToStreamOrNull(boolean... primitiveArray) {
    	if (primitiveArray.length < 1) {
    		return null;
    	}
        return IntStream.range(0, primitiveArray.length).mapToObj(idx -> primitiveArray[idx]);
    }


    public static Stream<Character> convertPrimitiveArrayToStream(char... primitiveArray) {
    	return emptyIfNull(convertPrimitiveArrayToStreamOrNull(primitiveArray));
    }
    
    public static Stream<Character> convertPrimitiveArrayToStreamOrNull(char... primitiveArray) {
    	if (primitiveArray.length < 1) {
    		return null;
    	}
        return IntStream.range(0, primitiveArray.length).mapToObj(idx -> primitiveArray[idx]);
    }
}
