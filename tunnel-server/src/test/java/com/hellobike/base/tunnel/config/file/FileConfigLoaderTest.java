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
package com.hellobike.base.tunnel.config.file;

import com.hellobike.base.tunnel.config.ConfigLoaderFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

/**
 * @author machunxiao create at 2018-12-26
 */
public class FileConfigLoaderTest {

    private static final Logger log = LoggerFactory.getLogger(FileConfigLoaderTest.class);

    @Test
    public void testPropertiesFileConfigLoader() {
        String file = "test_config.properties";
        testFileConfigLoader(ConfigLoaderFactory.getFileConfigLoader(getFilePath(file)));
    }

    @Test
    public void testYmlFileConfigLoader() {
        String file = "cfg.yml";
        testFileConfigLoader(ConfigLoaderFactory.getFileConfigLoader(getFilePath(file)));
    }

    private String getFilePath(String file) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(file);
        if (url == null) {
            url = FileConfigLoaderTest.class.getClassLoader().getResource(file);
        }
        if (url == null) {
            File f = Paths.get(file).toFile();
            if (f.exists()) {
                return f.getPath();
            }
        } else {
            return url.getPath();
        }
        return null;
    }

    private void testFileConfigLoader(FileConfigLoader loader){
        loader.addChangeListener((key, oldValue, newValue) -> log.info("key:{},old:{},new:{}", key, oldValue, newValue));
        try {
            Thread.currentThread().join(1000 * 60 * 10);
        } catch (InterruptedException e) {
            //
        }
        loader.close();
    }

}
