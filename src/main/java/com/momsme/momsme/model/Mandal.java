package com.momsme.momsme.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mandals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mandal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mandal_id")
    private Long id;

    @Column(name = "mandal_name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;
}

