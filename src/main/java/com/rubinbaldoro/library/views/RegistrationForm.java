package com.rubinbaldoro.library.views;

import com.rubinbaldoro.library.entity.Student;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;

public class RegistrationForm extends FormLayout {

    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final EmailField email = new EmailField("Email");
    private final TextField studentId = new TextField("Student ID");
    private final TextField login = new TextField("Username");
    private final PasswordField password = new PasswordField("Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm Password");
    private final Button submitButton = new Button("Register");

    private final Binder<Student> binder = new Binder<>(Student.class);

    public RegistrationForm() {
        // Style the form
        setResponsiveSteps(new ResponsiveStep("0", 1));
        getStyle().set("width", "100%");

        // Set placeholders for better UX
        firstName.setPlaceholder("Enter your first name");
        lastName.setPlaceholder("Enter your last name");
        email.setPlaceholder("your.email@example.com");
        studentId.setPlaceholder("e.g., STU12345");
        login.setPlaceholder("Choose a username");
        password.setPlaceholder("At least 6 characters");
        confirmPassword.setPlaceholder("Re-enter your password");

        // Apply dark theme to ALL fields
        firstName.getElement().setAttribute("theme", "dark");
        lastName.getElement().setAttribute("theme", "dark");
        email.getElement().setAttribute("theme", "dark");
        studentId.getElement().setAttribute("theme", "dark");
        login.getElement().setAttribute("theme", "dark");
        password.getElement().setAttribute("theme", "dark");
        confirmPassword.getElement().setAttribute("theme", "dark");

        // Configure fields
        firstName.setRequired(true);
        lastName.setRequired(true);
        email.setRequired(true);
        studentId.setRequired(true);
        login.setRequired(true);
        password.setRequired(true);
        confirmPassword.setRequired(true);

        // Style the submit button
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.getStyle()
                .set("background-color", "#d92626")
                .set("color", "white")
                .set("width", "100%")
                .set("margin-top", "20px");

        // Add components to form
        add(firstName, lastName, email, studentId, login, password, confirmPassword, submitButton);

        // Setup Binder with validation
        setupBinder();
    }

    private void setupBinder() {
        binder.forField(firstName)
                .asRequired("First name is required")
                .bind(Student::getFirstName, Student::setFirstName);

        binder.forField(lastName)
                .asRequired("Last name is required")
                .bind(Student::getLastName, Student::setLastName);

        binder.forField(email)
                .asRequired("Email is required")
                .withValidator(new EmailValidator("Invalid email address"))
                .bind(Student::getEmail, Student::setEmail);

        binder.forField(studentId)
                .asRequired("Student ID is required")
                .bind(Student::getStudentId, Student::setStudentId);

        binder.forField(login)
                .asRequired("Username is required")
                .withValidator(username -> username.length() >= 4,
                        "Username must be at least 4 characters")
                .bind(Student::getLogin, Student::setLogin);

        binder.forField(password)
                .asRequired("Password is required")
                .withValidator(pwd -> pwd.length() >= 6,
                        "Password must be at least 6 characters")
                .bind(Student::getPassword, Student::setPassword);
    }

    public Student getStudentIfValid() {
        // Check if confirm password matches
        if (!password.getValue().equals(confirmPassword.getValue())) {
            Notification.show("Passwords do not match!")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return null;
        }

        // Validate all fields
        if (binder.validate().isOk()) {
            Student student = new Student();
            try {
                binder.writeBean(student);
                return student;
            } catch (Exception e) {
                Notification.show("Validation error: " + e.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return null;
            }
        } else {
            Notification.show("Please fill all required fields correctly")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return null;
        }
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    public void clear() {
        binder.readBean(null);
        confirmPassword.clear();
    }
}