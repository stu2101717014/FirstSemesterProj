package com.example.firstsemesterproj.UI;

import com.example.firstsemesterproj.entities.Dog;
import com.example.firstsemesterproj.entities.Owner;
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


public class OwnerForm  extends FormLayout {

    private TextField ownerName = new TextField("Owner name");
    private TextField dogosNames = new TextField("Dogos names");

    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    private Button close = new Button("Cancel");

    private DogoService service;
    private Owner lastOwnerRef;
    private Grid<Owner> gridRef;
    private ListOwnersLayout listOwnerLayoutRef;

    public OwnerForm(Owner owner, DogoService service, Grid<Owner> grid, ListOwnersLayout ldlref) {
        this.listOwnerLayoutRef = ldlref;
        this.gridRef = grid;
        this.service = service;
        setOwner(owner);

        add(ownerName, dogosNames, createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> saveOwner(click));
        delete.addClickListener(click -> deleteOwner(click));
        close.addClickListener(clickEvent -> postUpdate());

        return new HorizontalLayout(save, delete, close);
    }

    private void saveOwner(ClickEvent<Button> clickEvent){
        lastOwnerRef.setName(this.ownerName.getValue());
        this.lastOwnerRef = this.service.persistOrUpdateOwner(this.lastOwnerRef);

        manageDogos();
        postUpdate();

    }

    private void deleteOwner(ClickEvent<Button> click) {
        List<Dog> allDogsByOwner = this.service.getAllDogsByOwner(this.lastOwnerRef);
        for (Dog dog: allDogsByOwner) {
            dog.getOwners().remove(lastOwnerRef);
            service.persistOrUpdateDog(dog);
        }
        this.lastOwnerRef.setDeleted(true);
        postUpdate();
    }

    public void setOwner(Owner owner) {
        if (owner == null) {
            ownerName.setValue("");
            dogosNames.setValue("");
            lastOwnerRef = null;
        } else {
            ownerName.setValue(owner.getName());
            dogosNames.setValue(String.join(", ", owner.getDogs().stream().map(d -> d.getName()).collect(Collectors.toList())));
            lastOwnerRef = owner;
        }
    }

    private void postUpdate() {
        this.lastOwnerRef = this.service.persistOrUpdateOwner(this.lastOwnerRef);
        this.setVisible(false);
        gridRef.deselectAll();
        listOwnerLayoutRef.updateList(listOwnerLayoutRef.filterTextByDogoName.getValue(), listOwnerLayoutRef.filterTextByOwnerName.getValue());
    }

    private void manageDogos(){
        List<String> allDogosFromUI = Arrays.stream(this.dogosNames.getValue().split(", ")).collect(Collectors.toList());
        List<String> allPersistedDogosLinkedToLastOwnerRef = lastOwnerRef.getDogs().stream().map(dogo -> dogo.getName()).collect(Collectors.toList());
        List<String> newDogos = allDogosFromUI.stream().filter(dogo -> !allPersistedDogosLinkedToLastOwnerRef.contains(dogo)).collect(Collectors.toList());

        List<String> dogosNamesForRemove = allPersistedDogosLinkedToLastOwnerRef.stream().filter(persDogo -> !allDogosFromUI.contains(persDogo)).collect(Collectors.toList());
        this.lastOwnerRef.getDogs().forEach(dogo ->{
            if (dogosNamesForRemove.contains(dogo.getName())){
                dogo.getOwners().remove(lastOwnerRef);
                this.service.persistOrUpdateDog(dogo);
            }
        });

        List<Dog> allDogosByDogoNameAndOwnerName = service.findAllDogosByDogoNameAndOwnerName("", "");
        List<String> allPersistedDogos = allDogosByDogoNameAndOwnerName
                .stream().map(d -> d.getName()).collect(Collectors.toList());

        newDogos.forEach(dogoName -> {
            if (allPersistedDogos.contains(dogoName)){
                allDogosByDogoNameAndOwnerName.stream().filter(d -> d.getName().equals(dogoName)).findFirst().ifPresent(innerD -> {
                    innerD.getOwners().add(lastOwnerRef);
                    this.service.persistOrUpdateDog(innerD);
                });
            }else {
                Dog dogo = new Dog();
                dogo.setName(dogoName);
                dogo.setBreed("Unknown");
                HashSet<Owner> owners = new HashSet();
                owners.add(this.lastOwnerRef);
                dogo.setOwners(owners);
                this.service.persistOrUpdateDog(dogo);
            }
        });

    }
}
