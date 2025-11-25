package com.momsme.momsme.msmedata.service;

import com.momsme.momsme.enterprise.repo.EnterpriseRepository;
import com.momsme.momsme.model.Enterprise;
import com.momsme.momsme.model.MsmeDataPoints;
import com.momsme.momsme.model.Variables;
import com.momsme.momsme.msmedata.dto.MsmeDataPointsDto;
import com.momsme.momsme.msmedata.repo.MsmeDataPointsRepo;
import com.momsme.momsme.msmedata.repo.VariablesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MsmeDataPointsService {

    private final EnterpriseRepository enterpriseRepository;
    private final VariablesRepository variablesRepository;
    private final MsmeDataPointsRepo msmeRepository;

    public MsmeDataPoints save(MsmeDataPointsDto req) {

        Enterprise enterprise = enterpriseRepository.findById(req.getEnterpriseId())
                .orElseThrow(() -> new RuntimeException("Enterprise not found with id: " + req.getEnterpriseId()));

        Variables field = variablesRepository.findById(req.getVariablesId())
                .orElseThrow(() -> new RuntimeException("Variable not found with id: " + req.getVariablesId()));

        MsmeDataPoints dp = getMsmeDataPoints(req, enterprise, field);

        return msmeRepository.save(dp);
    }

    private static MsmeDataPoints getMsmeDataPoints(MsmeDataPointsDto req, Enterprise enterprise, Variables field) {
        MsmeDataPoints dp = new MsmeDataPoints();

        dp.setEnterprise(enterprise);
        dp.setField(field);
        dp.setUdyam(req.getUdyam());
        dp.setPmegp(req.getPmegp());
        dp.setKvic(req.getKvic());
        dp.setEnergy(req.getEnergy());
        dp.setLabour(req.getLabour());
        dp.setTgipass(req.getTgipass());
        dp.setFactories(req.getFactories());
        dp.setIndustries(req.getIndustries());
        dp.setSocieties(req.getSocieties());
        dp.setWehub(req.getWehub());
        dp.setThub(req.getThub());
        dp.setGst(req.getGst());
        return dp;
    }

    private static MsmeDataPointsDto getMsmeDataPointstoDto(MsmeDataPoints req) {
        MsmeDataPointsDto dp = new MsmeDataPointsDto();

        dp.setEnterpriseId(req.getEnterprise().getEnterpriseId());
        dp.setEnterpriseName(req.getEnterprise().getEnterpriseName());
        dp.setVariableName(req.getField().getVariablesName());
        dp.setVariablesId(req.getField().getVariablesId());
        dp.setUdyam(req.getUdyam());
        dp.setPmegp(req.getPmegp());
        dp.setKvic(req.getKvic());
        dp.setEnergy(req.getEnergy());
        dp.setLabour(req.getLabour());
        dp.setTgipass(req.getTgipass());
        dp.setFactories(req.getFactories());
        dp.setIndustries(req.getIndustries());
        dp.setSocieties(req.getSocieties());
        dp.setWehub(req.getWehub());
        dp.setThub(req.getThub());
        dp.setGst(req.getGst());
        return dp;
    }


    public List<MsmeDataPointsDto> getEnterpriseData(Long enterpriseId) {
        List<MsmeDataPoints> byEnterpriseEnterpriseId = msmeRepository.findByEnterpriseEnterpriseId(enterpriseId);

        return byEnterpriseEnterpriseId.stream().map(e -> getMsmeDataPointstoDto(e)).toList();

    }
}
