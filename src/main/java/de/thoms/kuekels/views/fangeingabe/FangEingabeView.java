package de.thoms.kuekels.views.fangeingabe;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import de.thoms.kuekels.GlobaleWerte;
import de.thoms.kuekels.data.Fang;
import de.thoms.kuekels.data.FangRepository;
import de.thoms.kuekels.data.Fischart;
import de.thoms.kuekels.data.FischartRepository;
import de.thoms.kuekels.data.User;
import de.thoms.kuekels.data.UserRepository;
import jakarta.annotation.security.PermitAll;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Fang Eingabe")
@Route("fangeingabe")
@Menu(order = 1, icon = LineAwesomeIconUrl.ACCESSIBLE_ICON)
@PermitAll
public class FangEingabeView extends VerticalLayout {

	private final FangRepository fangRepository;
	private Fang neuerFang;

	public FangEingabeView(UserRepository userRepository, FangRepository fangRepository,
			FischartRepository fischartRepository) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		List<Fischart> fischarten = fischartRepository.findAllOrderedByName();
		this.fangRepository = fangRepository;

		DatePicker datePicker = new DatePicker("Fangtag");
		datePicker.setValue(LocalDate.now());
		datePicker.setLocale(Locale.GERMANY);

		ComboBox<Fischart> auswahl = new ComboBox<>("Fischart");
		auswahl.setItemLabelGenerator(Fischart::getName); // Anzeige-Text
		auswahl.setItems(fischarten);

		NumberField mengeField = new NumberField("Anzahl der Fische");
		NumberField gewichtField = new NumberField("Gewicht in Gramm");
		NumberField laengeField = new NumberField("Länge (optional)");
		Checkbox oitem = new Checkbox("Fisch released");

		TextArea ta = new TextArea();

		ta.setWidth("100%");

		MemoryBuffer buffer = new MemoryBuffer();
		Upload upload = new Upload(buffer);
		upload.setAcceptedFileTypes("image/jpeg", "image/png");
		upload.getElement().setAttribute("capture", "environment");

		neuerFang = new Fang();
		Optional<User> userOpt = userRepository.findByUsername(username);
		if (userOpt.isEmpty()) {
			add(new Span("Benutzer nicht gefunden."));
			return;
		}
		User user = userOpt.get();

		upload.addSucceededListener(event -> {
			InputStream inputStream = buffer.getInputStream();
			try {
				byte[] imageBytes = inputStream.readAllBytes();
				neuerFang.setBild(imageBytes);
				Notification.show("Bild erfolgreich hochgeladen!");
			} catch (IOException e) {
				Notification.show("Fehler beim Speichern des Bildes.");
			}
		});

		Button speichernButton = new Button("Speichern");
		speichernButton.addClickListener(e -> {
			if (user.isGps())
				UI.getCurrent().getPage()
						.executeJs("navigator.geolocation.getCurrentPosition(function(pos) {"
								+ "  $0.$server.receivePosition(pos.coords.latitude, pos.coords.longitude);" + "});",
								getElement());

			LocalDate datum = datePicker.getValue();

			Fischart gewählt = auswahl.getValue();
			if (gewählt.getId() == 9) {
				mengeField.setValue((double) 1);
				gewichtField.setValue((double) 0);
			}

			if (!laengeField.getOptionalValue().isEmpty()) {
				neuerFang.setLaenge(laengeField.getValue());
				if (gewichtField.getOptionalValue().isEmpty()) {
					gewichtField.setValue((double) 0);
				}
				if(laengeField.getValue() > 116 && gewählt.getName().equals("Hecht"))
					Notification.show("Der Hecht ist sehr groß!");

			}

			if (mengeField.getOptionalValue().isEmpty()) {
				Notification.show("Bitte die Menge eintragen!");
			} else {
				if (gewichtField.getOptionalValue().isEmpty()) {
					Notification.show("Bitte das Gewicht eintragen!");
				}

				neuerFang.setUser(username);
				neuerFang.setDatum(datum);
				// Notification.show("Gewähltes Datum");
				neuerFang.setFischart(gewählt.getName());
				neuerFang.setMenge(mengeField.getValue());
				neuerFang.setGewicht(gewichtField.getValue());
				neuerFang.setReleased(oitem.getValue());
				neuerFang.setBemerkung(ta.getValue());
				fangRepository.save(neuerFang);

				Notification.show("Fisch gespeichert!");

				auswahl.setValue(null);
				mengeField.setValue(null);
				gewichtField.setValue(null);
				laengeField.setValue(null);
				ta.setValue("");
				oitem.setValue(false);
			}

		});

		FormLayout formLayout = new FormLayout();
		formLayout.add(datePicker, auswahl, mengeField, gewichtField, laengeField, oitem);
		formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("500px", 2));

		// Optional: volle Breite für Eingabefelder
		formLayout.setColspan(datePicker, 2);
		formLayout.setColspan(auswahl, 2);

		// Hauptlayout
		VerticalLayout layout = new VerticalLayout();
		layout.add(new H2("Neuen Fisch erfassen"), formLayout, ta, upload, speichernButton);
		layout.setSizeFull();
		layout.setAlignItems(FlexComponent.Alignment.CENTER);
		layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

		add(layout);

		setSizeFull();
		setJustifyContentMode(JustifyContentMode.CENTER);
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		getStyle().set("text-align", "center");
	}

	@ClientCallable
	private void receivePosition(double latitude, double longitude) {
		/*
		 * System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
		 * 
		 * System.out.println(" LAT -> " + latitude + " >= " + GlobaleWerte.sueddBreite
		 * + " & " + latitude + " <= " + GlobaleWerte.nordBreite);
		 * System.out.println(" LON -> " + longitude + " >= " + GlobaleWerte.westLaenge
		 * + " & " + longitude + " <= " + GlobaleWerte.ostLaenge);
		 */

		if ((latitude >= GlobaleWerte.sueddBreite & latitude <= GlobaleWerte.nordBreite)
				& (longitude >= GlobaleWerte.westLaenge & longitude <= GlobaleWerte.ostLaenge)) {
			neuerFang.setLat(latitude);
			neuerFang.setLon(longitude);
			
		} else {
			Notification.show("Die GPX-Daten sind nicht am See und werden nicht verwendet!");
		}
		fangRepository.save(neuerFang);

	}

}
