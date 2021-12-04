package com.example.firstsemesterproj.services;

import com.example.firstsemesterproj.entities.Dog;
import com.example.firstsemesterproj.entities.Owner;
import com.example.firstsemesterproj.entities.Shelter;
import com.example.firstsemesterproj.repos.DogRepository;
import com.example.firstsemesterproj.repos.OwnerRepository;
import com.example.firstsemesterproj.repos.ShelterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DogoService {

    private final DogRepository dogRepository;

    private final OwnerRepository ownerRepository;

    private final ShelterRepository shelterRepository;

    @Autowired
    public DogoService(DogRepository dogRepository, OwnerRepository ownerRepository, ShelterRepository shelterRepository) {
        this.dogRepository = dogRepository;
        this.ownerRepository = ownerRepository;
        this.shelterRepository = shelterRepository;
    }


    public List<Dog> findAllDogosByDogoNameAndOwnerName(String dogoName, String ownerName) {
        String dogoNameInnerParam = dogoName == null ? "" : dogoName;
        String ownerNameInnerParam = ownerName == null ? "" : ownerName;


        return dogRepository.findAll().stream().filter(dog ->
                (dogoNameInnerParam.isEmpty() || dog.getName().contains(dogoNameInnerParam)) &&
                        (ownerNameInnerParam.isEmpty() || dog.getOwners().stream().anyMatch(o -> o.getName().contains(ownerNameInnerParam))) &&
                        !dog.getDeleted()
        ).collect(Collectors.toList());

    }

    public List<Shelter> getAllShelters() {
        return this.shelterRepository.findAll();
    }

    public List<Owner> getAllOwners() {
        return this.ownerRepository.findAll();
    }

    public Dog persistOrUpdateDog(Dog dog) {
        return this.dogRepository.saveAndFlush(dog);
    }

    public Owner persistOrUpdateOwner(Owner owner) {
        return this.ownerRepository.saveAndFlush(owner);
    }

    public Shelter persistOrUpdateShelter(Shelter shelter) {
        return this.shelterRepository.saveAndFlush(shelter);
    }
}
