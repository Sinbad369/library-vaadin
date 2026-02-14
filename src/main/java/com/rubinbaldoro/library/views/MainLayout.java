package com.rubinbaldoro.library.views;

import com.rubinbaldoro.library.entity.Student;
import com.rubinbaldoro.library.service.StudentService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Base64;
import java.util.Optional;

public class MainLayout extends AppLayout {

    private final StudentService studentService;
    private Student currentUser;
    private Avatar avatar;

    public MainLayout(StudentService studentService) {
        this.studentService = studentService;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Rubin's Library");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM
        );

        DrawerToggle toggle = new DrawerToggle();

        // --- AVATAR SETUP ---
        avatar = new Avatar();
        avatar.addClassNames(LumoUtility.Margin.Right.LARGE);
        avatar.getStyle().set("cursor", "pointer");
        avatar.setTooltipEnabled(true);

        // Load User Data
        Optional<Student> userOpt = studentService.getCurrentUser();
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            refreshAvatarUI();
        }

        // Add Click Listener
        avatar.getElement().addEventListener("click", e -> openProfileDialog());

        // --- LAYOUT ---
        HorizontalLayout header = new HorizontalLayout(toggle, logo, avatar);

        // MODERN VAADIN 25 ALIGNMENT API
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setFlexGrow(1, logo); // Pushes Avatar to the right

        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(true, header);
    }

    private void createDrawer() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem("Students", StudentView.class, VaadinIcon.USERS.create()));
        nav.addItem(new SideNavItem("Books", BookView.class, VaadinIcon.BOOK.create()));
        nav.addItem(new SideNavItem("Loans", StudentBookView.class, VaadinIcon.EXCHANGE.create()));

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        addToDrawer(scroller);
    }

    // --- PROFILE DIALOG ---
    private void openProfileDialog() {
        if (currentUser == null) return;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Profile");

        TextField firstName = new TextField("First Name");
        firstName.setValue(currentUser.getFirstName() != null ? currentUser.getFirstName() : "");

        TextField lastName = new TextField("Last Name");
        lastName.setValue(currentUser.getLastName() != null ? currentUser.getLastName() : "");

        // --- NEW VAADIN 25 UPLOAD API ---
        final byte[][] uploadedBytes = new byte[1][]; // Array used to hold bytes in memory

        UploadHandler inMemoryHandler = UploadHandler.inMemory((metadata, bytes) -> {
            uploadedBytes[0] = bytes; // Store bytes when upload finishes
        });

        Upload upload = new Upload(inMemoryHandler);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setMaxFiles(1);
        upload.setDropLabel(new Span("Drop Profile Picture Here"));

        Button saveButton = new Button("Save", e -> {
            try {
                // Update Text Fields
                currentUser.setFirstName(firstName.getValue());
                currentUser.setLastName(lastName.getValue());

                // Update Image if a new one was uploaded
                if (uploadedBytes[0] != null) {
                    currentUser.setProfilePicture(uploadedBytes[0]);
                }

                studentService.save(currentUser);

                // Refresh Avatar Immediately
                refreshAvatarUI();

                Notification.show("Profile Updated").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Failed to save profile.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(firstName, lastName, upload);
        dialogLayout.setPadding(false);
        dialog.add(dialogLayout);
        dialog.getFooter().add(cancelButton, saveButton);

        dialog.open();
    }

    private void refreshAvatarUI() {
        // Update Name Initials
        avatar.setName(currentUser.getFirstName() + " " + currentUser.getLastName());

        // --- BASE64 IMAGE STRATEGY ---
        // This bypasses StreamResource and DownloadHandler entirely.
        // It converts the image to a text string that the browser renders directly.
        if (currentUser.getProfilePicture() != null && currentUser.getProfilePicture().length > 0) {
            String base64 = Base64.getEncoder().encodeToString(currentUser.getProfilePicture());
            String dataUrl = "data:image/png;base64," + base64;
            avatar.setImage(dataUrl);
        } else {
            avatar.setImage(null);
        }
    }
}