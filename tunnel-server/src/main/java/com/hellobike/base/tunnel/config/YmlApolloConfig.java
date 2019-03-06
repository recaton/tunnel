package com.hellobike.base.tunnel.config;

import com.hellobike.base.tunnel.apollo.ApolloConfig;

public class YmlApolloConfig extends ApolloConfig {

    private String pg_dump_path;

    public String getPg_dump_path() {
        return pg_dump_path;
    }

    public void setPg_dump_path(String pg_dump_path) {
        this.setPgDumpPath(pg_dump_path);
        this.pg_dump_path = pg_dump_path;
    }

    public static class YmlHiveConf extends HiveConf{
        private String hdfs_address;
        private String data_dir;
        private String table_name;

        public String getHdfs_address() {
            return hdfs_address;
        }

        public void setHdfs_address(String hdfs_address) {
            this.setHdfsAddress(hdfs_address);
            this.hdfs_address = hdfs_address;
        }

        public String getData_dir() {
            return data_dir;
        }

        public void setData_dir(String data_dir) {
            this.setDataDir(data_dir);
            this.data_dir = data_dir;
        }

        public String getTable_name() {
            return table_name;
        }

        public void setTable_name(String table_name) {
            this.setTableName(table_name);
            this.table_name = table_name;
        }
    }

}
