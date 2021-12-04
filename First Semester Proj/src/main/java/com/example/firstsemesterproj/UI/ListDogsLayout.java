package com.example.firstsemesterproj.UI;

import com.example.firstsemesterproj.entities.Dog;
import com.example.firstsemesterproj.entities.Owner;
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
@Route(value="", layout = MainLayout.class)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListDogsLayout extends VerticalLayout {

    private final Grid<Dog>  grid = new Grid<>(Dog.class);
    public TextField filterTextByDogoName = new TextField();
    public TextField filterTextByOwnerName = new TextField();
    private DogoService service;
    DogoForm form;

    private Button newDogo = new Button("New Dogo");

    public ListDogsLayout(DogoService service) {
        this.service = service;
        super.setSizeFull();

        configureGrid();

        form = new DogoForm(null, this.service, grid, this);
        form.setWidth("25em");
        form.setVisible(false);

        FlexLayout content = new FlexLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setFlexShrink(0, form);
        content.addClassNames("content", "gap-m");
        content.setSizeFull();

        newDogo.addClickListener(click -> editDogo(initNewDogo()));
        add(new HorizontalLayout(getToolbarOnDogoName(), getToolbarOnOwnerName(), newDogo), content);
        updateList(this.filterTextByDogoName.getValue(), this.filterTextByOwnerName.getValue());

        grid.asSingleSelect().addValueChangeListener(event -> editDogo(event.getValue()));
    }

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("name", "breed");
        grid.addColumn(dog -> String.join(", ", dog.getOwners().stream().map(o -> o.getName()).collect(Collectors.toList()))).setHeader("Owners");
        grid.addColumn(dog -> String.join(", ", dog.getShelters().stream().map(s -> s.getLocation()).collect(Collectors.toList()))).setHeader("Shelters");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    public void updateList(String dogoNameFilter, String ownerNameFilter) {
        List<Dog> allDogosByName = service.findAllDogosByDogoNameAndOwnerName(dogoNameFilter, ownerNameFilter);
        grid.setItems(allDogosByName);

    }

    private HorizontalLayout getToolbarOnDogoName() {
        filterTextByDogoName.setPlaceholder("Filter by dogo name...");
        filterTextByDogoName.setClearButtonVisible(true);
        filterTextByDogoName.setValueChangeMode(ValueChangeMode.LAZY);
        filterTextByDogoName.addValueChangeListener(e -> updateList(this.filterTextByDogoName.getValue(), this.filterTextByOwnerName.getValue()));

        HorizontalLayout toolbar = new HorizontalLayout(filterTextByDogoName);
        toolbar.addClassName("toolbarDogoName");
        return toolbar;
    }

    private HorizontalLayout getToolbarOnOwnerName() {
        filterTextByOwnerName.setPlaceholder("Filter by owner name...");
        filterTextByOwnerName.setClearButtonVisible(true);
        filterTextByOwnerName.setValueChangeMode(ValueChangeMode.LAZY);
        filterTextByOwnerName.addValueChangeListener(e -> updateList(this.filterTextByDogoName.getValue(), this.filterTextByOwnerName.getValue()));

        HorizontalLayout toolbar = new HorizontalLayout(filterTextByOwnerName);
        toolbar.addClassName("toolbarOwnerName");
        return toolbar;
    }

    public void editDogo(Dog dogo) {
        if (dogo == null) {
            closeEditor();
        } else {
            form.setDogo(dogo);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setDogo(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private Dog initNewDogo() {
        Dog dog = new Dog();
        dog.setName("");
        dog.setBreed("");
        dog.setDeleted(false);
        dog.setOwners(new HashSet<>());
        dog.setShelters(new HashSet<>());
        return dog;
    }
}
