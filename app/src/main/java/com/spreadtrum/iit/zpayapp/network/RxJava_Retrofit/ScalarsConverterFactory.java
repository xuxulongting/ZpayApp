package com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit;

/**
 * Created by SPREADTRUM\ting.long on 16-10-11.
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit.ScalarResponseBodyConverters.StringResponseBodyConverter;
import com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit.ScalarResponseBodyConverters.BooleanResponseBodyConverter;
import com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit.ScalarResponseBodyConverters.ByteResponseBodyConverter;
import com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit.ScalarResponseBodyConverters.CharacterResponseBodyConverter;
import com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit.ScalarResponseBodyConverters.DoubleResponseBodyConverter;
import com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit.ScalarResponseBodyConverters.FloatResponseBodyConverter;
import com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit.ScalarResponseBodyConverters.IntegerResponseBodyConverter;
import com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit.ScalarResponseBodyConverters.LongResponseBodyConverter;
import com.spreadtrum.iit.zpayapp.network.RxJava_Retrofit.ScalarResponseBodyConverters.ShortResponseBodyConverter;

/**
 * A {@linkplain Converter.Factory converter} for strings and both primitives and their boxed types
 * to {@code text/plain} bodies.
 */
public final class ScalarsConverterFactory extends Converter.Factory {
    public static ScalarsConverterFactory create() {
        return new ScalarsConverterFactory();
    }

    private ScalarsConverterFactory() {
    }

    @Override public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                                    Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (type == String.class
                || type == boolean.class
                || type == Boolean.class
                || type == byte.class
                || type == Byte.class
                || type == char.class
                || type == Character.class
                || type == double.class
                || type == Double.class
                || type == float.class
                || type == Float.class
                || type == int.class
                || type == Integer.class
                || type == long.class
                || type == Long.class
                || type == short.class
                || type == Short.class) {
            return ScalarRequestBodyConverter.INSTANCE;
        }
        return null;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (type == String.class) {
            return StringResponseBodyConverter.INSTANCE;
        }
        if (type == Boolean.class || type == boolean.class) {
            return BooleanResponseBodyConverter.INSTANCE;
        }
        if (type == Byte.class || type == byte.class) {
            return ByteResponseBodyConverter.INSTANCE;
        }
        if (type == Character.class || type == char.class) {
            return CharacterResponseBodyConverter.INSTANCE;
        }
        if (type == Double.class || type == double.class) {
            return DoubleResponseBodyConverter.INSTANCE;
        }
        if (type == Float.class || type == float.class) {
            return FloatResponseBodyConverter.INSTANCE;
        }
        if (type == Integer.class || type == int.class) {
            return IntegerResponseBodyConverter.INSTANCE;
        }
        if (type == Long.class || type == long.class) {
            return LongResponseBodyConverter.INSTANCE;
        }
        if (type == Short.class || type == short.class) {
            return ShortResponseBodyConverter.INSTANCE;
        }
        return null;
    }
}