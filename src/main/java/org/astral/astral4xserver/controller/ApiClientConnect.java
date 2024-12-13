package org.astral.astral4xserver.controller;

import org.astral.astral4xserver.message.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/client")
public class ApiClientConnect {
    @PostMapping("/connect")
    public ApiResponse connect() {
        ApiResponse apiResponse = new ApiResponse(200, "success");
        return apiResponse;
    }
}
