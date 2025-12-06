package com.momsme.momsme.msmedata.controller;

import com.momsme.momsme.model.MsmeDataPoints;
import com.momsme.momsme.msmedata.dto.MsmeDataPointsDto;
import com.momsme.momsme.msmedata.service.MsmeDataPointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(name = "/msme-data")
public class MsmeDataPointsController {

    private final MsmeDataPointsService service;

    @PostMapping("/msme/save")
    public MsmeDataPoints save(@RequestBody MsmeDataPointsDto req) {
        return service.save(req);
    }

    @GetMapping("/get-msme/data/{enterpriseId}")
    public List<MsmeDataPointsDto> getEnterpriseData(@PathVariable Long enterpriseId){

       return service.getEnterpriseData(enterpriseId);

    }

}
