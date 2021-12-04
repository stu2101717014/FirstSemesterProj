package com.example.firstsemesterproj.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "dog")
public class Dog {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "dog_shelter",
            joinColumns = @JoinColumn(name = "dog_id"),
            inverseJoinColumns = @JoinColumn(name = "shelter_id"))
    private Set<Shelter> shelters;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "dog_owner",
            joinColumns = @JoinColumn(name = "dog_id"),
            inverseJoinColumns = @JoinColumn(name = "owner_id"))
    private Set<Owner> owners;

    @Column(name = "name", length = 255, nullable = true)
    private String name;

    @Column(name = "breed", length = 255, nullable = true)
    private String breed;

    @Column(name = "pic_id", length = 255, nullable = true)
    private String picId;

    private boolean isDeleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Set<Shelter> getShelters() {
        return shelters;
    }

    public Set<Owner> getOwners() {
        return owners;
    }

    public boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setShelters(Set<Shelter> shelters) {
        this.shelters = shelters;
    }

    public void setOwners(Set<Owner> owners) {
        this.owners = owners;
    }
}
