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
@Route(value="Owners", layout = MainLayout.class)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListOwnersLayout extends VerticalLayout {

    private final Grid<Owner> grid = new Grid<>(Owner.class);

    public TextField filterTextByOwnerName = new TextField();
    public TextField filterTextByDogoName = new TextField();
    private Button newOwnerButton = new Button("New owner");

    private OwnerForm form;

    private DogoService service;

    public ListOwnersLayout(DogoService service) {
        super.setSizeFull();
        this.service = service;

        configureGrid();

        form = new OwnerForm(null, this.service, grid, this);
        form.setWidth("25em");
        form.setVisible(false);

        FlexLayout content = new FlexLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setFlexShrink(0, form);
        content.addClassNames("content", "gap-m");
        content.setSizeFull();

        newOwnerButton.addClickListener(click -> editOwner(initNewOwner()));
        add(new HorizontalLayout(getToolbarOnOwnerName(), getToolbarOnDogoName(), newOwnerButton), content);

        updateList(this.filterTextByDogoName.getValue(), this.filterTextByOwnerName.getValue());
        grid.asSingleSelect().addValueChangeListener(event -> editOwner(event.getValue()));
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

    public void editOwner(Owner owner) {
        if (owner == null) {
            closeEditor();
        } else {
            form.setOwner(owner);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setOwner(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private Owner initNewOwner() {
        Owner owner = new Owner();
        owner.setName("");
        owner.setDogs(new HashSet<Dog>());
        return owner;
    }

    private void configureGrid() {
        grid.addClassNames("owner-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.addColumn(owners -> String.join(", ", owners.getDogs().stream().map(d -> d.getName()).collect(Collectors.toList()))).setHeader("Dogos");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    public void updateList(String dogoName, String ownerName) {
        List<Owner> allOwners = service.getAllOwners(ownerName, dogoName);
        grid.setItems(allOwners);
    }
}
