package com.momsme.momsme.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Variables {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long variablesId;

    @Column(unique = true, nullable = false)
    private String variablesName;
}

