package com.rubinbaldoro.library.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("/contact")
@PageTitle("Contact Page")
public class ContactView extends VerticalLayout {
    public ContactView(){
        add(new H1("Contact Page"));
    }
}
