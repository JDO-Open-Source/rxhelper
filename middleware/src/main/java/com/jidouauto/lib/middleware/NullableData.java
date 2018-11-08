package com.jidouauto.lib.middleware;

public final class NullableData<T> {

    private final T value;

    private NullableData(T value) {
        this.value = value;
    }


    public static <T> NullableData<T> of(T value) {
        return new NullableData<>(value);
    }

    public T get() {
        if (value == null) {
            throw new NullPointerException("Null data");
        }
        return value;
    }


    public boolean isNull() {
        return value == null;
    }

    public boolean isNotNull() {
        return value != null;
    }

    @Override
    public String toString() {
        return value != null
                ? String.format("NullableData[%s]", value)
                : "NullableData.empty";
    }
}
