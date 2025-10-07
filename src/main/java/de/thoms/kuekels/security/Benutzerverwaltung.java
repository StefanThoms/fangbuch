package de.thoms.kuekels.security;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.thoms.kuekels.data.Role;
import de.thoms.kuekels.data.User;
import de.thoms.kuekels.data.UserRepository;
import jakarta.annotation.security.RolesAllowed;

@Route("admin/passwoerter-zuruecksetzen")
@PageTitle("Passwörter zurücksetzen")

@RolesAllowed("ADMIN")
@Menu(order = 4, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)

public class Benutzerverwaltung extends VerticalLayout {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private Grid<User> userGrid;

	public Benutzerverwaltung(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;

		userGrid = new Grid<>(User.class, false);
		userGrid.addColumn(User::getUsername).setHeader("Anmeldename");
		userGrid.addColumn(User::getName).setHeader("Name");
		userGrid.addColumn(User::getEmail).setHeader("E-Mail");
		userGrid.addColumn(User::getVerein).setHeader("Verein");
		userGrid.addColumn(new ComponentRenderer<Component, User>(user -> {

			if (user.getRolesString().contains("ADMIN")) {
				Icon adminIcon = VaadinIcon.SHIELD.create();
				adminIcon.setColor("red");
				adminIcon.getElement().setAttribute("title", "Administrator");
				return adminIcon;
			} else {
				return new Span("");
			}
		})).setHeader("Admin");

		userGrid.addColumn(new ComponentRenderer<Component, User>(user -> {

			if (user.getRolesString().contains("STATISTIK")) {
				Icon adminIcon = VaadinIcon.ACADEMY_CAP.create();
				adminIcon.setColor("blue");
				adminIcon.getElement().setAttribute("title", "Statistik");
				return adminIcon;
			} else {
				return new Span("");
			}
		})).setHeader("Statistik");

		userGrid.addComponentColumn(user -> {
			Button resetButton = new Button("Passwort zurücksetzen");
			resetButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

			resetButton.addClickListener(e -> {
				showResetConfirmation(user);
			});

			return resetButton;
		}).setHeader("Aktion");

		userGrid.addItemDoubleClickListener(event -> {
			User clickedUser = event.getItem();
			showUserDetailsDialog(clickedUser);
		});

		userGrid.setItems(userRepository.findAll());
		this.setHeight("800px");

		add(new H2("Benutzerverwaltung – Passwort zurücksetzen"), userGrid);

	}

	private void showUserDetailsDialog(User user) {

		Dialog dialog = new Dialog();

		String[] sItems = { "Kükels", "Wittenborn", "Mözen" };
		ComboBox<String> auswahl = new ComboBox("Verein");
		Checkbox checkbox = new Checkbox();
		checkbox.setLabel("Administrator");
		Checkbox statistik = new Checkbox();
		statistik.setLabel("Statistik");

		auswahl.setItems(sItems);

		ComboBox<Role> rolle = new ComboBox("Adminrolle");
		rolle.setItems(Role.values());

		VerticalLayout vl = new VerticalLayout();
		TextField username = new TextField("Benutzername");
		TextField email = new TextField("E-Mail Adresse");
		TextField telefon = new TextField("Telefonnummer");

		try {
			username.setValue(user.getName());
			email.setValue(user.getEmail());
			telefon.setValue(user.getTelefon());
			auswahl.setValue(user.getVerein());

			if (user.getRolesString().contains("ADMIN")) {
				checkbox.setValue(true);
				rolle.setValue(Role.ADMIN);
			}

			if (user.getRolesString().contains("STATISTIK")) {
				statistik.setValue(true);
				rolle.setValue(Role.STATISTIK);
			}

		} catch (Exception e) {

		}

		Button save = new Button("Save");

		save.addClickListener(e -> {
			user.setEmail(email.getValue());
			user.setName(username.getValue());
			user.setTelefon(telefon.getValue());
			user.setVerein(auswahl.getValue());

			user.setRoles(Role.ADMIN.equals(rolle.getValue()) ? Set.of(Role.ADMIN) : null);
			if (checkbox.getValue()) {
				user.setRoles(Set.of(Role.ADMIN));
			}

			if (statistik.getValue()) {
				user.setRoles(Set.of(Role.STATISTIK));
			}
			if (statistik.getValue().equals(false) & checkbox.getValue().equals(false)) {
				user.setRoles(null);
			}
			if (statistik.getValue() & checkbox.getValue()) {
				user.setRoles(Set.of(Role.ADMIN, Role.STATISTIK));
			}
			userRepository.save(user);
			dialog.close();
			refreshGrid();
		});

		Button delete = new Button("", VaadinIcon.TRASH.create());
		delete.getStyle().set("color", "red");
		delete.addClickListener(e -> {
			userRepository.delete(user);

			Notification.show("User gelöscht.");
			dialog.close();
			refreshGrid();
		});

		vl.add(username, email, telefon, auswahl, checkbox, statistik);
		dialog.add(vl);

		Button close = new Button("Schließen", e -> dialog.close());
		close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		HorizontalLayout buttonLayout = new HorizontalLayout(close, save, delete);
		buttonLayout.setSpacing(true); // ist per Default true
		buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER); // rechtsbündig
		dialog.add(buttonLayout);

		dialog.open();

	}

	private void showResetConfirmation(User user) {
		Dialog confirmDialog = new Dialog();
		confirmDialog.setHeaderTitle("Passwort zurücksetzen");

		Span message = new Span("Passwort auf 'angeln' zurücksetzen von " + user.getUsername());
		Button confirmBtn = new Button("Zurücksetzen", e -> {
			String neuesPasswort = "angeln"; // oder zufällig generieren
			user.setHashedPassword(passwordEncoder.encode(neuesPasswort));
			userRepository.save(user);

			Notification.show("Passwort wurde zurückgesetzt!", 3000, Notification.Position.TOP_CENTER);
			confirmDialog.close();
		});
		confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

		Button cancelBtn = new Button("Abbrechen", e -> confirmDialog.close());

		HorizontalLayout buttons = new HorizontalLayout(cancelBtn, confirmBtn);
		confirmDialog.add(message, buttons);
		confirmDialog.open();
	}

	private void openUserDialog() {
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle("Neuen Benutzer erstellen");
		// Lable l = new Lable("Das Passwort ist immer 'angeln'");

		TextField usernameField = new TextField("Benutzername");
		// PasswordField passwordField = new PasswordField("Passwort");
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
			// user.setRoles( 'adsa');

			userRepository.save(user);
			Notification.show("Benutzer erfolgreich erstellt!", 3000, Notification.Position.TOP_CENTER);
			dialog.close();
			refreshGrid();
		});

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button cancelButton = new Button("Abbrechen", e -> dialog.close());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		HorizontalLayout buttons = new HorizontalLayout(cancelButton, saveButton);
		VerticalLayout formLayout = new VerticalLayout(usernameField, emailField, telefonField);

		dialog.add(formLayout, buttons);
		dialog.open();
	}

	private void refreshGrid() {
		userGrid.setItems(userRepository.findAll());
	}

}
