package com.rubinbaldoro.library.views;

import com.rubinbaldoro.library.entity.Book;
import com.rubinbaldoro.library.service.BookService;
import com.rubinbaldoro.library.service.GoogleBooksService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.UploadHandler;
import jakarta.annotation.security.PermitAll;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Base64;

@Route(value = "books", layout = MainLayout.class)
@PageTitle("Books | Rubin's Library")
@PermitAll
public class BookView extends VerticalLayout {

    private final BookService bookService;
    private final GoogleBooksService googleBooksService;
    private final Grid<Book> grid = new Grid<>(Book.class, false);

    public BookView(BookService bookService, GoogleBooksService googleBooksService) {
        this.bookService = bookService;
        this.googleBooksService = googleBooksService;

        setSizeFull();
        configureGrid();

        Button addBookButton = new Button("Add New Book", VaadinIcon.PLUS.create());
        addBookButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBookButton.addClickListener(e -> openBookEditor(new Book()));

        add(addBookButton, grid);
        updateList();
    }

    private void configureGrid() {
        grid.setSizeFull();

        // Custom Column for Cover Image
        grid.addColumn(new ComponentRenderer<>(book -> {
            Image image = new Image();
            if (book.getCoverImage() != null && book.getCoverImage().length > 0) {
                String base64 = Base64.getEncoder().encodeToString(book.getCoverImage());
                image.setSrc("data:image/png;base64," + base64);
            } else {
                image.setSrc("https://via.placeholder.com/50x75?text=No+Cover");
            }
            image.setWidth("50px");
            image.setHeight("75px");
            image.getStyle().set("object-fit", "cover");
            return image;
        })).setHeader("Cover").setWidth("80px").setFlexGrow(0);

        grid.addColumn(Book::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(Book::getAuthor).setHeader("Author").setSortable(true);
        grid.addColumn(Book::getIsbn).setHeader("ISBN");
        grid.addColumn(Book::getPublishYear).setHeader("Year"); // Added Year column

        // Edit Button Column
        grid.addComponentColumn(book -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addClickListener(e -> openBookEditor(book));
            return editBtn;
        });
    }

    private void updateList() {
        grid.setItems(bookService.findAll(null));
    }

    // --- THE MAGIC DIALOG ---
    private void openBookEditor(Book book) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(book.getId() == null ? "New Book" : "Edit Book");

        // 1. Fields
        TextField isbnField = new TextField("ISBN");
        Button searchBtn = new Button("Auto-Fill", VaadinIcon.MAGIC.create());
        searchBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        TextField titleField = new TextField("Title");
        TextField authorField = new TextField("Author");
        IntegerField yearField = new IntegerField("Year"); // Added Year field

        // 2. Image Preview Component
        Image coverPreview = new Image();
        coverPreview.setWidth("100px");
        coverPreview.setVisible(false);

        // Populate fields if editing existing book
        if (book.getId() != null) {
            isbnField.setValue(book.getIsbn());
            titleField.setValue(book.getTitle());
            authorField.setValue(book.getAuthor());
            yearField.setValue(book.getPublishYear());
            if (book.getCoverImage() != null) {
                String base64 = Base64.getEncoder().encodeToString(book.getCoverImage());
                coverPreview.setSrc("data:image/png;base64," + base64);
                coverPreview.setVisible(true);
            }
        }

        // 3. The "Auto-Fill" Logic (Google Books)
        searchBtn.addClickListener(e -> {
            String isbn = isbnField.getValue();
            if (isbn == null || isbn.isEmpty()) {
                Notification.show("Please enter an ISBN first");
                return;
            }

            googleBooksService.searchByIsbn(isbn).ifPresentOrElse(info -> {
                titleField.setValue(info.title());
                authorField.setValue(info.author());
                // Note: Google API often doesn't give a clean year, so we skip auto-filling year for simplicity

                // If Google gave us an image URL, download it!
                if (info.imageUrl() != null && !info.imageUrl().isEmpty()) {
                    try {
                        byte[] imageBytes = downloadImage(info.imageUrl());
                        book.setCoverImage(imageBytes);

                        // Update Preview
                        String base64 = Base64.getEncoder().encodeToString(imageBytes);
                        coverPreview.setSrc("data:image/png;base64," + base64);
                        coverPreview.setVisible(true);

                        Notification.show("Book details found!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    } catch (Exception ex) {
                        Notification.show("Found info, but failed to download cover image.");
                    }
                }
            }, () -> Notification.show("Book not found on Google Books").addThemeVariants(NotificationVariant.LUMO_ERROR));
        });

        // 4. Modern Vaadin 25 Upload Logic
        UploadHandler handler = UploadHandler.inMemory((metadata, bytes) -> {
            book.setCoverImage(bytes);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            coverPreview.setSrc("data:image/png;base64," + base64);
            coverPreview.setVisible(true);
        });

        Upload upload = new Upload(handler);
        upload.setAcceptedFileTypes("image/jpeg", "image/png");
        upload.setMaxFiles(1);
        upload.setDropLabel(new Span("Drop cover image here"));

        // Layouts
        HorizontalLayout isbnLayout = new HorizontalLayout(isbnField, searchBtn);
        isbnLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        VerticalLayout dialogLayout = new VerticalLayout(
                isbnLayout,
                titleField,
                authorField,
                yearField, // Added year field to layout
                new Span("Cover Image:"),
                coverPreview,
                upload
        );

        // 5. Save Action
        Button saveButton = new Button("Save", e -> {
            book.setIsbn(isbnField.getValue());
            book.setTitle(titleField.getValue());
            book.setAuthor(authorField.getValue());
            book.setPublishYear(yearField.getValue()); // Save the year

            bookService.save(book);
            updateList();
            dialog.close();
            Notification.show("Book saved!");
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private byte[] downloadImage(String urlString) throws IOException {
        URL url = URI.create(urlString).toURL();
        try (InputStream in = url.openStream()) {
            return in.readAllBytes();
        }
    }
}