/*
 * Copyright 2018 Shanghai Junzheng Network Technology Co.,Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import com.hellobike.base.tunnel.config.WatchedConfigLoader;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author machunxiao create at 2018-12-26
 */
public abstract class FileConfigLoader extends WatchedConfigLoader<File> {

    public FileConfigLoader(String fileName) {
        super(lookupConfigFile(fileName));
    }

    private static File lookupConfigFile(String fileName) {
        File file;
        if (!(file = new File(fileName)).isFile() &&
                !(file = new File(System.getProperty("user.dir") + "/conf/" + fileName)).isFile()) {
            file = new File(ClassLoader.class.getResource("/conf/").getPath() + fileName);
        }
        return file;
    }


    @Override
    protected Runnable watcherTask() {
        return () -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                Path dir = Paths.get(watchKey.getParent());
                dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                while (started.get()) {
                    try {
                        WatchKey key = watchService.poll(50, TimeUnit.MILLISECONDS);
                        if (key == null) {
                            continue;
                        }

                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();
                            List<ThreeTuple<String, String, String>> data = new ArrayList<>();
                            Path path = (Path) event.context();
                            if (!path.toFile().getName().equals(watchKey.getName())) {
                                continue;
                            }
                            if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                Map<String, String> newData = load(watchKey);
                                mergeData(properties, newData, data);
                            }

                            onDataChange(data);
                        }
                        key.reset();

                    } catch (Throwable e) {
                        //
                    }
                }

            } catch (Throwable t) {
                //
            }
        };
    }

}
