package com.wuest.from_the_depths.entityinfo.restrictions;

import com.google.gson.annotations.SerializedName;

public class DataAndComparator<D extends Comparable<D>> {
    public D data;
    public Operator operator;

    public DataAndComparator()
    {
        data = null;
        operator = null;
    }

    public DataAndComparator(D data, Operator operator)
    {
        this.data = data;
        this.operator = operator;
    }

    /**
     * operator > -> true when in-game value is higher than configured value
     * operator < -> true when in-game value is lower than configured value
     */
    public enum Operator {

        @SerializedName(value = "EQUALS", alternate = {"equals", "Equals", "="})
        EQUALS,
        @SerializedName(value = "LESS", alternate = {"less", "Less", "<"})
        LESS,
        @SerializedName(value = "MORE", alternate = {"more", "More", "Greater", "GREATER", "greater", ">"})
        MORE;

        public <T extends Comparable<T>> boolean test(T data, T data2) {
            switch (this) {
                case EQUALS:
                    return data == data2;
                case LESS:
                    return data.compareTo(data2) < 0;
                case MORE:
                    return data.compareTo(data2) > 0;
            }

            return false;
        }
    }

    @Override
    public String toString()
    {
        return "DataAndComparator{" +
                "data=" + data +
                ", operator=" + operator +
                '}';
    }
}
