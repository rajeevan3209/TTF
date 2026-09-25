package com.sgqrplus.switchengine.controller;

import com.sgqrplus.switchengine.dto.FeeConfigResponse;
import com.sgqrplus.switchengine.repository.FeeConfigRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fee-config")
public class FeeConfigController {

    private final FeeConfigRepository feeConfigRepository;

    public FeeConfigController(FeeConfigRepository feeConfigRepository) {
        this.feeConfigRepository = feeConfigRepository;
    }

    @GetMapping
    public List<FeeConfigResponse> listFeeConfig() {
        return feeConfigRepository.findAll().stream()
                .map(FeeConfigResponse::from)
                .toList();
    }
}
