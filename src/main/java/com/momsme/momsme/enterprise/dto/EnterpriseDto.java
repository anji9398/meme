package com.momsme.momsme.enterprise.dto;

import com.momsme.momsme.msmedata.dto.MsmeDataPointsDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseDto {
    private Long enterpriseId;
    private String enterpriseName;
    private List<MsmeDataPointsDto> fields;
}
