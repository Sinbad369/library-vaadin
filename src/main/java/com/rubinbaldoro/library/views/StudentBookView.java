package com.rubinbaldoro.library.views;

import com.rubinbaldoro.library.entity.Book;
import com.rubinbaldoro.library.entity.Student;
import com.rubinbaldoro.library.entity.StudentBook;
import com.rubinbaldoro.library.enums.StudentBookStatus;
import com.rubinbaldoro.library.service.BookService;
import com.rubinbaldoro.library.service.StudentBookService;
import com.rubinbaldoro.library.service.StudentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "loans", layout = MainLayout.class)
@PageTitle("Loans | Rubin's Library")
@PermitAll
public class StudentBookView extends VerticalLayout {

    private final StudentBookService studentBookService;
    private final StudentService studentService;
    private final BookService bookService;

    private final Grid<StudentBook> grid = new Grid<>(StudentBook.class, false);

    public StudentBookView(StudentBookService studentBookService,
                           StudentService studentService,
                           BookService bookService) {
        this.studentBookService = studentBookService;
        this.studentService = studentService;
        this.bookService = bookService;

        setSizeFull();
        configureGrid();

        // "New Loan" Button
        Button newLoanButton = new Button("Assign Book", VaadinIcon.PLUS.create());
        newLoanButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newLoanButton.addClickListener(e -> openLoanDialog());

        add(newLoanButton, grid);
        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();

        // 1. Student Name Column
        grid.addColumn(loan -> {
            Student s = loan.getStudent();
            return s != null ? s.getFirstName() + " " + s.getLastName() : "Unknown";
        }).setHeader("Student").setSortable(true);

        // 2. Book Title Column
        grid.addColumn(loan -> {
            Book b = loan.getBook();
            return b != null ? b.getTitle() : "Unknown";
        }).setHeader("Book").setSortable(true);

        // 3. Dates
        grid.addColumn(StudentBook::getAssignedDate).setHeader("Assigned Date");
        grid.addColumn(StudentBook::getReturnedDate).setHeader("Returned Date");

        // 4. Status Badge (Optional: you can style this later)
        grid.addColumn(StudentBook::getStatus).setHeader("Status");

        // 5. "Return" Button Column
        grid.addComponentColumn(loan -> {
            if (loan.getStatus() == StudentBookStatus.TAKEN) {
                Button returnBtn = new Button("Return", VaadinIcon.ARROW_LEFT.create());
                returnBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);

                returnBtn.addClickListener(e -> {
                    // Call your service logic to return the book
                    studentBookService.returnBook(loan);
                    updateList(); // Refresh grid
                    Notification.show("Book returned successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                });
                return returnBtn;
            } else {
                Span closedSpan = new Span("Closed");
                closedSpan.getStyle().set("color", "gray");
                return closedSpan;
            }
        }).setHeader("Actions");
    }

    private void updateList() {
        // Fetch all loans (null usually means "no filter" in your service pattern)
        grid.setItems(studentBookService.findAll(null));
    }

    private void openLoanDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Assign Book to Student");

        // 1. Student Dropdown
        ComboBox<Student> studentSelect = new ComboBox<>("Student");
        studentSelect.setItems(studentService.findAll(null)); // Load all students
        studentSelect.setItemLabelGenerator(s -> s.getFirstName() + " " + s.getLastName());
        studentSelect.setWidthFull();

        // 2. Book Dropdown
        ComboBox<Book> bookSelect = new ComboBox<>("Book");
        bookSelect.setItems(bookService.findAll(null)); // Load all books
        bookSelect.setItemLabelGenerator(Book::getTitle);
        bookSelect.setWidthFull();

        Button saveButton = new Button("Assign", e -> {
            if (studentSelect.getValue() == null || bookSelect.getValue() == null) {
                Notification.show("Please select both a student and a book");
                return;
            }

            // Create new Loan
            StudentBook loan = new StudentBook();
            loan.setStudent(studentSelect.getValue());
            loan.setBook(bookSelect.getValue());

            // NOTE: Your service's save() method automatically sets the
            // AssignedDate to NOW and Status to TAKEN.
            studentBookService.save(loan);

            updateList();
            dialog.close();
            Notification.show("Book assigned successfully!");
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(studentSelect, bookSelect);
        dialogLayout.setPadding(false);

        dialog.add(dialogLayout);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }
}