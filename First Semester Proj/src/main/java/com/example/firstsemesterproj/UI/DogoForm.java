package com.example.firstsemesterproj.UI;

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
import java.util.List;
import java.util.stream.Collectors;


public class DogoForm extends FormLayout {

    private TextField dogoName = new TextField("Dogo name");
    private TextField dogoBreed = new TextField("Dogo breed");
    private TextField dogoOwners = new TextField("Dogo owners");
    private TextField dogoShelters = new TextField("Dogo shelters");

    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    private Button close = new Button("Cancel");

    private DogoService service;
    private Dog lastDogRef;
    private Grid<Dog>  gridRef;
    private ListDogsLayout listDogsLayoutRef;


    public DogoForm(Dog dogo, DogoService service, Grid<Dog> grid, ListDogsLayout ldlref) {
        this.listDogsLayoutRef = ldlref;
        this.gridRef = grid;
        this.service = service;
        setDogoInner(dogo);
        add(dogoName, dogoBreed, dogoOwners, dogoShelters, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> saveDogo(click));
        delete.addClickListener(click -> deleteDogo(click));
        close.addClickListener(clickEvent -> postUpdate());

        return new HorizontalLayout(save, delete, close);
    }

    public void setDogo(Dog Dogo) {
        setDogoInner(Dogo);
    }

    private void setDogoInner(Dog dogo) {
        if (dogo == null) {
            dogoName.setValue("");
            dogoBreed.setValue("");
            dogoOwners.setValue("");
            dogoShelters.setValue("");
            lastDogRef = null;
        } else {
            dogoName.setValue(dogo.getName());
            dogoBreed.setValue(dogo.getBreed());
            dogoOwners.setValue(String.join(", ", dogo.getOwners().stream().map(o -> o.getName()).collect(Collectors.toList())));
            dogoShelters.setValue(String.join(", ", dogo.getShelters().stream().map(s -> s.getLocation()).collect(Collectors.toList())));
            lastDogRef = dogo;
        }
    }

    private void deleteDogo(ClickEvent<Button> clickEvent){
        lastDogRef.setDeleted(true);
        postUpdate();
    }

    private void saveDogo(ClickEvent<Button> clickEvent){
        manageOwners();
        manageShelters();

        lastDogRef.setName(this.dogoName.getValue());
        lastDogRef.setBreed(this.dogoBreed.getValue());

        postUpdate();

    }

    private void postUpdate() {
        lastDogRef = service.persistOrUpdateDog(lastDogRef);
        this.setVisible(false);
        gridRef.deselectAll();
        listDogsLayoutRef.updateList(listDogsLayoutRef.filterTextByDogoName.getValue(), listDogsLayoutRef.filterTextByOwnerName.getValue());
    }

    private void manageOwners() {
        List<String> allOwnersFromUI = Arrays.stream(this.dogoOwners.getValue().split(", ")).collect(Collectors.toList());
        List<String> allPersistedOwners = lastDogRef.getOwners().stream().map(_do -> _do.getName()).collect(Collectors.toList());
        List<String> newOwners = allOwnersFromUI.stream().filter(uio -> !allPersistedOwners.contains(uio)).collect(Collectors.toList());

        List<String> ownersForRemove = allPersistedOwners.stream().filter(pdo -> !allOwnersFromUI.contains(pdo)).collect(Collectors.toList());
        lastDogRef.getOwners().removeIf(ow -> ownersForRemove.contains(ow.getName()));

        newOwners.forEach(no -> {
            Owner owner = new Owner();
            owner.setName(no);
            Owner persisted = service.persistOrUpdateOwner(owner);
            lastDogRef.getOwners().add(persisted);
        });
    }

    private void manageShelters(){
        List<String> allSheltersFromUI = Arrays.stream(this.dogoShelters.getValue().split(", ")).collect(Collectors.toList());
        List<String> allPersistedShelters = lastDogRef.getShelters().stream().map(sh -> sh.getLocation()).collect(Collectors.toList());
        List<String> newShelters = allSheltersFromUI.stream().filter(sh -> !allPersistedShelters.contains(sh)).collect(Collectors.toList());

        List<String> sheltersForRemove = allPersistedShelters.stream().filter(psh -> !allSheltersFromUI.contains(psh)).collect(Collectors.toList());
        lastDogRef.getShelters().removeIf(sh -> sheltersForRemove.contains(sh.getLocation()));

        newShelters.forEach(sh -> {
            Shelter shelter = new Shelter();
            shelter.setLocation(sh);
            Shelter persisted = service.persistOrUpdateShelter(shelter);
            lastDogRef.getShelters().add(persisted);
        });
    }
}
