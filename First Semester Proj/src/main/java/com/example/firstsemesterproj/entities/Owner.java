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

    @ManyToMany(mappedBy = "owners")
    private Set<Dog> dogs;

    @Column(name = "name", length = 255, nullable = true)
    private String name;

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
}
