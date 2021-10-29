package com.xxs.igcsandroid.entity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class HistoryDataHolder {
    Map<String, WeakReference<Object>> data = new HashMap<>();
    private static HistoryDataHolder client = null;

    public static HistoryDataHolder getInstance() {
        if (client == null) {
            client = new HistoryDataHolder();
        }
        return client;
    }

    public void save(String id, Object object) {
        data.put(id, new WeakReference<Object>(object));
    }

    public Object retrieve(String id) {
        WeakReference<Object> objectWeakReference = data.get(id);
        return objectWeakReference.get();
    }
}
