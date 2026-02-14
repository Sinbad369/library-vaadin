package com.rubinbaldoro.library.views;

import com.rubinbaldoro.library.entity.Student;
import com.rubinbaldoro.library.service.StudentService;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

@Route("registration")
@PageTitle("Registration | Rubin's Library")
@AnonymousAllowed
public class RegistrationView extends VerticalLayout implements BeforeEnterObserver {

    private final RegistrationForm registrationForm;
    private final StudentService studentService;
    private final PasswordEncoder passwordEncoder;

    public RegistrationView(StudentService studentService, PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.passwordEncoder = passwordEncoder;
        this.registrationForm = new RegistrationForm();

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Styles
        getStyle().set("background-color", "#0b1016");
        getStyle().set("position", "relative");
        getStyle().set("overflow", "hidden");

        // Add CSS
        String css = """
            <style>
                .login-card {
                    background: rgba(255, 255, 255, 0.05);
                    backdrop-filter: blur(10px);
                    border: 1px solid rgba(255,0,0,0.3);
                    border-radius: 15px;
                    padding: 40px;
                    box-shadow: 0 10px 30px rgba(0,0,0,0.5);
                }
                                
                vaadin-text-field::part(label),
                vaadin-email-field::part(label),
                vaadin-password-field::part(label) {
                    color: #d92626 !important;
                }
                
                /* Floating Rubies Animation */
                .back-shapes { 
                    position: absolute; 
                    top: 0; 
                    left: 0; 
                    width: 100%; 
                    height: 100%; 
                    z-index: 1; 
                    pointer-events: none; 
                }
                
                .floating-ruby {
                    position: absolute; 
                    width: 20px; 
                    height: 20px;
                    background: linear-gradient(135deg, #ff0000 0%, #590000 100%);
                    transform: rotate(45deg); 
                    opacity: 0.6;
                    animation: floatUp 15s infinite linear;
                }
                
                .ruby-light { 
                    background: linear-gradient(135deg, #ff4d4d 0%, #ffcccc 100%); 
                    opacity: 0.4; 
                }
                
                @keyframes floatUp {
                    0% { transform: translateY(110vh) rotate(45deg) scale(0.5); }
                    100% { transform: translateY(-10vh) rotate(225deg) scale(1.2); }
                }
            </style>
        """;
        add(new Html(css));

        // Background shapes
        add(createFloatingRubies());

        // Card
        Div registrationCard = new Div();
        registrationCard.addClassName("login-card");
        registrationCard.setMaxWidth("550px");
        registrationCard.getStyle().set("z-index", "10");

        H1 title = new H1("Join the Library");
        title.getStyle()
                .set("color", "#d92626")
                .set("text-align", "center")
                .set("font-family", "'Raleway', sans-serif")
                .set("font-size", "2.5rem")
                .set("font-weight", "900")
                .set("text-transform", "uppercase")
                .set("margin-bottom", "20px");

        // Handle Submit Button
        registrationForm.getSubmitButton().addClickListener(event -> registerStudent());

        registrationCard.add(title, registrationForm);
        add(registrationCard);
    }

    private void registerStudent() {
        Student student = registrationForm.getStudentIfValid();
        if (student != null) {
            try {
                // Encrypt Password
                student.setPassword(passwordEncoder.encode(student.getPassword()));

                // Save
                studentService.save(student);

                Notification.show("Registration successful! Please check your email to verify.")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Clear the form first
                registrationForm.clear();

                // Redirect to login
                getUI().ifPresent(ui -> ui.navigate(LoginView.class));

            } catch (Exception e) {
                Notification.show("Error: " + e.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }

    private Div createFloatingRubies() {
        Div backShapes = new Div();
        backShapes.addClassName("back-shapes");
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            Span gem = new Span();
            gem.addClassName("floating-ruby");
            if (random.nextBoolean()) gem.addClassName("ruby-light");

            int size = random.nextInt(15) + 10;
            gem.getStyle().set("width", size + "px").set("height", size + "px");
            gem.getStyle().set("left", random.nextInt(100) + "%");
            gem.getStyle().set("animation-delay", "-" + random.nextInt(15) + "s");
            gem.getStyle().set("animation-duration", (random.nextInt(10) + 15) + "s");
            backShapes.add(gem);
        }
        return backShapes;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // empty
    }
}