package com.csgroup.rba.model.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuxFileRepository extends JpaRepository<AuxFileJPA, UUID> {

}
