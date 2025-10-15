package de.thoms.kuekels.views.login;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

import javax.swing.text.html.HTML;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.thoms.kuekels.data.User;
import de.thoms.kuekels.data.UserRepository;
import de.thoms.kuekels.security.AuthenticatedUser;
import de.thoms.kuekels.services.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {


@Autowired
private PasswordEncoder passwordEncoder;

    private final AuthenticatedUser authenticatedUser;
    private final EmailService emailService ;

    public LoginView(AuthenticatedUser authenticatedUser ,UserRepository userRepository , EmailService emailService ) {
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
        
		/*
		 * addLoginListener(event -> { String username = event.getUsername();
		 * //System.out.println(username); });
		 */
        
        addForgotPasswordListener(event -> {
        
        	SendPasswordToken(userRepository);
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
    public void SendPasswordToken(UserRepository userRepository) {
    	
    	Dialog dialog = new Dialog();
    	HorizontalLayout hl = new HorizontalLayout();
    	VerticalLayout vl = new VerticalLayout();
    	String sText = "Trag deine e-mail ein.Wenn es die im System gibt, wird das Passwort auf ein zufälliges Passwort zurückgesetzt und du erhälst es per Mail.";
    	//Html html = new Html(sText);
    	
    	TextArea textArea = new TextArea();
    	textArea.setValue(sText);
    	EmailField email = new EmailField("E-Mail Adresse");
    	email.setErrorMessage("Bitte eine richtige EMail eintragen!");
    	Button ok = new Button("Passwort an E-Mail senden");
    	
    	Button cancel = new Button("Nein doch nicht");
    	
    	
    	ok.addClickListener(e -> {
    		String smail = email.getValue();
    		
    	    Optional<User> optionalUser = userRepository.findByEmail(smail);

    	    if (optionalUser.isEmpty()) {
    	        Notification.show("Kein Benutzer mit dieser E-Mail gefunden!");
    	        return;
    	    }
    	    User user = optionalUser.get();
    	    // Zufälliges Passwort generieren
    	    String newPassword = generateRandomPassword();

    	    // Passwort verschlüsseln (z. B. mit BCrypt)
    	    user.setHashedPassword(passwordEncoder.encode(newPassword));
    	    userRepository.save(user);

    		
    		if( smail.isBlank()) {
    			Notification.show("Bitte e-mail-Adresse eintragen!");
    		}
    		if(email.isInvalid()) {
    			Notification.show("Das ist keine richtige Adresse!");
    		}else {
    			
    			emailService.sendMail(email.getValue(),
    					 "Passwort im Fangbuch zurückgesetzt", "Das neue Passwort ist : [" + newPassword + "]" );
    			dialog.close();
    			Notification.show("Passwort wurde an die e-mail Adresse gesendet!");
    		}
    		
    	});
    	cancel.addClickListener(e -> {
    		dialog.close();
    	});
    	textArea.setWidth("100%");
    	email.setWidth("100%");
    	hl.add(ok , cancel);
    	vl.add(textArea,email,hl);
    	dialog.add(vl );
    	dialog.open();
    	
    	
    	
		/*
		 * emailService.sendMail("u9572914646@gmail.com",
		 * "Passwort im Fangbuch zurückgesetzt", "Das neue Passwort ist : " );
		 */
    	
    	
    	
    }
    private String generateRandomPassword() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
