package de.thoms.kuekels.views.login;

import javax.management.relation.Role;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import de.thoms.kuekels.data.User;
import de.thoms.kuekels.data.UserRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

@Route("user-management")

@RolesAllowed("ADMIN")
public class UserManagementView extends VerticalLayout {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementView(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        Button newUserButton = new Button("Neuen Benutzer anlegen");
        newUserButton.addClickListener(e -> openUserDialog());

        add(newUserButton);
    }

    private void openUserDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Neuen Benutzer erstellen");
        //Lable l = new Lable("Das Passwort ist immer 'angeln'");

        TextField usernameField = new TextField("Benutzername");
        //PasswordField passwordField = new PasswordField("Passwort");
        TextField emailField = new TextField("E-Mail");
        TextField telefonField = new TextField("Telefon");

        Button saveButton = new Button("Erstellen", event -> {
            String username = usernameField.getValue();
            String password = "angeln";
            String email = emailField.getValue();
            String telefon = telefonField.getValue();


            if (username.isEmpty() || password.isEmpty()) {
                Notification.show("Benutzername und Passwort sind erforderlich.", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (userRepository.findByUsername(username).isPresent()) {
                Notification.show("Benutzername existiert bereits.", 3000, Notification.Position.MIDDLE);
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setHashedPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setTelefon(telefon);
            //user.setRoles( 'adsa');

            userRepository.save(user);
            Notification.show("Benutzer erfolgreich erstellt!", 3000, Notification.Position.TOP_CENTER);
            dialog.close();
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Abbrechen", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, saveButton);
        VerticalLayout formLayout = new VerticalLayout(usernameField, emailField, telefonField);

        dialog.add(formLayout, buttons);
        dialog.open();
    }
}

