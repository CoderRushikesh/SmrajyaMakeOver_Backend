package com.Samrajya.MakeOver.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // Public endpoint for uptime pings (UptimeRobot / Render health check)
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
