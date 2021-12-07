package com.example.firstsemesterproj.UI.shelter;

import com.example.firstsemesterproj.UI.MainLayout;
import com.example.firstsemesterproj.entities.Dog;
import com.example.firstsemesterproj.entities.Shelter;
import com.example.firstsemesterproj.services.DogoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Route(value="Shelter", layout = MainLayout.class)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListShelterLayout extends VerticalLayout {

    private final Grid<Shelter> grid = new Grid<>(Shelter.class);

    public TextField filterTextByShelterName = new TextField();
    public TextField filterTextByDogoName = new TextField();
    private Button newShelterButton = new Button("New shelter");

    private ShelterForm form;

    private DogoService service;

    public ListShelterLayout(DogoService service) {
        super.setSizeFull();
        this.service = service;

        configureGrid();

        form = new ShelterForm(null, this.service, grid, this);
        form.setWidth("25em");
        form.setVisible(false);

        FlexLayout content = new FlexLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setFlexShrink(0, form);
        content.addClassNames("content", "gap-m");
        content.setSizeFull();

        newShelterButton.addClickListener(click -> editShelter(initNewShelter()));
        add(new HorizontalLayout(getToolbarOnShelterName(), getToolbarOnDogoName(), newShelterButton), content);

        updateList(this.filterTextByDogoName.getValue(), this.filterTextByShelterName.getValue());
        grid.asSingleSelect().addValueChangeListener(event -> editShelter(event.getValue()));

    }

    public void editShelter(Shelter shelter) {
        if (shelter == null) {
            closeEditor();
        } else {
            form.setShelter(shelter);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setShelter(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private HorizontalLayout getToolbarOnDogoName() {
        filterTextByDogoName.setPlaceholder("Filter by dogo name...");
        filterTextByDogoName.setClearButtonVisible(true);
        filterTextByDogoName.setValueChangeMode(ValueChangeMode.LAZY);
        filterTextByDogoName.addValueChangeListener(e -> updateList(this.filterTextByDogoName.getValue(), this.filterTextByShelterName.getValue()));

        HorizontalLayout toolbar = new HorizontalLayout(filterTextByDogoName);
        toolbar.addClassName("toolbarDogoName");
        return toolbar;
    }

    private HorizontalLayout getToolbarOnShelterName() {
        filterTextByShelterName.setPlaceholder("Filter by shelter name...");
        filterTextByShelterName.setClearButtonVisible(true);
        filterTextByShelterName.setValueChangeMode(ValueChangeMode.LAZY);
        filterTextByShelterName.addValueChangeListener(e -> updateList(this.filterTextByDogoName.getValue(), this.filterTextByShelterName.getValue()));

        HorizontalLayout toolbar = new HorizontalLayout(filterTextByShelterName);
        toolbar.addClassName("toolbarShelterName");
        return toolbar;
    }

    public void updateList(String dogoName, String shelterName) {
        List<Shelter> allshelters = service.getAllShelters(shelterName, dogoName);
        grid.setItems(allshelters);
    }

    private void configureGrid() {
        grid.addClassNames("shelter-grid");
        grid.setSizeFull();
        grid.setColumns("location");
        grid.addColumn(shelter -> String.join(", ", shelter.getDogs().stream().map(d -> d.getName()).collect(Collectors.toList()))).setHeader("Dogos");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private Shelter initNewShelter() {
        Shelter shelter = new Shelter();
        shelter.setLocation("");
        shelter.setDogs(new HashSet<Dog>());
        return shelter;
    }
}
