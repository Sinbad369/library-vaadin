package com.rubinbaldoro.library.views;

import com.rubinbaldoro.library.service.StudentService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;
import java.util.Map;

@Route("verify")
@AnonymousAllowed
public class VerificationView extends VerticalLayout implements BeforeEnterObserver {

    private final StudentService studentService;
    private final H1 statusHeader = new H1();
    private final H3 messageLabel = new H3();

    public VerificationView(StudentService studentService) {
        this.studentService = studentService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        RouterLink loginLink = new RouterLink("Go to Login", LoginView.class);
        add(statusHeader, messageLabel, loginLink);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Read the ?token=... parameter from the URL
        Map<String, List<String>> parameters = event.getLocation().getQueryParameters().getParameters();
        
        if (parameters.containsKey("token")) {
            String token = parameters.get("token").get(0);
            boolean verified = studentService.verifyToken(token);
            
            if (verified) {
                statusHeader.setText("Success!");
                statusHeader.getStyle().set("color", "green");
                messageLabel.setText("Your account has been verified. You may now log in.");
            } else {
                statusHeader.setText("Verification Failed");
                statusHeader.getStyle().set("color", "red");
                messageLabel.setText("The verification link is invalid or has expired.");
            }
        } else {
            statusHeader.setText("Error");
            messageLabel.setText("No verification token provided.");
        }
    }
}