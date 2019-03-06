package com.hellobike.base.tunnel.config;

import lombok.Data;

@Data
public class YmlConfig {
    private YmlApolloConfig tunnel_subscribe_config;
    private String tunnel_zookeeper_address;
}
