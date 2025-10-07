package de.thoms.kuekels.views.login;

import java.util.UUID;

import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.thoms.kuekels.security.AuthenticatedUser;
import de.thoms.kuekels.services.EmailService;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    private final EmailService emailService ;

    public LoginView(AuthenticatedUser authenticatedUser , EmailService emailService ) {
    	this.emailService = emailService;
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Fangbuch");
        i18n.getHeader().setDescription("Das Erstpasswort ist 'angeln'");
        
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(true);
        addForgotPasswordListener(event -> {
        //getUI().ifPresent(ui -> ui.navigate("forgot-password"))
        	SendPasswordToken();
            
    	});
        setOpened(true);
    }
    

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("allefaenge");
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
    public void SendPasswordToken() {
    	
    	
    	emailService.sendMail("u9572914646@gmail.com", 
                "Hallo aus Vaadin", 
                "Das ist eine Testmail");
    	 
    	
    	
    	Notification.show("Passwort erfolgreich geändert");
    }

}
