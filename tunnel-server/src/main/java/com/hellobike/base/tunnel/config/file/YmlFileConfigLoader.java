package com.hellobike.base.tunnel.config.file;

import com.alibaba.fastjson.JSON;
import com.hellobike.base.tunnel.config.YmlConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class YmlFileConfigLoader extends FileConfigLoader {

    public YmlFileConfigLoader(String fileName) {
        super(fileName);
    }

    @Override
    protected Map<String, String> load(File file) {
        HashMap<String, String> map = new HashMap<>();
        try {
            Yaml yaml = new Yaml();
            YmlConfig ymlConfig = yaml.loadAs(new FileInputStream(file), YmlConfig.class);
            map.put("tunnel_subscribe_config", JSON.toJSONString(ymlConfig.getTunnel_subscribe_config()));
            map.put("tunnel_zookeeper_address", ymlConfig.getTunnel_zookeeper_address());
        } catch (FileNotFoundException e) {
            logger.warn("config file {} not found", file);
        } catch (RuntimeException e) {
            logger.warn("parse config error: ", e);
        }
        return map;
    }

}
