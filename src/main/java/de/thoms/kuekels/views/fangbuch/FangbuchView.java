package de.thoms.kuekels.views.fangbuch;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import de.thoms.kuekels.data.Fang;
import de.thoms.kuekels.data.FangRepository;
import de.thoms.kuekels.data.Fischart;
import de.thoms.kuekels.data.FischartRepository;
import de.thoms.kuekels.data.UserRepository;
import jakarta.annotation.security.PermitAll;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Fangbuch")
@Route("meinfangbuch")
@Menu(order = 2, icon = LineAwesomeIconUrl.FISH_SOLID)
@PermitAll
public class FangbuchView extends VerticalLayout {
	private FangRepository fangrepository;
	private List<Fischart> fischarten;
	private String username;
	private Grid<Fang> userGrid;

    public FangbuchView(UserRepository userRepository , FangRepository fangrepository,FischartRepository fischartRepository) {
    	this.fangrepository = fangrepository;
    	
    	username = SecurityContextHolder.getContext().getAuthentication().getName();
    	fischarten = fischartRepository.findAllOrderedByName();
    	
    	Integer iDatum[] = {-1,2023,2024,2025,2026,2027,2028,2029,2030}; 
    	ComboBox datum = new ComboBox();
    	datum.setItems(iDatum);
    	LocalDate heute = LocalDate.now();
    	
    	datum.setValue(String.valueOf(heute.getYear()));
    	
    	
        setSpacing(false);

        H2 header = new H2("Fangbuch");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        //add(header);
        DateTimeFormatter germanFormatter = DateTimeFormatter.ofPattern("dd.LL").withLocale(Locale.GERMAN);
     
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.START);
        getStyle().set("text-align", "left");
        
        userGrid = new Grid<>(Fang.class, false);
        //userGrid.addColumn(list ->  list.getDatum().format(germanFormatter)).setHeader("Datum").setSortable(true);
        userGrid.addColumn(list -> list.getDatum().format(germanFormatter))
        .setHeader("Datum")
        .setSortable(true)
        .setComparator(list -> list.getDatum());
        
        userGrid.addColumn(Fang::getFischart).setHeader("Fischart").setSortable(true);
        userGrid.addColumn(list -> (int) list.getMenge()).setHeader("Menge").setSortable(true);
        userGrid.addColumn(list -> String.format("%d G" ,  	(int) list.getGewicht())).setHeader("Gewicht").setSortable(true);
        userGrid.addColumn(list -> String.format("%d CM" ,	(int) list.getLaenge())).setHeader("Länge").setSortable(true);
        
        userGrid.addColumn(Fang::getReleasedString).setHeader("Released");
        
        userGrid.addComponentColumn(list ->  {
            StreamResource resource = new StreamResource("image.jpg", () -> new ByteArrayInputStream(list.getBild()));
            Image img = new Image(resource, " ");
            img.setHeight("60px");
            img.setWidth("80px");
            return img;
        }).setHeader("Vorschau");
       
        //userGrid.addColumn(if(list -> list.getReleased()) );

        
        //Button
        userGrid.addComponentColumn(list -> {
            HorizontalLayout actions = new HorizontalLayout();

            // Bearbeiten-Button
            Button edit = new Button("", VaadinIcon.EDIT.create());
            edit.addClickListener(e -> {
                openDialog( list  );
                userGrid.setItems(fangrepository.findByUser(username));
                Notification.show("Eintrag geändert.");
            });

            // Löschen-Button
            Button delete = new Button("", VaadinIcon.TRASH.create());
            delete.getStyle().set("color", "red");
            delete.addClickListener(e -> {
            	fangrepository.delete(list); // oder Service
                userGrid.setItems(fangrepository.findByUser(username));
                
                Notification.show("Eintrag gelöscht.");
            });

            actions.add(edit, delete);
            return actions;
        }).setHeader("Aktionen");
        userGrid.addItemDoubleClickListener(e -> {
        	openDialog( e.getItem() );
        	
        	
        	});
      
