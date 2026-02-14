package com.rubinbaldoro.library.views;

import com.rubinbaldoro.library.component.NavigationComponent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.Random;

@PageTitle("Welcome | Rubin's Library")
@Route(value = "")
@AnonymousAllowed
public class LandingView extends VerticalLayout {

    public LandingView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        NavigationComponent navbar = new NavigationComponent();
        add(navbar);

        //HERO SECTION (Takes all remaining space and centers its content)
        VerticalLayout heroSection = new VerticalLayout();
        heroSection.setSizeFull();
        heroSection.setJustifyContentMode(JustifyContentMode.CENTER); // Center vertically
        heroSection.setAlignItems(Alignment.CENTER); // Center horizontally
        heroSection.setPadding(false);

        // Dark Elegant Background (Dark Blue/Black to make the Red Pop)
        getStyle().set("background-color", "#0b1016");
        getStyle().set("position", "relative");
        getStyle().set("overflow", "hidden");

        // --- 1. CSS: RUBY TEXT & DIAMOND SHAPES ---
        String css = """
            <style>
                /* --- THE RUBY TEXT ANIMATION --- */
                .ruby-text {
                    font-family: 'Raleway', sans-serif;
                    font-size: 8vw;
                    font-weight: 900;
                    text-transform: uppercase;
                    text-align: center;
                    margin: 0;
                    line-height: 1.1;
                    z-index: 20;
                    
                    /* THE MAGIC: Image inside the text */
                    color: rgba(255, 255, 255, 0.1); 
                    background-image: url('https://images.unsplash.com/photo-1596484552834-6a58f850e0a1?q=80&w=2070&auto=format&fit=crop'); /* Red Ruby Texture */
                    background-size: 150%; 
                    background-clip: text;
                    -webkit-background-clip: text;
                    
                    /* Animate the texture moving inside the letters */
                    animation: sparkle-text 10s ease-in-out infinite alternate;
                }

                @keyframes sparkle-text {
                    0% { background-position: 0% 50%; filter: brightness(110%); }
                    100% { background-position: 100% 50%; filter: brightness(140%); }
                }

                /* --- FLOATING RUBIES (DIAMONDS) --- */
                .back-shapes {
                    position: absolute;
                    top: 0; left: 0; width: 100%; height: 100%;
                    z-index: 1;
                    pointer-events: none;
                }
                
                .floating-ruby {
                    position: absolute;
                    width: 30px; 
                    height: 30px;
                    background: linear-gradient(135deg, #ff0000 0%, #590000 100%);
                    transform: rotate(45deg); /* Make it a Diamond */
                    box-shadow: 0 0 10px rgba(255, 0, 0, 0.5);
                    opacity: 0.6;
                    animation: floatUp 15s infinite linear;
                }
                
                /* Some variants for variety */
                .ruby-light { background: linear-gradient(135deg, #ff4d4d 0%, #ffcccc 100%); opacity: 0.4; }
                .ruby-dark { background: linear-gradient(135deg, #800000 0%, #330000 100%); opacity: 0.7; }

                @keyframes floatUp {
                    0% { transform: translateY(110vh) rotate(45deg) scale(0.5); }
                    100% { transform: translateY(-10vh) rotate(225deg) scale(1.2); }
                }

                /* CARD STYLES */
                .dashboard-card {
                    background: rgba(255, 255, 255, 0.05); /* Glass effect */
                    backdrop-filter: blur(10px);
                    border: 1px solid rgba(255,0,0,0.2); /* Slight red border */
                    border-radius: 15px;
                    padding: 30px;
                    width: 250px;
                    cursor: pointer;
                    transition: transform 0.3s, background 0.3s;
                    text-align: center;
                    z-index: 20;
                }
                .dashboard-card:hover {
                    transform: translateY(-10px);
                    background: rgba(255, 0, 0, 0.1); /* Red tint on hover */
                    border-color: #ff0000;
                }
                .card-title { color: white; margin: 0 0 10px 0; }
                .card-desc { color: #ccc; margin: 0; }
            </style>
        """;
        add(new Html(css));

        // --- 2. GENERATE FLOATING RUBIES ---
        Div backShapes = new Div();
        backShapes.addClassName("back-shapes");

        String[] rubyTypes = {"floating-ruby", "floating-ruby ruby-light", "floating-ruby ruby-dark"};
        Random random = new Random();

        // Create 25 rubies
        for (int i = 0; i < 40; i++) {
            Span gem = new Span();
            // Pick a random style (Dark red, Bright red, or Light pink)
            gem.addClassName("floating-ruby");
            if(random.nextBoolean()) gem.addClassName("ruby-light");

            // Randomize size
            int size = random.nextInt(20) + 10; // 10px to 30px
            gem.getStyle().set("width", size + "px").set("height", size + "px");

            // Random position
            gem.getStyle().set("left", random.nextInt(100) + "%");

            // Random delay
            gem.getStyle().set("animation-delay", "-" + random.nextInt(15) + "s");
            gem.getStyle().set("animation-duration", (random.nextInt(10) + 10) + "s"); // 10-20s speed

            backShapes.add(gem);
        }
        add(backShapes);

        // --- 3. CONTENT ---
        H1 title = new H1("RUBIN'S LIBRARY");
        title.addClassName("ruby-text");

        Paragraph subtitle = new Paragraph("The Crown Jewel of Knowledge");
        subtitle.getStyle().set("color", "rgba(255,255,255,0.7)").set("font-size", "1.3rem").set("z-index", "20");

        // Cards
        FlexLayout cardContainer = new FlexLayout();
        cardContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        cardContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        cardContainer.getStyle().set("gap", "30px").set("margin-top", "40px").set("z-index", "20");

        cardContainer.add(
                createCard("👨‍🎓 Students", "Manage Users", StudentView.class),
                createCard("📚 Books", "Inventory", BookView.class),
                createCard("🤝 Loans", "Check In / Out", StudentBookView.class)
        );

        add(title, subtitle, cardContainer);
    }

    private Div createCard(String titleText, String subText, Class<? extends com.vaadin.flow.component.Component> navTarget) {
        Div card = new Div();
        card.addClassName("dashboard-card");
        H3 title = new H3(titleText);
        title.addClassName("card-title");
        Paragraph desc = new Paragraph(subText);
        desc.addClassName("card-desc");
        card.add(title, desc);
        card.addClickListener(e -> UI.getCurrent().navigate(navTarget));
        return card;
    }
}