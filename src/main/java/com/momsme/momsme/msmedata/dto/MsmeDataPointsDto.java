package com.momsme.momsme.msmedata.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MsmeDataPointsDto {

    private Long id;
    private Long enterpriseId;
    private String enterpriseName;
    private Long variablesId;
    private String variableName;

    private Boolean udyam;
    private Boolean pmegp;
    private Boolean kvic;
    private Boolean energy;
    private Boolean labour;
    private Boolean tgipass;
    private Boolean factories;
    private Boolean industries;
    private Boolean societies;
    private Boolean wehub;
    private Boolean thub;
    private Boolean gst;
}
