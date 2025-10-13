package de.thoms.kuekels.views.alleFaenge;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import de.thoms.kuekels.data.Fang;
import de.thoms.kuekels.data.FangRepository;
import de.thoms.kuekels.data.FangStatistikDTO;
import de.thoms.kuekels.data.Fischart;
import de.thoms.kuekels.data.FischartRepository;
import de.thoms.kuekels.data.User;
import de.thoms.kuekels.data.UserRepository;
import de.thoms.kuekels.security.AuthenticatedUser;
import jakarta.annotation.security.PermitAll;

@PageTitle("Alle Fänge im See")
@Route("")

@AnonymousAllowed

public class AlleFaengeView extends VerticalLayout {
	private LocalDate startDatum;
	private LocalDate stopDatum;
	private String sUser;
	private String sVerein;

	private int iTag;
	private int iMonat;
	private int iJahr;
	private List<Fischart> fischarten;
	private FangRepository fangRepository;
	private List<FangStatistikDTO> statistikListe;
	private Grid<FangStatistikDTO> grid;
	private AuthenticatedUser authenticatedUser;
	private FischartRepository fischartRepository;
	private Optional<User> maybeUser;

	public AlleFaengeView(FangRepository fangRepository, FischartRepository fischartRepository,
			AuthenticatedUser authenticatedUser) {

		this.fangRepository = fangRepository;
		this.fischartRepository = fischartRepository;
		this.fischarten = fischartRepository.findAllOrderedByName();
		this.authenticatedUser = authenticatedUser;

		maybeUser = authenticatedUser.get();
		if (maybeUser.isPresent()) {
			User user = maybeUser.get();
			sUser = user.getUsername();
		}
		
		sUser = "*";
		initDatum();

		grid = new Grid<>(FangStatistikDTO.class);
		grid.setColumns("name", "menge"); // optional sortierbar machen
		grid.getColumnByKey("name").setHeader("Fischart");
		grid.getColumnByKey("menge").setTextAlign(ColumnTextAlign.END);
		grid.addColumn(product -> {
			Long weight = product.getGewicht(); // in Gramm
			if (product.getName().equalsIgnoreCase("Schneider")) {
				return "";
			} else {
				if (weight >= 1000) {
					return (weight / 1000.0) + " kg";
				} else {
					return weight + " g";
				}
			}

		}).setHeader("Gewicht").setTextAlign(ColumnTextAlign.END);

		H2 header = new H2("Alle Fänge im See");
		Refresh();

		header.addClassNames(Margin.Vertical.AUTO);
		this.setHeight("800px");
		add(header, FilterLeiste(), grid);

	}

	private void Refresh() {

		Optional<Long> loMenge;
		Optional<Long> loGewicht;

		statistikListe = new ArrayList<>();
		
		for (Fischart i : fischarten) {
			if (sUser == "*") {
				loMenge = fangRepository.findAlleMengeByFischartDatum(i.getName(), startDatum,stopDatum);
				loGewicht = fangRepository.findAlleGewichtByFischartDatum(i.getName(), startDatum,stopDatum);
			}
			else {
				loMenge = fangRepository.findAlleMengeByFischartUserDatum(i.getName(), startDatum,stopDatum, sUser);
				loGewicht = fangRepository.findAlleGewichtByFischartUserDatum(i.getName(), startDatum,stopDatum, sUser);
			}
			long lMenge = loMenge.orElse(0L);

			long lGewicht = loGewicht.orElse(0L);
			statistikListe.add(new FangStatistikDTO(i.getName(), lMenge, lGewicht));

		}
		grid.setItems(statistikListe);
	}
	

	private void JahrEinstellen(int i) {

		iJahr = i;
		iMonat = 1;
		startDatum = LocalDate.of(i++, iMonat, 1);
		stopDatum = LocalDate.of(i, iMonat, 1).minusDays(1);
	
	}

	private void MonatEinstellen(int i) {

		iMonat = i;
		startDatum = LocalDate.of(iJahr, i++, 1);
		stopDatum = LocalDate.of(iJahr, i, 1).minusDays(1);

	}

	private void initDatum() {
		iTag = LocalDateTime.now().getDayOfMonth();
		iMonat = LocalDateTime.now().getMonth().getValue();
		iJahr = LocalDateTime.now().getYear();
		
		startDatum = LocalDate.of( iJahr,1,1);
		stopDatum = LocalDate.of( iJahr,12,31);

	}

	private int forLoop(String[] sMonate, Object object) {
		for (int index = 0; index < sMonate.length; index++) {
			if (sMonate[index] == object) {
				return index;
			}
		}
		return -1;
	}

	private HorizontalLayout FilterLeiste() {

		HorizontalLayout filter = new HorizontalLayout();
		ComboBox<String> optionen = new ComboBox<String>("Optionen");
		ComboBox<String> monat = new ComboBox<String>("Monat");
		ComboBox<Integer> jahr = new ComboBox<Integer>("Jahr");
		LocalDate heute = LocalDate.now();

		Integer iDatum[] = { -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		String sMonate[] = { "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September",
				"Oktober", "November", "Dezember", "Alle" };
		String sOptionen[] = { "Meine Einträge",  "Alle" };	//{ "Meine Einträge", "Mein Verein", "Alle" };
		if (maybeUser.isPresent()) {
			optionen.setItems(sOptionen);
			optionen.setVisible(true);
		} else {
			optionen.setItems("Alle");
			optionen.setVisible(false);
		}

		optionen.setValue("Alle");
		monat.setItems(sMonate);
		monat.setValue("Alle");

		for (int i = 10; i >= 0; i--) {

			iDatum[i] = heute.getYear() - i;
		}
		iDatum[10] = -1;
		jahr.setItems(iDatum);
		jahr.setValue(Integer.valueOf(String.valueOf(heute.getYear())));
		

		optionen.addValueChangeListener(e -> {
			if (optionen.getValue() == "Alle") {
				sUser = "*";

			} else {
				if (maybeUser.isPresent()) {
					User user = maybeUser.get();
					sUser = user.getUsername();
					
				}
			}
		
			Refresh();

		});

		monat.addValueChangeListener(e -> {

			if (monat.getValue() == "Alle") {
				JahrEinstellen((int) jahr.getValue());
			} else {
				MonatEinstellen(forLoop(sMonate, monat.getValue()) + 1);
			}
			//System.out.println("User -> " + sUser );
			Refresh();
		});
		jahr.addValueChangeListener(e -> {

			if ((int) jahr.getValue() == -1) {

				startDatum = LocalDate.ofYearDay(2000, 1);
				stopDatum = startDatum.plusYears(100).minusDays(1);
			} else {
				JahrEinstellen((int) jahr.getValue());
			}
			monat.setValue("Alle");
			Refresh();
		});

		filter.add(monat, jahr, optionen);
		return filter;

	}

}
