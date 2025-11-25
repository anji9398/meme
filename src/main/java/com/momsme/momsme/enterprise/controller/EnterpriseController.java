package com.momsme.momsme.enterprise.controller;

import com.momsme.momsme.common.MsmeResponse;
import com.momsme.momsme.enterprise.dto.EnterpriseDto;
import com.momsme.momsme.enterprise.service.EnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(name = "/enterprise")
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    @PostMapping(path = "/save")
    public MsmeResponse<EnterpriseDto> save(@RequestBody EnterpriseDto request) {
      return enterpriseService.saveEnterprise(request);
    }

    @GetMapping(path = "/get-all")
    public MsmeResponse<List<EnterpriseDto>> getAllEnterprises() {
        return enterpriseService.getAllEnterprises();
    }

    @GetMapping(path = "/get-by/{enterprisesId}")
    public MsmeResponse<?> getByEnterprisesId(@PathVariable Long enterprisesId) {
        return enterpriseService.getByEnterprisesId(enterprisesId);
    }

    @DeleteMapping(path = "/delete/{enterprisesId}")
    public MsmeResponse<?> deleteByEnterprisesId(@PathVariable Long enterprisesId) {
        return enterpriseService.deleteByEnterprisesId(enterprisesId);
    }

}
