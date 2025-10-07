package de.thoms.kuekels.views.meldungen;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.thoms.ThomsDialog01;
import de.thoms.kuekels.data.News;
import de.thoms.kuekels.data.NewsRepository;
import de.thoms.kuekels.data.User;
import de.thoms.kuekels.data.UserRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Hilfe")
@Route("Hilfe")
@Menu(order = 7, icon = LineAwesomeIconUrl.LIST_SOLID)
@AnonymousAllowed
public class Meldungen extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NewsRepository newsRepository;
	private UserRepository userRepository;

 
    public Meldungen(NewsRepository newsRepository, UserRepository userRepository) {
    	this.newsRepository = newsRepository;
    	this.userRepository = userRepository;
    	String[] sTest = {"Zeile1","Zeile 2","Zeile 3 ganz unten"};
    	
		Button showDialogButton = new Button("Dialog öffnen", e -> {
		    ThomsDialog01 meinDialog = new ThomsDialog01();
		    meinDialog.setContext(sTest);
		    meinDialog.setTop("ÜBERSCHRIFT");
		    Dialog dialog = meinDialog.erzeugeDialog();		    
		    dialog.open();
		});

		//add(showDialogButton);
		
		
		Button openDialogButton = new Button("HTML-Seite im Dialog öffnen", event -> {
            // Dialog erstellen
            Dialog dialog = new Dialog();
            
            // iframe erstellen, um die HTML-Seite einzubetten
            IFrame iframe = new IFrame();
            iframe.setSrc("/test.html");  // Deine statische HTML-Seite
            iframe.setWidth("100%");  // Passe die Breite und Höhe an
            iframe.setHeight("400px");

            dialog.add(iframe);
            dialog.open();
        });
		
		Button openHtmlPage = new Button("Termine öffnen", e -> {
		    getUI().ifPresent(ui -> ui.getPage().open("/termine.html"));
		});
		
		//add(openHtmlPage);

        //add(openDialogButton);
		
    	
    	Grid<News> newsGrid = new Grid<>(News.class, false);
    	List <News> list = newsRepository.findAll();
    	List <User> melder = userRepository.findAll();
    	Button createNew = new Button("Neuer Eintrag");
    	
    	  if (hasRole("ADMIN")) {
    		  createNew.isVisible();
          } else {
              // Menüpunkt ganz ausblenden
        	  createNew.setVisible(false);
          }
    	
    	
    	createNew.addClickListener(e -> {
    		createNews(melder);
    		newsGrid.setItems(newsRepository.findAll());
        });

        // Löschen-Button
        Button delete = new Button("", VaadinIcon.TRASH.create());
        delete.getStyle().set("color", "red");
        delete.addClickListener(e -> {
            Notification.show("Eintrag gelöscht.");
        });
		
    	DateTimeFormatter germanFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.GERMAN);
        addClassName("feed-view");
        setSizeFull();
        
        add(createNew);
        newsGrid.setHeight("100%");
        newsGrid.addColumn(datum ->  datum.getDatum().format(germanFormatter)).setHeader("Datum").setSortable(true);
        newsGrid.addColumn(schlagwort ->  schlagwort.getSchlagwort()).setHeader("Schlagwort").setSortable(true);
        //newsGrid.addColumn(text ->  text.getText()).setHeader("Text").setSortable(true);
        
        newsGrid.addItemClickListener(event -> {
            News news = event.getItem(); 
            boolean bAdmin = hasRole("ADMIN")?true:false;
            showNewsDialog(news , bAdmin);     
        });
        newsGrid.setItems(list);
        add(newsGrid);
      
    }
    
    private void createNews(List<User> melder) {
    	Dialog dialog = new Dialog();
		
        dialog.setHeaderTitle("News bearbeiten");
        VerticalLayout dialogLayout = new VerticalLayout();
        HorizontalLayout h1 = new HorizontalLayout();
        HorizontalLayout h2 = new HorizontalLayout();
        HorizontalLayout h3 = new HorizontalLayout();
                   
        DatePicker datePicker = new DatePicker("Posting Datum");
        datePicker.setValue(LocalDate.now());
        datePicker.setLocale(Locale.GERMANY);
        
        
        ComboBox<User> auswahl = new ComboBox<>("Melder");
        auswahl.setItemLabelGenerator(User::getUsername); // Anzeige-Text
        auswahl.setItems(melder);
        
        TextField meldung = new TextField("Meldung");
        TextArea text = new TextArea("HTML-Eingabe");
        text.setSizeFull();
        meldung.setTooltipText("Die Überschrift");
        meldung.setMaxLength(20);
        
        h1.add(datePicker,auswahl);
        h2.add(meldung);
        h3.add(text);
        h3.setWidth("100%");
        dialogLayout.add(h1,h2,h3);

        dialog.add(dialogLayout);
        dialog.open();
        
        Button saveButton = new Button("Speichern");
        Button cancelButton = new Button("Cancel", e1 -> dialog.close());
        saveButton.addClickListener(e3 -> {
        	News n = new News();
    		
    		n.setSchlagwort("Hallo");
    		n.setText("Das ist ein Text");        		
    		User x = auswahl.getValue();        		
       		n.setUser(x.getUsername());       		
       		n.setDatum(datePicker.getValue());
        	
        	n.setSchlagwort(meldung.getValue());
        	n.setText(text.getValue());
        	
        	newsRepository.save(n);
        	
            Notification.show("News gespeichert!");
            dialog.close();

        });

        dialog.open();
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
        add(dialog);
        
	}

	public void showNewsDialog(News news , boolean editmode) {
	
			
    	Dialog newsDlg = new Dialog();
    	newsDlg.setHeight("80%");
    	newsDlg.setWidth("80%");
    	VerticalLayout vl1 = new VerticalLayout();
    	VerticalLayout vl2 = new VerticalLayout();
    	HorizontalLayout vl3 = new HorizontalLayout();
    	Button save = new Button();
    	Button cancel = new Button();
    	
    	TextField schlagwort = new TextField("Schlagwort");
    	TextArea text = new TextArea("Text");
    	
    	
    	vl3.setSpacing(true);
    	vl3.setPadding(true);
    	vl3.getStyle().set("margin-top", "1rem");
    	
        // Löschen-Button
        Button delete = new Button("", VaadinIcon.TRASH.create());
        delete.getStyle().set("color", "red");
        delete.addClickListener(e -> {
        	newsRepository.delete(news);
            Notification.show("Eintrag gelöscht.");
    	
        });
    	save.setText("Speichern");
    	cancel.setText("Abbruch");
    	
    	save.addClickListener(e -> {
    		        	
        	news.setSchlagwort(schlagwort.getValue());
        	news.setText(text.getValue());
    		
    		saveNews(news);
    		newsDlg.close();	
    	});
    	cancel.addClickListener(event -> { newsDlg.close();});
    	
    	if (editmode) {
    		
        	schlagwort.setValue(news.getSchlagwort());
        	text.setValue(news.getText());
        	vl1.add(schlagwort,text);
        	//vl2.add(text);
        	save.setVisible(true);
        	delete.setVisible(true);
    	}
    	else {
    		Span span = new Span();
        	Span voschlagwort = new Span();
        	span.getElement().setProperty("innerHTML","<h1>" + news.getSchlagwort() + "</h1>");
        	span.getElement().setProperty("innerHTML",news.getText().replace("\n","<br>"));
        	save.setVisible(false);
        	delete.setVisible(false);
        	vl1.add(voschlagwort,span);
        	//vl2.add(span);
        	
    	}
    	
    	
    	vl3.add(cancel,save , delete);
    	
    	//vl3.setSizeFull();
    	//vl3.setAlignItems(FlexComponent.Alignment.CENTER);
    	//vl3.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    	
    	
    	vl1.setHeight("60%");
    	vl1.setWidth("80%");
    	vl1.setPadding(true);
    	
    	newsDlg.add(vl3,vl1);
    	
    	newsDlg.open();
    }
    
    private Object saveNews(News news) {
    	newsRepository.save(news);
		
		return null;
		
	}

	public boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(r -> r.equals("ROLE_" + role));
    }

}