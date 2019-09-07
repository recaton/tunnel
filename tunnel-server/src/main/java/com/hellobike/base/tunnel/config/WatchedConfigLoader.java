package com.hellobike.base.tunnel.config;

import com.hellobike.base.tunnel.utils.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WatchedConfigLoader<T> implements ConfigLoader, AutoCloseable {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Map<String, String> properties = new ConcurrentHashMap<>();
    protected List<ConfigListener> listeners = new CopyOnWriteArrayList<>();
    protected AtomicBoolean started = new AtomicBoolean(Boolean.FALSE);
    private ExecutorService executor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new NamedThreadFactory("WatcherThread"));
    protected T watchKey;

    public WatchedConfigLoader(T watchKey) {
        this.watchKey = watchKey;
        this.properties.putAll(load(watchKey));
        addWatcher();
    }

    protected void addWatcher() {
        Runnable watchTask = watcherTask();
        this.started.compareAndSet(Boolean.FALSE, Boolean.TRUE);
        this.executor.submit(watchTask);
    }

    protected abstract Map<String, String> load(T watchKey);

    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    @Override
    public void addChangeListener(ConfigListener configListener) {
        if (!this.listeners.contains(configListener)) {
            this.listeners.add(configListener);
        }
    }

    @Override
    public void close() {
        this.started.compareAndSet(Boolean.TRUE, Boolean.FALSE);
        this.executor.shutdown();
    }

    protected static class ThreeTuple<O1, O2, O3> {

        protected O1 k1;
        protected O2 v1;
        protected O3 v2;

        protected ThreeTuple(O1 k1, O2 v1, O3 v2) {
            this.k1 = k1;
            this.v1 = v1;
            this.v2 = v2;
        }

        protected boolean valueEquals() {
            if (v1 == null) {
                return v2 == null;
            }
            return v1.equals(v2);
        }
    }

    protected final synchronized void mergeData(Map<String, String> oldProp, Map<String, String> newProp, List<ThreeTuple<String, String, String>> data) {
        Map<String, ThreeTuple<String, String, String>> tmp = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : oldProp.entrySet()) {
            String key = e.getKey();
            String oldVal = e.getValue();
            String newVal = newProp.get(key);

            if (newVal == null) {
                oldProp.remove(key);
            }

            tmp.put(key, new ThreeTuple<>(key, oldVal, newVal));
        }

        for (Map.Entry<String, String> e : newProp.entrySet()) {
            String key = e.getKey();
            String newVal = e.getValue();

            String oldVal = oldProp.putIfAbsent(key, newVal);

            tmp.putIfAbsent(key, new ThreeTuple<>(key, oldVal, newVal));
        }

        data.addAll(new ArrayList<>(tmp.values()));
        oldProp.clear();
        oldProp.putAll(newProp);
    }

    protected final void onDataChange(List<ThreeTuple<String, String, String>> data) {
        for (ThreeTuple<String, String, String> tuple : data) {
            for (ConfigListener listener : listeners) {
                if (!tuple.valueEquals()) {
                    listener.onChange(tuple.k1, tuple.v1, tuple.v2);
                }
            }
        }
    }

    protected abstract Runnable watcherTask();
}
