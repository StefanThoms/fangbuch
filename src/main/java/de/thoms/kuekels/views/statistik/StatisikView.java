package de.thoms.kuekels.views.statistik;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import de.thoms.kuekels.data.FangRepository;
import de.thoms.kuekels.data.FangStatistikDTO;
import de.thoms.kuekels.data.Fischart;
import de.thoms.kuekels.data.FischartRepository;
import de.thoms.kuekels.data.StatistikJahr;
import de.thoms.kuekels.data.UserRepository;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Statistik")
@Route("Statistik")
@Menu(order = 2, icon = LineAwesomeIconUrl.FISH_SOLID)
@RolesAllowed({"STATISTIK" , "ADMIN"})


public class StatisikView extends VerticalLayout {

	private List<StatistikJahr> statistikListe;
	private List<Fischart> fischarten;
	
	public StatisikView(UserRepository userRepository , FangRepository fangrepository,FischartRepository fischartRepository) {
		this.fischarten 		= fischartRepository.findAllOrderedByName();
		int aktuellesJahr = Year.now().getValue();
		statistikListe = new ArrayList<>();
					
		Optional<Long> ol ;
		long[] l = { 0 , 0, 0 };
		int i = 0;
					
		for (Fischart sFisch : fischarten) {
			i = 0;
			
			for (int iJahr = aktuellesJahr - 2 ; iJahr <= aktuellesJahr ; iJahr ++ , i++ ) {
				ol = fangrepository.findAlleMengeByFischartandJahr(sFisch.getName(), iJahr );
				//System.out.println(sFisch.getName() + " >> " +  iJahr + ">>" + ol);
				l[i] = ol.orElse((long) 0);
				}
			statistikListe.add(new StatistikJahr(sFisch.getName(), l[0] , l[1] , l[2] ) );
		}	
		
		Grid<StatistikJahr> grid = new Grid<>(StatistikJahr.class,false);
		
		grid.addColumn(StatistikJahr::getName).setHeader("Name");
        grid.addColumn(StatistikJahr::getLetztesJahr).setHeader(String.valueOf(aktuellesJahr - 2));
        grid.addColumn(StatistikJahr::getAktuellesJahr).setHeader(String.valueOf(aktuellesJahr -1));
        grid.addColumn(StatistikJahr::getNaechstesJahr).setHeader(String.valueOf(aktuellesJahr));
		
		grid.setItems(statistikListe);
	    this.setHeight("700px");
		
		add(grid);
	}
	
	

}

