package com.rubinbaldoro.library.views;

import com.rubinbaldoro.library.entity.Student;
import com.rubinbaldoro.library.service.StudentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;



@Route(value = "student", layout = MainLayout.class)
public class StudentView extends VerticalLayout {

    private final StudentService studentService;

    private final Grid<Student> grid = new Grid<>(Student.class);

    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final EmailField email = new EmailField("Email");
    private final TextField studentID = new TextField("Student ID");

    private final Button saveButton = new Button("Save");
    private final Button deleteButton = new Button("Delete");

    private final Binder<Student> binder = new Binder<>(Student.class);

    private Student currentStudent = new Student();


    public StudentView(StudentService studentService) {
        this.studentService = studentService;

        setSizeFull();
        configureGrid();
        configureForm();

        HorizontalLayout formLayout = new HorizontalLayout(firstName, lastName, email, studentID, saveButton, deleteButton);
        formLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        add(formLayout, grid);
        refreshGrid();
    }

    private void configureForm() {
        binder.bindInstanceFields(this);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(click->{
            try{
                binder.writeBean(currentStudent);

                studentService.save(currentStudent);

                refreshGrid();
                clearForm();
            }catch (Exception e){
                System.out.println("Validation failed: " + e.getMessage());
            }
        });

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(click->{
            if(currentStudent.getStudentId() != null){
                try{
                    // try to delete the student
                    studentService.delete(currentStudent);

                    // If successful:
                    refreshGrid();
                    clearForm();
                    Notification.show("Student deleted successfully.");

                }catch (Exception e){
                    // if it fails (bcz they have books):
                    Notification.show("ERROR: Cannot delete this student! They still has books assigned.",
                            5000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }

            }
        });
    }

    private void refreshGrid() {
        grid.setItems(studentService.findAll(null));
    }

    private void configureGrid() {
        grid.setColumns("firstName", "lastName", "email", "studentId");

        grid.getColumns().forEach(col-> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event->{
           if(event.getValue() != null){
               currentStudent = event.getValue();
               binder.readBean(currentStudent);
           } else{
               clearForm();
           }
        });
    }

    private void clearForm() {
        currentStudent = new Student();
        binder.readBean(currentStudent);
        grid.asSingleSelect().clear();
    }

}
