package com.rubinbaldoro.library.views;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Random;

@Route("login")
@PageTitle("Login | Rubin's Library")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // 1. Dark Elegant Background
        getStyle().set("background-color", "#0b1016");
        getStyle().set("position", "relative");
        getStyle().set("overflow", "hidden");

        // 2. CSS Styles (Reused from HomeView, plus Login Card styles)
        String css = """
            <style>
                .ruby-text-small {
                    font-family: 'Raleway', sans-serif;
                    font-size: 3rem;
                    font-weight: 900;
                    text-transform: uppercase;
                    color: rgba(255, 255, 255, 0.9); 
                    margin-bottom: 20px;
                    z-index: 20;
                    text-align: center;
                }

                /* Floating Rubies Animation */
                .back-shapes { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 1; pointer-events: none; }
                .floating-ruby {
                    position: absolute; width: 20px; height: 20px;
                    background: linear-gradient(135deg, #ff0000 0%, #590000 100%);
                    transform: rotate(45deg); opacity: 0.6;
                    animation: floatUp 15s infinite linear;
                }
                .ruby-light { background: linear-gradient(135deg, #ff4d4d 0%, #ffcccc 100%); opacity: 0.4; }
                
                @keyframes floatUp {
                    0% { transform: translateY(110vh) rotate(45deg) scale(0.5); }
                    100% { transform: translateY(-10vh) rotate(225deg) scale(1.2); }
                }

                /* Login Card Style */
                .login-card-wrapper {
                    background: rgb(102 99 99);
                    backdrop-filter: blur(10px);
                    border: 1px solid rgba(255,0,0,0.3);
                    border-radius: 15px;
                    padding: 40px;
                    z-index: 20;
                    box-shadow: 0 10px 30px rgba(0,0,0,0.5);
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                }
            </style>
        """;
        add(new Html(css));

        // 3. Floating Background Shapes
        add(createFloatingRubies());

        // 4. Login Container
        Div loginCard = new Div();
        loginCard.addClassName("login-card-wrapper");

        H1 title = new H1("MEMBER LOGIN");
        title.addClassName("ruby-text-small");

        // CONFIGURING THE FORM FOR SPRING SECURITY
        loginForm.setAction("login");  // POSTs to the URL Spring Security listens to
        loginForm.setForgotPasswordButtonVisible(false);

        // Make the form text visible on dark background (Vaadin specific theme tweak)
        loginForm.getElement().getThemeList().add("dark");

        // CREATE THE REGISTER LINK
        Div registerLinkDiv = new Div();
        registerLinkDiv.addClassName("register-link");

        Span registerText = new Span("New user? ");
        RouterLink registerLink = new RouterLink("Register here", RegistrationView.class);

        registerLinkDiv.add(registerText, registerLink);

        loginCard.add(title, loginForm, registerLinkDiv);
        add(loginCard);
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
        // Show error if Spring Security redirects back with ?error
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }
}