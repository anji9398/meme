package com.momsme.momsme.enterprise.service;

import com.momsme.momsme.common.MsmeResponse;
import com.momsme.momsme.common.ResponseUtil;
import com.momsme.momsme.enterprise.dto.EnterpriseDto;
import com.momsme.momsme.enterprise.repo.EnterpriseRepository;
import com.momsme.momsme.model.Enterprise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;


    public MsmeResponse<EnterpriseDto> saveEnterprise(EnterpriseDto request) {
        Enterprise e = new Enterprise();
        StringBuilder msg = new StringBuilder();
        e.setEnterpriseName(request.getEnterpriseName());
        Optional<Enterprise> byId = enterpriseRepository.findById(request.getEnterpriseId());
        if (byId.isPresent()) {
            e.setEnterpriseId(request.getEnterpriseId());
            msg.append("Enterprise Updated Successfully");
        } else{
            e.setEnterpriseId(null);
            msg.append("Enterprise Create Successfully");
        }
        Enterprise save = enterpriseRepository.save(e);
        request.setEnterpriseId(save.getEnterpriseId());
        request.setEnterpriseName(save.getEnterpriseName());
        return ResponseUtil.created(msg,request);
    }

    public MsmeResponse<List<EnterpriseDto>> getAllEnterprises() {

            List<EnterpriseDto> allEnterprise = enterpriseRepository.findAll().stream().map(e -> {
                        EnterpriseDto dto = new EnterpriseDto();
                        dto.setEnterpriseId(e.getEnterpriseId());
                        dto.setEnterpriseName(e.getEnterpriseName());
                        return dto;
                    }
            ).toList();
         return  ResponseUtil.success("Fetched all enterprises",allEnterprise);

    }


    public MsmeResponse<?> getByEnterprisesId(Long enterprisesId) {

        Optional<Enterprise> byId = enterpriseRepository.findById(enterprisesId);
        if (byId.isPresent()) {
            Enterprise enterprise = byId.get();
            return ResponseUtil.success("Successfully fetched with id : "+enterprisesId,EnterpriseDto.builder()
                    .enterpriseId(enterprise.getEnterpriseId())
                    .enterpriseName(enterprise.getEnterpriseName()).build());
        }else {
            return ResponseUtil.error("Could not find id : "+enterprisesId,400);
        }

    }

    public MsmeResponse<?> deleteByEnterprisesId(Long enterprisesId) {

        Optional<Enterprise> byId = enterpriseRepository.findById(enterprisesId);
        if (byId.isPresent()) {
            enterpriseRepository.deleteById(enterprisesId);
            Enterprise enterprise = byId.get();
            return ResponseUtil.success("Deleted successfully with id : "+enterprisesId,EnterpriseDto.builder()
                    .enterpriseId(enterprise.getEnterpriseId())
                    .enterpriseName(enterprise.getEnterpriseName()).build());
        }else {

            return ResponseUtil.error("Could not find id : "+enterprisesId,400);
        }
    }
}
