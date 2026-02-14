package com.rubinbaldoro.library.component;

import com.rubinbaldoro.library.views.AboutUsView;
import com.rubinbaldoro.library.views.ContactView;
import com.rubinbaldoro.library.views.LoginView;
import com.rubinbaldoro.library.views.ServicesView;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

public class NavigationComponent extends HorizontalLayout {
   public NavigationComponent(){
       setWidthFull();
       addClassName("navbar");
       setAlignItems(Alignment.CENTER);
       setJustifyContentMode(JustifyContentMode.BETWEEN);
       setPadding(true);

       // logo
       Image logo = new Image("/images/rubinLibrary.png","Logo");
       logo.setHeight("40px");

       // middle links on the menu
       RouterLink services = new RouterLink("Services", ServicesView.class);
       services.getStyle().set("color", "white").set("text-decoration", "none");

       RouterLink aboutUs = new RouterLink("About Us", AboutUsView.class);
       aboutUs.getStyle().set("color", "white").set("text-decoration", "none");

       RouterLink contact = new RouterLink("Contact", ContactView.class);
       contact.getStyle().set("color", "white").set("text-decoration", "none");

       HorizontalLayout middleNav = new HorizontalLayout(services, aboutUs, contact);
       middleNav.setSpacing(true);
       middleNav.getStyle().set("gap", "2rem");

       //
       RouterLink login = new RouterLink("Login", LoginView.class);
       login.getStyle().set("color", "#d92626"); // Red color for Login
       login.getStyle().set("font-weight", "bold");
       login.getStyle().set("border", "1px solid #d92626");
       login.getStyle().set("padding", "5px 15px");
       login.getStyle().set("border-radius", "5px");

       add(logo, middleNav, login);


       // style of navbar that aligns with dark
       getStyle().set("background-color", "rgba(0, 0, 0, 0.2)"); // Slight dark tint
       getStyle().set("backdrop-filter", "blur(5px)"); // Glass effect
       getStyle().set("border-bottom", "1px solid rgba(255,255,255,0.1)");
       getStyle().set("z-index", "100"); // Ensure it sits ON TOP of the rubies
   }

}
