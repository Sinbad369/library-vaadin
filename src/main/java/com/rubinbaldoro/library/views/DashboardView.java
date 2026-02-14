package com.rubinbaldoro.library.views;

import com.rubinbaldoro.library.service.StudentService;
import com.rubinbaldoro.library.entity.Student;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import java.util.Optional;

@Route(value = "dashboard", layout = MainLayout.class) // <--- Uses the Sidebar Layout!
@PermitAll // <--- Only logged-in users
@PageTitle("Dashboard | Rubin's Library")
public class DashboardView extends VerticalLayout {

    public DashboardView(StudentService studentService) {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        // 1. Get the current user
        Optional<Student> maybeUser = studentService.getCurrentUser();
        
        if (maybeUser.isPresent()) {
            Student user = maybeUser.get();
            add(new H1("Welcome back, " + user.getFirstName() + "!"));
            add(new Paragraph("You are now logged in. Use the sidebar to manage books."));
        } else {
            add(new H1("Welcome to Rubin's Library"));
        }
        
        // Add a dashboard widget or summary here later!
        add(new H2("📚 Library Stats Coming Soon..."));
    }
}