package de.thoms.kuekels.views.demo;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.List;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.thoms.kuekels.data.Fang;
import de.thoms.kuekels.data.FangRepository;
import de.thoms.kuekels.data.User;
import de.thoms.kuekels.data.UserRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import de.thoms.kuekels.data.Fang;
import de.thoms.kuekels.data.FangRepository;


@PageTitle("Demo")
@Route("demo")
@PermitAll
@RolesAllowed("ADMIN")
public class Demo extends VerticalLayout{
	
	public class test{


		String Nachname;
		String Geburtsort;
		String Wohnort;
		String Vorname;
		
		public test(){
			this.Nachname = "leer";
			this.Vorname = "leer";
			this.Geburtsort = "leer";
			this.Wohnort = "leer";
		}
		
		public test(String sVorname , String sNachname , String sWohnort, String sGeburtsort){
			this.Nachname = sNachname;
			this.Vorname = sVorname;
			this.Geburtsort = sGeburtsort;
			this.Wohnort = sWohnort;
		}
		
		
		
		public String getVorname() {
			return Vorname;
		}

		public void setVorname(String vorname) {
			Vorname = vorname;
		}

		public String getNachname() {
			return Nachname;
		}

		public void setNachname(String nachname) {
			Nachname = nachname;
		}

		public String getGeburtsort() {
			return Geburtsort;
		}

		public void setGeburtsort(String geburtsort) {
			Geburtsort = geburtsort;
		}

		public String getWohnort() {
			return Wohnort;
		}

		public void setWohnort(String wohnort) {
			Wohnort = wohnort;
		}

			
	}
	
	public Demo(UserRepository userRepository , FangRepository fangRepository) {

		ArrayList<test> al = new ArrayList();
		
		DateTimeFormatter germanFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.GERMAN);
	     
		
		LocalDate end = LocalDate.now();
		LocalDate start = end.minusDays(14);
		List<Fang> ergebnisse = fangRepository.findAllByDatumBetweenNative(start, end);
		
		//ArrayList<Fang> = (ArrayList<Fang>) fangRepository.findAllByDatumBetweenNative(start, end);
		
		ergebnisse.forEach((n) -> {
			String sName	=	n.getUser();
			String sUsername = "Kenne ich noch nicht!";
			Optional<User> u =	userRepository.findByUsername(sName);
			if (u.isPresent()) {
				User x = u.get();
				sUsername	=	x.getName();
			}
			al.add(new test(n.getFischart() , n.getUser() , sUsername , ""));
		});
		
		//al.add(new test("Klaudia","Müller","Grünau","Lübeck"));

		Grid<test> grid = new Grid<>(test.class);
		
		grid.setItems(al);
		H1 h1 = new H1("Demo");
		
		add(h1,grid);

		

	}

}
