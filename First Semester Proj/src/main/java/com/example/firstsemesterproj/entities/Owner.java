package com.example.firstsemesterproj.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "owner")
public class Owner {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "owners", fetch = FetchType.EAGER)
    private Set<Dog> dogs;

    @Column(name = "name", length = 255, nullable = true)
    private String name;

    @Column(name = "is_deleted")
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

    public Set<Dog> getDogs() {
        return dogs;
    }

    public void setDogs(Set<Dog> dogs) {
        this.dogs = dogs;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
