package com.example.firstsemesterproj.repos;

import com.example.firstsemesterproj.entities.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {
}
