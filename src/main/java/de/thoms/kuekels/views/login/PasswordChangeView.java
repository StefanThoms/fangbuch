package de.thoms.kuekels.views.login;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.thoms.kuekels.data.User;
import de.thoms.kuekels.data.UserRepository;
import jakarta.annotation.security.PermitAll;


@PageTitle("Passwort ändern")
@Route("anmeldung-passwort-aendern")
@Menu(order = 0, icon = LineAwesomeIconUrl.KEY_SOLID)
@PermitAll


public class PasswordChangeView extends VerticalLayout {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordChangeView(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        PasswordField currentPassword = new PasswordField("Aktuelles Passwort");
        PasswordField newPassword = new PasswordField("Neues Passwort");
        PasswordField confirmPassword = new PasswordField("Neues Passwort bestätigen");

        Button changeButton = new Button("Passwort ändern");

        Span feedback = new Span();
        feedback.getStyle().set("color", "red");

        changeButton.addClickListener(e -> {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                if (!passwordEncoder.matches(currentPassword.getValue(), user.getHashedPassword())) {
                    feedback.setText("Aktuelles Passwort ist falsch.");
                } else if (!newPassword.getValue().equals(confirmPassword.getValue())) {
                    feedback.setText("Neue Passwörter stimmen nicht überein.");
                } else {
                    user.setHashedPassword(passwordEncoder.encode(newPassword.getValue()));
                    userRepository.save(user);
                    feedback.setText("Passwort wurde erfolgreich geändert.");
                    feedback.getStyle().set("color", "green");
                }
            }
        });

        add(currentPassword, newPassword, confirmPassword, changeButton, feedback);
        setAlignItems(Alignment.CENTER);
        setPadding(true);
        setSpacing(true);
    }
}
