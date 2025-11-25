package com.momsme.momsme.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
public class MsmeDataPoints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "enterprise_id", nullable = false)
    private Enterprise enterprise;

    @ManyToOne
    @JoinColumn(name = "variables_id", nullable = false)
    private Variables field;

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
