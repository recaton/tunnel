/*
 * Copyright 2018 Shanghai Junzheng Network Technology Co.,Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain CONFIG_NAME copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hellobike.base.tunnel.config;

import com.hellobike.base.tunnel.apollo.ApolloConfigLoader;
import com.hellobike.base.tunnel.config.db.DatabaseConfigLoader;
import com.hellobike.base.tunnel.config.file.FileConfigLoader;
import com.hellobike.base.tunnel.config.file.PropertiesFileConfigLoader;
import com.hellobike.base.tunnel.config.file.YmlFileConfigLoader;

/**
 * @author machunxiao create at 2018-12-27
 */
public class ConfigLoaderFactory {

    private ConfigLoaderFactory() {
    }

    public static ConfigLoader getConfigLoader(TunnelConfig tunnelConfig) {
        switch (tunnelConfig.getConfigType()) {
            case DATABASE: return new DatabaseConfigLoader(tunnelConfig.getConfigSource());
            case APOLLO: return new ApolloConfigLoader(tunnelConfig.getAppId(), tunnelConfig.getMetaDomain());
            default: return getFileConfigLoader(tunnelConfig.getConfigSource());
        }
    }

    public static FileConfigLoader getFileConfigLoader(String fileName){
        String ext = "";
        try {
            ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        } catch (StringIndexOutOfBoundsException e) {
            // do nothing
        }

        switch (ext){
            case "yml":
            case "yaml": return new YmlFileConfigLoader(fileName);
            default: return new PropertiesFileConfigLoader(fileName);
        }
    }

}
