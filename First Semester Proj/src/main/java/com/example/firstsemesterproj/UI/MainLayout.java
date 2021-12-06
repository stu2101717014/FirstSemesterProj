package com.example.firstsemesterproj.UI;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createDrawer();
    }

    private void createDrawer() {
        RouterLink listLink = new RouterLink("Dogos", ListDogsLayout.class);
        listLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink listOwnersLink = new RouterLink("Owners", ListOwnersLayout.class);

        addToDrawer(new VerticalLayout(
                listLink,
                listOwnersLink
        ));
        
    }
}
