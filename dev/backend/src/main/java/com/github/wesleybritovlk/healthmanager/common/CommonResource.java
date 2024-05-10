package com.github.wesleybritovlk.healthmanager.common;

import java.util.Map;
import java.util.TreeMap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonResource {
    private static final String CONTENT = "content";

    public static Map<Object, Object> toResource(Object content) {
        var resource = new TreeMap<>();
        resource.put(CONTENT, content);
        return resource;
    }

    public static Map<Object, Object> toResource(String message, Object content) {
        var resource = new TreeMap<>((key0, key1) -> ((String) key1).compareTo((String) key0));
        resource.put("message", message);
        resource.put(CONTENT, content);
        return resource;
    }
}
