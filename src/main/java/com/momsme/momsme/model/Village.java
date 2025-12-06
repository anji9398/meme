package com.momsme.momsme.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "villages",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"village_name", "mandal_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "village_id")
    private Long id;

    @Column(name = "village_name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mandal_id", nullable = false)
    private Mandal mandal;
}

