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

    @ManyToMany(mappedBy = "shelters")
    private Set<Dog> dogs;

    @Column(name = "location", length = 255, nullable = true)
    private String location;

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
}
