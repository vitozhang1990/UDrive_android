package cn.com.i_zj.udrive_az.network.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * @author JayQiu
 * @create 2018/10/26
 * @Describe
 */
public class NullStringToEmptyAdapterFactory <T> implements TypeAdapterFactory {
    @SuppressWarnings({ "unchecked", "hiding" })
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = (Class<T>) type.getRawType();
        if (rawType != String.class) {
            return null;
        }
        return (TypeAdapter<T>) new StringNullAdapter();
    }
}