        List <Fang> list = fangrepository.findByUser(username);
        datum.addClientValidatedEventListener(e -> {
        	LocalDate start = LocalDate.ofYearDay((int) datum.getValue(), 1);
    		LocalDate end = start.plusYears(1).minusDays(1);
        	if((int) datum.getValue() == -1 ) {
        		start = LocalDate.ofYearDay(2000, 1);
        		end = start.plusYears(100).minusDays(1);
        	} 
    		List<Fang> ergebnisse = fangrepository.findAllByDatumBetweenNative(start, end , username);
    		userGrid.setItems(ergebnisse);
    		Notification.show("Hallo " + datum.getValue() + " - " + start.toString() + " - " + end.toString() + " - " + username);
    		
    	});
        userGrid.setItems(list);
       
        add(datum,userGrid);

    }

	private void openDialog( Fang list) {
		
		Dialog dialog = new Dialog();
        
        dialog.setHeaderTitle("Fang bearbeiten");
        VerticalLayout dialogLayout = new VerticalLayout();
        dialog.add(dialogLayout);
        DatePicker datePicker = new DatePicker("Fangtag");
        datePicker.setValue(LocalDate.now());
        datePicker.setLocale(Locale.GERMANY);
        
        ComboBox<Fischart> auswahl = new ComboBox<>("Fischart");
        auswahl.setItemLabelGenerator(Fischart::getName); // Anzeige-Text
        auswahl.setItems(fischarten);
        
        NumberField mengeField = new NumberField("Anzahl der Fische");
        NumberField gewichtField = new NumberField("Gewicht in Gramm");
        NumberField laengeField = new NumberField("Länge (optional)");
        Checkbox released = new Checkbox("Released");
        TextArea ta = new TextArea();
        ta.setWidth("100%");
        
        try {
			ta.setValue(list.getBemerkung());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			//System.out.println("Kein Kommentar!");
		}
        
        String vorgabeName =  list.getFischart();
        
        Optional<Fischart> vorgabe = fischarten.stream()
            .filter(f -> f.getName().equals(vorgabeName))
            .findFirst();
        
        vorgabe.ifPresent(auswahl::setValue);
        
        datePicker.setValue(list.getDatum());
        mengeField.setValue(list.getMenge());
        gewichtField.setValue(list.getGewicht());
        laengeField.setValue(list.getLaenge());
        released.setValue(list.getReleased());
        
        dialogLayout.add(datePicker, auswahl, mengeField , gewichtField , laengeField , released,ta);

        dialog.add(dialogLayout);
        
        Button saveButton = new Button("Speichern");
        Button deleteButton = new Button("" , VaadinIcon.TRASH.create());
        deleteButton.getStyle().set("color", "red");
        Button cancelButton = new Button("Cancel", e1 -> dialog.close());
        saveButton.addClickListener(e3 -> {
        	
        	list.setDatum(datePicker.getValue());
        	list.setFischart(auswahl.getValue().getName());
        	list.setGewicht(gewichtField.getOptionalValue().get());
        	list.setMenge(mengeField.getOptionalValue().get());
        	list.setLaenge(laengeField.getOptionalValue().get());
        	list.setReleased(released.getValue());
        	list.setBemerkung(ta.getValue());
        	
        	if (auswahl.getValue().toString() == "Schneider" ) {
        		mengeField.setValue((double) 1);
        		gewichtField.setValue((double) 0);
        	}
        	
        	fangrepository.save(list);
        	userGrid.getDataProvider().refreshItem(list);
            Notification.show("Fisch gespeichert!");
            dialog.close();

        });
        
        deleteButton.addClickListener(e4 -> {
        	fangrepository.delete(list);
        	Notification.show("Fang wurde gelöscht!");
        	userGrid.setItems(fangrepository.findByUser(username));
        	userGrid.getDataProvider().refreshAll();
            dialog.close();
            
        });

        dialog.open();
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
        dialog.getFooter().add(deleteButton);
             
        add(dialog);
        
	}

}
