package de.thoms.kuekels.views.rangliste;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.thoms.kuekels.data.FangRepository;
import de.thoms.kuekels.data.FischartRepository;
import de.thoms.kuekels.data.UserRepository;



public class Rangliste extends VerticalLayout{
	
	public Rangliste(FangRepository fangRepository,FischartRepository fischartRepository, UserRepository userRepository) {
		
	}

}
