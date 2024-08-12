package com.demo.core.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class JsonUtils {

    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Integer.class, new IntDefaultAdapter())
            .registerTypeAdapter(int.class, new IntDefaultAdapter())
            .registerTypeAdapter(Short.class, new ShortDefaultAdapter())
            .registerTypeAdapter(short.class, new ShortDefaultAdapter())
            .registerTypeAdapter(Float.class, new FloatDefaultAdapter())
            .registerTypeAdapter(float.class, new FloatDefaultAdapter())
            .registerTypeAdapter(Double.class, new DoubleDefaultAdapter())
            .registerTypeAdapter(double.class, new DoubleDefaultAdapter())
            .registerTypeAdapter(Long.class, new LongDefaultAdapter())
            .registerTypeAdapter(long.class, new LongDefaultAdapter())
            .create();

    public static String toJsonStr(Object object) {
        switch (object.getClass().getName()) {
            case "java.lang.String":
            case "java.lang.Character":
                return object.toString();
            default:
                return gson.toJson(object);
        }
    }

    public static <T> T fromJson(String str, Class<T> clazz) {
        switch (clazz.getName()) {
            case "java.lang.String":
            case "java.lang.Character":
                return (T) str;
        }
        if (str != null && str.trim().length() > 0) {
            return gson.fromJson(str, clazz);
        }
        return null;
    }

    public static <T> T fromJson(String str, Type type) {
        if (str != null && str.trim().length() > 0) {
            return gson.fromJson(str, type);
        }
        return null;
    }

    public static <T> T fromJson(JsonElement obj, Type type) {
        if (obj != null && !obj.isJsonNull()) {
            return gson.fromJson(obj, type);
        }
        return null;
    }

    public static JsonObject fromJson(Object o) {
        if (o != null) {
            return gson.fromJson(gson.toJson(o), JsonObject.class);
        }
        return new JsonObject();
    }

    public static <T> List<T> toList(String str) {
        if (str != null && str.trim().length() > 0) {
            return gson.fromJson(str, new TypeToken<List<T>>() {
            }.getType());
        }
        return null;
    }

    public static <T> List<T> toList(String str, Type type) {
        if (str != null && str.trim().length() > 0) {
            return gson.fromJson(str, type);
        }
        return null;
    }

    public static <T> List<T> toObjList(List<String> list, Class<T> clazz) {
        List<T> re = new ArrayList<>(list.size());
        list.forEach(i -> re.add(gson.fromJson(i, clazz)));
        return re;
    }

    public static List<String> toStrList(List<Object> list) {
        List<String> re = new ArrayList<>(list.size());
        list.forEach(i -> re.add(gson.toJson(i)));
        return re;
    }

    public static <T> Set<T> toObjSet(Set<String> list, Class<T> clazz) {
        Set<T> re = new HashSet<>(list.size());
        list.forEach(i -> re.add(gson.fromJson(i, clazz)));
        return re;
    }


    public static Set<String> toStrSet(Set<Object> list) {
        Set<String> re = new HashSet<>(list.size());
        list.forEach(i -> re.add(gson.toJson(i)));
        return re;
    }


    public static <K, V> Map<K, V> toObjMap(Map<String, String> map, Class<K> k, Class<V> v) {
        Map<K, V> reMap = new HashMap<>();
        map.keySet().forEach(i -> reMap.put(fromJson(i, k), fromJson(map.get(i), v)));
        return reMap;
    }


    public static <K, V> Map<K, V> toObjSafeMap(Map<String, String> map, Class<K> k, Class<V> v) {
        Map<K, V> reMap = new ConcurrentHashMap<>();
        map.keySet().forEach(i -> reMap.put(fromJson(i, k), fromJson(map.get(i), v)));
        return reMap;
    }


    static class DoubleDefaultAdapter implements JsonSerializer<Double>, JsonDeserializer<Double> {

        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                String str = json.getAsString();
                if (str.equals("") || str.equals("null")) {
                    return null;
                }
            } catch (Exception e) {
            }
            try {
                return json.getAsDouble();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }

    static class FloatDefaultAdapter implements JsonSerializer<Float>, JsonDeserializer<Float> {

        @Override
        public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                String str = json.getAsString();
                if (str.equals("") || str.equals("null")) {
                    return null;
                }
            } catch (Exception e) {
            }
            try {
                return json.getAsFloat();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Float src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }

    static class IntDefaultAdapter implements JsonSerializer<Integer>, JsonDeserializer<Integer> {

        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                String str = json.getAsString();
                if (str.equals("") || str.equals("null")) {
                    return null;
                }
            } catch (Exception e) {
            }
            try {
                return json.getAsInt();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }

    static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
    }

    static class LongDefaultAdapter implements JsonSerializer<Long>, JsonDeserializer<Long> {

        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                String str = json.getAsString();
                if (str.equals("") || str.equals("null")) {
                    return null;
                }
            } catch (Exception e) {
            }
            try {
                return json.getAsLong();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }

    static class ShortDefaultAdapter implements JsonSerializer<Short>, JsonDeserializer<Short> {

        @Override
        public Short deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                String str = json.getAsString();
                if (str.equals("") || str.equals("null")) {
                    return null;
                }
            } catch (Exception e) {
            }
            try {
                return json.getAsShort();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Short src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }
}
