package com.rubinbaldoro.library.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("/service")
@PageTitle("Service Page")
public class ServicesView extends VerticalLayout {
    public ServicesView(){
        add(new H1("Services Page"));
    }
}
