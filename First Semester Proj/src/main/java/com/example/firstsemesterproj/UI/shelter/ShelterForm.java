package com.example.firstsemesterproj.UI.shelter;

import com.example.firstsemesterproj.UI.owner.ListOwnersLayout;
import com.example.firstsemesterproj.entities.Dog;
import com.example.firstsemesterproj.entities.Owner;
import com.example.firstsemesterproj.entities.Shelter;
import com.example.firstsemesterproj.services.DogoService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ShelterForm extends FormLayout {

    private TextField shelterName = new TextField("Shelter name");
    private TextField dogosNames = new TextField("Dogos names");

    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    private Button close = new Button("Cancel");

    private DogoService service;
    private Shelter lastShelterRef;
    private Grid<Shelter> gridRef;
    private ListShelterLayout listShelterLayoutRef;


    public ShelterForm(Shelter shelter, DogoService service, Grid<Shelter> grid, ListShelterLayout ldlref) {
        this.listShelterLayoutRef = ldlref;
        this.gridRef = grid;
        this.service = service;
        setShelter(shelter);

        add(shelterName, dogosNames, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> saveShelter(click));
        delete.addClickListener(click -> deleteShelter(click));
        close.addClickListener(clickEvent -> postUpdate());

        return new HorizontalLayout(save, delete, close);
    }

    public void saveShelter(ClickEvent<Button> clickEvent) {
        lastShelterRef.setLocation(this.shelterName.getValue());
        this.lastShelterRef = this.service.persistOrUpdateShelter(this.lastShelterRef);

        manageDogos();
        postUpdate();
    }

    private void deleteShelter(ClickEvent<Button> click) {
        List<Dog> allDogsByOwner = this.service.getAllDogsByShelter(this.lastShelterRef);
        for (Dog dog : allDogsByOwner) {
            dog.getOwners().remove(lastShelterRef);
            service.persistOrUpdateDog(dog);
        }
        this.lastShelterRef.setDeleted(true);
        postUpdate();
    }

    public void setShelter(Shelter shelter) {
        if (shelter == null) {
            shelterName.setValue("");
            dogosNames.setValue("");
            lastShelterRef = null;
        } else {
            shelterName.setValue(shelter.getLocation());
            dogosNames.setValue(String.join(", ", shelter.getDogs().stream().map(d -> d.getName()).collect(Collectors.toList())));
            lastShelterRef = shelter;
        }
    }

    private void postUpdate() {
        this.lastShelterRef = this.service.persistOrUpdateShelter(this.lastShelterRef);
        this.setVisible(false);
        gridRef.deselectAll();
        listShelterLayoutRef.updateList(listShelterLayoutRef.filterTextByDogoName.getValue(), listShelterLayoutRef.filterTextByShelterName.getValue());
    }

    private void manageDogos() {
        List<String> allDogosFromUI = Arrays.stream(this.dogosNames.getValue().split(", ")).collect(Collectors.toList());
        List<String> allPersistedDogosLinkedToLastShelterRef = lastShelterRef.getDogs().stream().map(dogo -> dogo.getName()).collect(Collectors.toList());
        List<String> newDogos = allDogosFromUI.stream().filter(dogo -> !allPersistedDogosLinkedToLastShelterRef.contains(dogo)).collect(Collectors.toList());

        List<String> dogosNamesForRemove = allPersistedDogosLinkedToLastShelterRef.stream().filter(persDogo -> !allDogosFromUI.contains(persDogo)).collect(Collectors.toList());
        this.lastShelterRef.getDogs().forEach(dogo -> {
            if (dogosNamesForRemove.contains(dogo.getName())) {
                dogo.getShelters().remove(lastShelterRef);
                this.service.persistOrUpdateDog(dogo);
            }
        });

        List<Dog> allDogosByDogoNameAndShelterName = service.findAllDogosByDogoNameAndShelterName("", "");
        List<String> allPersistedDogos = allDogosByDogoNameAndShelterName
                .stream().map(d -> d.getName()).collect(Collectors.toList());

        newDogos.forEach(dogoName -> {
            if (allPersistedDogos.contains(dogoName)) {
                allDogosByDogoNameAndShelterName.stream().filter(d -> d.getName().equals(dogoName)).findFirst().ifPresent(innerD -> {
                    innerD.getShelters().add(lastShelterRef);
                    this.service.persistOrUpdateDog(innerD);
                });
            } else {
                Dog dogo = new Dog();
                dogo.setName(dogoName);
                dogo.setBreed("Unknown");
                HashSet<Shelter> shelters = new HashSet();
                shelters.add(this.lastShelterRef);
                dogo.setShelters(shelters);
                this.service.persistOrUpdateDog(dogo);
            }
        });
    }
}
