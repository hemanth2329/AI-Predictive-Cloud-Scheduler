package com.eco.cloud_dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CloudController {

    @Autowired
    private EcoSchedulingService ecoService;

    @GetMapping("/api/dispatch")
    public String dispatchTasks(@RequestParam(defaultValue = "12") int hourOfDay) {

        return ecoService.runIntelligentSimulation(hourOfDay);
    }
}