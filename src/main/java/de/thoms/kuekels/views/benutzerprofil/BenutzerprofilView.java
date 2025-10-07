package de.thoms.kuekels.views.benutzerprofil;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import de.thoms.kuekels.data.Fischart;
import de.thoms.kuekels.data.User;
import de.thoms.kuekels.data.UserRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Benutzerprofil")
@Route("benutzerprofil")
@Menu(order = 0, icon = LineAwesomeIconUrl.KEY_SOLID)
@PermitAll
public class BenutzerprofilView extends VerticalLayout {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private byte[] imageBytes;

	public BenutzerprofilView(UserRepository userRepository, PasswordEncoder passwordEncoder) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		imageBytes = null;

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> userOpt = userRepository.findByUsername(username);

		if (userOpt.isEmpty()) {
			add(new Span("Benutzer nicht gefunden."));
			return;
		}

		User user = userOpt.get();

		HorizontalLayout hl01 = new HorizontalLayout();
		HorizontalLayout hl02 = new HorizontalLayout();
		HorizontalLayout hl03 = new HorizontalLayout();
		HorizontalLayout hl04 = new HorizontalLayout();
		TextField nameField = new TextField("Voller Name");
		TextField userName = new TextField("Anmeldename");
		Checkbox gps = new Checkbox();
		gps.setLabel("GPS aktivieren");
		gps.setValue(user.isGps());

		gps.addClickListener(x -> {
			if (gps.getValue())
				UI.getCurrent().getPage()
						.executeJs("navigator.geolocation.getCurrentPosition(function(pos) {"
								+ "  $0.$server.receivePosition(pos.coords.latitude, pos.coords.longitude);" + "});",
								getElement());
		});

		userName.setValue(user.getUsername());
		userName.setEnabled(false);

		nameField.setValue(user.getName() != null ? user.getName() : "");

		TextField emailField = new TextField("E-Mail");
		emailField.setValue(user.getEmail() != null ? user.getEmail() : "");

		TextField telefonField = new TextField("Telefon");
		telefonField.setValue(user.getTelefon() != null ? user.getTelefon() : "");

		String sVerein = user.getVerein() != null ? user.getVerein() : "";

		String[] sItems = { "Kükels", "Wittenborn", "Mözen" };
		ComboBox<String> auswahl = new ComboBox("Verein");
		auswahl.setItems(sItems);

		auswahl.setValue(sVerein);

		// Profilbild hochladen

		MemoryBuffer buffer = new MemoryBuffer();
		Upload upload = new Upload(buffer);
		upload.setAcceptedFileTypes("image/jpeg", "image/png");
		upload.getElement().setAttribute("capture", "environment");

		upload.addSucceededListener(x -> {
			InputStream inputStream = buffer.getInputStream();
			try {
				imageBytes = inputStream.readAllBytes();
				Notification.show("Bild erfolgreich hochgeladen!");
			} catch (IOException e) {
				Notification.show("Fehler beim Speichern des Bildes.");
			}
		});

		// Ende Profilbild

		hl01.add(nameField, auswahl);
		hl02.add(emailField, telefonField);
		hl04.add(gps);
		Button saveButton = new Button("Speichern");
		Span status = new Span();

		saveButton.addClickListener(e -> {
			user.setName(nameField.getValue());
			user.setEmail(emailField.getValue());
			user.setTelefon(telefonField.getValue());
			user.setVerein(auswahl.getValue());
			user.setGps(gps.getValue());
			try {
				if (!imageBytes.equals(null))
					user.setProfilePicture(imageBytes);
				else
					user.setProfilePicture(null);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			userRepository.save(user);

			status.setText("Profil wurde gespeichert.");
			status.getStyle().set("color", "green");
		});

		Button openDialogButton = new Button("Passwort ändern");

		openDialogButton.addClickListener(e -> {
			Dialog dialog = new Dialog();

			PasswordField currentPassword = new PasswordField("Aktuelles Passwort");
			PasswordField newPassword = new PasswordField("Neues Passwort");
			PasswordField confirmPassword = new PasswordField("Neues Passwort bestätigen");

			// Button changeButton = new Button("Passwort ändern");

			Span feedback = new Span();
			feedback.getStyle().set("color", "red");

			Button pswAendern = new Button("Passwort ändern", event -> {

				if (userOpt.isPresent()) {
					// User user = userOpt.get();

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
				dialog.close();
			});

			VerticalLayout dialogLayout = new VerticalLayout(currentPassword, newPassword, confirmPassword, pswAendern);

			dialog.add(dialogLayout);
			dialog.open();
		});

		hl03.add(upload, saveButton); // , openDialogButton);

		add(userName, hl01, hl02, hl04, hl03, status);
		setAlignItems(Alignment.CENTER);
		setPadding(true);

		setSizeFull();
		setJustifyContentMode(JustifyContentMode.CENTER);
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		getStyle().set("text-align", "center");
	}

}
