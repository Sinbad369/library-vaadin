package com.rubinbaldoro.library.config;

import com.rubinbaldoro.library.views.LandingView;
import com.rubinbaldoro.library.views.LoginView;
import com.rubinbaldoro.library.views.RegistrationView;
import com.rubinbaldoro.library.views.VerificationView;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringComponent
public class SecurityNavigationListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            uiEvent.getUI().addBeforeEnterListener(enterEvent -> {
                Class<?> target = enterEvent.getNavigationTarget();

                // 1. Allow Public Views
                if (LoginView.class.equals(target)
                        || LandingView.class.equals(target)
                        || RegistrationView.class.equals(target)
                        || VerificationView.class.equals(target)) {
                    return;
                }

                // 2. Check Login Status
                if (!isUserLoggedIn()) {
                    // 3. Reroute to Login
                    enterEvent.rerouteTo(LoginView.class);
                }
            });
        });
    }

    private boolean isUserLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated();
    }
}