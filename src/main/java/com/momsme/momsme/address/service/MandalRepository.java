package com.momsme.momsme.address.service;

import com.momsme.momsme.model.Mandal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MandalRepository extends JpaRepository<Mandal,Long> {
    Mandal findByNameIgnoreCase(String w);
}
