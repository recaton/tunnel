package com.hellobike.base.tunnel.config.db;

import com.alibaba.fastjson.JSON;
import com.hellobike.base.tunnel.config.WatchedConfigLoader;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConfigLoader extends WatchedConfigLoader<String> {

    public DatabaseConfigLoader(String watchKey) {
        super(watchKey);
    }

    @Override
    protected Map<String, String> load(String watchKey) {
        return load(watchKey, true);
    }

    private Map<String, String> load(String watchKey, boolean first) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(watchKey);
        CloseableHttpResponse response = null;
        do {
            try {
                response = client.execute(httpget);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String str;
                    while ((str = reader.readLine()) != null) {
                        Map<String, String> map = JSON.parseObject(str, Map.class);
                        return map;
                    }
                }
            } catch (Exception e) {
                logger.error("get config error.", e);
                if(first) {
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException ex) {
                        //
                    }
                }
            }finally {
                if(response != null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                        //
                    }
                }
            }
        } while (first);

        return new HashMap<>();
    }

    @Override
    protected Runnable watcherTask() {
        return () -> {
            while (true) {
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    //
                }
                List<ThreeTuple<String, String, String>> data = new ArrayList<>();
                mergeData(properties, load(watchKey, false), data);
                onDataChange(data);
            }
        };
    }
}
