package com.sgqrplus.switchengine.controller;

import com.sgqrplus.switchengine.dto.SchemeResponse;
import com.sgqrplus.switchengine.repository.SchemeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schemes")
public class SchemeController {

    private final SchemeRepository schemeRepository;

    public SchemeController(SchemeRepository schemeRepository) {
        this.schemeRepository = schemeRepository;
    }

    @GetMapping
    public List<SchemeResponse> listSchemes() {
        return schemeRepository.findAll().stream()
                .map(SchemeResponse::from)
                .toList();
    }
}
