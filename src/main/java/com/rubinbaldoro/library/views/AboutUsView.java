package com.rubinbaldoro.library.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("/about-us")
@PageTitle("About us Page")
public class AboutUsView extends VerticalLayout {

    public AboutUsView(){
        add(new H1("About us Page"));
    }
}
