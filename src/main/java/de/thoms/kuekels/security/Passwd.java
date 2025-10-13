package de.thoms.kuekels.security;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@Route("selfeservice/passwoerter-zuruecksetzen")
@PageTitle("Passwörter zurücksetzen")
public class Passwd extends VerticalLayout {
	
	
	Passwd(){
		System.out.println("Das war schon mal gut!");
	}

}
