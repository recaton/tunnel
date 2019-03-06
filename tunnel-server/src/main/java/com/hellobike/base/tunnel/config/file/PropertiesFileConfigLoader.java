package com.hellobike.base.tunnel.config.file;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesFileConfigLoader extends FileConfigLoader{

    public PropertiesFileConfigLoader(String fileName) {
        super(fileName);
    }

    @Override
    protected Map<String, String> load(File file) {
        Map<String, String> data = new LinkedHashMap<>();
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(file));
            for (Map.Entry<Object, Object> e : prop.entrySet()) {
                Object key = e.getKey();
                Object value = e.getValue();
                if (key != null) {
                    data.put((String) key, (String) value);
                }
            }
        } catch (Exception e) {

        }
        return data;
    }
}
