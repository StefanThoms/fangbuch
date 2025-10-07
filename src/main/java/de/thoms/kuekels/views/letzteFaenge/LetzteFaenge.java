package de.thoms.kuekels.views.letzteFaenge;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.thoms.kuekels.data.Fang;
import de.thoms.kuekels.data.FangRepository;
import de.thoms.kuekels.data.User;
import de.thoms.kuekels.data.UserRepository;

@PageTitle("Letzte Fänge")
@Route("letztefaenge")

@AnonymousAllowed
public class LetzteFaenge extends VerticalLayout {
	
	public LetzteFaenge(FangRepository fangRepository, UserRepository userRepository) {
		H2 header = new H2("Fänge der letzten 14 Tage");
		Grid<Fang> fangGrid = new Grid<>(Fang.class);
		DateTimeFormatter germanFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.GERMAN);
	     
		
		LocalDate end = LocalDate.now();
		LocalDate start = end.minusDays(14);
		List<Fang> ergebnisse = fangRepository.findAllByDatumBetweenNative(start, end);
		fangGrid.setColumns();
		fangGrid.addColumn(list -> list.getDatum().format(germanFormatter)).setHeader("Datum").setSortable(true);
		fangGrid.addColumn(list -> {
		    Optional<User> u = userRepository.findByUsername(list.getUser());
		    return u.map(User::getName).orElse(list.getUser());
		}).setHeader("Benutzer");
		fangGrid.addColumn(Fang::getFischart).setHeader("Fischart");
		//fangGrid.addColumn(list -> (int) list.getMenge()).setHeader("Menge").setSortable(true);
		//fangGrid.addColumn(list -> String.format("%d G" ,  	(int) list.getGewicht())).setHeader("Gewicht").setSortable(true);
		//fangGrid.addColumn(list -> String.format("%d CM" ,	(int) list.getLaenge())).setHeader("Länge").setSortable(true);
		
		fangGrid.setItems(ergebnisse);
		fangGrid.addItemDoubleClickListener(e -> openDialog(e));
		add(header,fangGrid);
		
	}

	private Object openDialog(ItemDoubleClickEvent<Fang> e) {
		Fang fang = e.getItem();
		Dialog dialog = new Dialog();
		VerticalLayout v1 = new VerticalLayout();
		VerticalLayout v2 = new VerticalLayout();
		VerticalLayout v3 = new VerticalLayout();
		
		Button ok = new Button("OK");
		
		ok.addClickListener( x -> { dialog.close(); });
		
		TextField fischart = new TextField("Fischart");
		NumberField laenge = new NumberField("Länge");
		NumberField gewicht = new NumberField("Gewicht");
		NumberField anzahl = new NumberField("Anzahl");
		Span span = new Span();
		
		fischart.setValue(fang.getFischart());
		laenge.setValue( fang.getLaenge() );
		gewicht.setValue(fang.getGewicht());
		anzahl.setValue(fang.getMenge());
		
		try {
			span.getElement().setProperty("innerHTML", fang.getBemerkung().replace("\n", "<br>"));
		} catch (Exception e1) {
			// Wenn kein Komentar eingegeben ist, würde ein Fehler generiert werden. Das wird jetzt vermieden!
		}
		
		v1.add(fischart,laenge,gewicht,anzahl);
		v2.add(span);
		v3.add(ok);
		dialog.add(v1,v2,v3);
		dialog.open();
		
		return null;
	}

}
