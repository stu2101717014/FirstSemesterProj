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
import java.util.Set;
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


        List<Dog> dogosRes = dogRepository.findAll().stream().filter(dog ->
                (dogoNameInnerParam.isEmpty() || dog.getName().contains(dogoNameInnerParam)) &&
                        (ownerNameInnerParam.isEmpty() || dog.getOwners().stream().anyMatch(o -> o.getName().contains(ownerNameInnerParam))) &&
                        !dog.getDeleted()
        ).collect(Collectors.toList());

        dogosRes.forEach(dog -> {
            Set<Owner> owners = dog.getOwners();
            owners.removeIf(owner -> owner.isDeleted());
        });

        return dogosRes;
    }

    public List<Shelter> getAllShelters() {
        return this.shelterRepository.findAll();
    }

    public List<Owner> getAllOwners(String ownerName, String dogoName) {
        String dogoNameInnerParam = dogoName == null ? "" : dogoName;
        String ownerNameInnerParam = ownerName == null ? "" : ownerName;

        return this.ownerRepository.findAll().stream().filter(o ->
                (ownerNameInnerParam.isEmpty() || o.getName().contains(ownerNameInnerParam)) &&
                        (dogoNameInnerParam.isEmpty() || o.getDogs().stream().anyMatch(d -> d.getName().contains(dogoNameInnerParam))) &&
                        !o.isDeleted()
        ).collect(Collectors.toList());
    }

    public List<Dog> getAllDogsByOwner(Owner owner){
        return dogRepository.findAll().stream().filter(d -> d.getOwners().contains(owner)).collect(Collectors.toList());
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

    public Owner getOwnerById(Long ownerId){
        return this.ownerRepository.getById(ownerId);
    }
}
