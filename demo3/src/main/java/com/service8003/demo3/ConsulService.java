package com.service8003.demo3;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ServiceEntry;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

@Service
public class ConsulService {

    private final ConsulClient consulClient;

    @Autowired
    public ConsulService(@Qualifier("customConsulClient") ConsulClient consulClient) {
        this.consulClient = consulClient;
    }

    public void callService(String serviceName) {
        consulClient.healthServiceNodes(serviceName, true, result -> {
            if (result.succeeded()) {
                List<ServiceEntry> services = result.result().getList();
                if (!services.isEmpty()) {
                    String address = services.get(0).getService().getAddress();
                    int port = services.get(0).getService().getPort();
                    System.out.println("Found service at: " + address + ":" + port);
                } else {
                    System.out.println("No healthy services found for: " + serviceName);
                }
            } else {
                System.err.println("Failed to get services: " + result.cause());
            }
        });
    }

    public void callServiceEndpoint(String serviceName, String endpoint, Handler<AsyncResult<String>> resultHandler) {
        consulClient.healthServiceNodes(serviceName, true, result -> {
            if (result.succeeded()) {
                List<ServiceEntry> services = result.result().getList();
                if (!services.isEmpty()) {
                    String address = services.get(0).getService().getAddress();
                    int port = services.get(0).getService().getPort();
                    String url = "http://" + address + ":" + port + endpoint;

                    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                        HttpGet request = new HttpGet(url);
                        HttpResponse response = httpClient.execute(request);
                        String responseBody = EntityUtils.toString(response.getEntity());
                        resultHandler.handle(Future.succeededFuture(responseBody));
                    } catch (IOException e) {
                        resultHandler.handle(Future.failedFuture(e));
                    }
                } else {
                    resultHandler.handle(Future.failedFuture("No services found for: " + serviceName));
                }
            } else {
                resultHandler.handle(Future.failedFuture("Failed to get services: " + result.cause()));
            }
        });
    }

}
