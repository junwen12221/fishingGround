package com.pawandubey.griffin;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.SetUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by karak on 16-8-26.
 */
public class Util {
   public final static <T> List<T> toDefaultList(final List<T> value){
       return value==null? ListUtils.EMPTY_LIST:value;
    }
    public final static <K,V> Map<K,V> toDefaultMap(final Map<K,V> value){
        return value==null? MapUtils.EMPTY_MAP :value;
    }
    public final static <T> Set<T> toDefaultSet(final Set<T> value){
        return value==null? SetUtils.EMPTY_SET:value;
    }
    public final static String toDefaultString(final String value){
        return value==null? "":value;
    }
}
