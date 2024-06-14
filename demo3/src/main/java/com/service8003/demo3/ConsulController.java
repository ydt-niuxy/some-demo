package com.service8003.demo3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsulController {

    private final ConsulService consulService;

    @Autowired
    public ConsulController(ConsulService consulService) {
        this.consulService = consulService;
    }

    @GetMapping("/callService")
    public String callService(@RequestParam String serviceName) {
        consulService.callService(serviceName);
        return "Service called: " + serviceName;
    }

    @GetMapping("/getEndpoint")
    public String getEndpoint(@RequestParam String serviceName, @RequestParam String endpoint) {
        consulService.callServiceEndpoint(serviceName, endpoint, result -> {
            if (result.succeeded()) {
                System.out.println("Service response: " + result.result());
            } else {
                System.err.println("Failed to call service: " + result.cause());
            }
        });
        return serviceName;
    }
}

