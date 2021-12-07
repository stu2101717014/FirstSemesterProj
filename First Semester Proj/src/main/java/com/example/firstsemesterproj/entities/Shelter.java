package com.example.firstsemesterproj.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "shelter")
public class Shelter {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "shelters", fetch = FetchType.EAGER)
    private Set<Dog> dogs;

    @Column(name = "location", length = 255, nullable = true)
    private String location;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Set<Dog> getDogs() {
        return dogs;
    }

    public void setDogs(Set<Dog> dogs) {
        this.dogs = dogs;
    }
}
