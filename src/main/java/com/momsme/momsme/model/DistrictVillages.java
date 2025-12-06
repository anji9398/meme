package com.momsme.momsme.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "district_villages")
@Data
public class DistrictVillages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(name = "villages_json", nullable = false, columnDefinition = "jsonb")
    private String villagesJson;  // Store JSONB as String in entity

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

