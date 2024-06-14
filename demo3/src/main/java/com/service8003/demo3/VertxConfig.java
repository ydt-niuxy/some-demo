package com.service8003.demo3;

import io.vertx.core.Vertx;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ConsulClientOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertxConfig {

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    @Bean(name = "customConsulClient")
    public ConsulClient consulClient(Vertx vertx) {
        ConsulClientOptions options = new ConsulClientOptions()
                .setHost("localhost")  // 设置Consul服务器的地址
                .setPort(8500);        // 设置Consul服务器的端口
        return ConsulClient.create(vertx, options);
    }
}

