package com.example.firstsemesterproj.repos;

import com.example.firstsemesterproj.entities.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {
}
