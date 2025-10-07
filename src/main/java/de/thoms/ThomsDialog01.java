package de.thoms;

import java.util.List;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ThomsDialog01 {

	private Html htmlTop;
	private Html html;

	public Dialog erzeugeDialog() {
		Dialog dialog = new Dialog();

		if (htmlTop.equals(null))
			htmlTop = new Html("<div>Nicht gesetzt!</div>");
		if (html.equals(null))
			html = new Html("<div>Nicht gesetzt!</div>");

		// Drei Buttons
		Button abbrechenButton = new Button("Abbrechen", event -> dialog.close());
		Button speichernButton = new Button("Speichern", event -> {
			// Speichern-Logik hier
			dialog.close();
		});
		Button hilfeButton = new Button("Hilfe", event -> {

		});

		// Layout für Buttons
		hilfeButton.setVisible(false);
		speichernButton.setVisible(false);
		HorizontalLayout buttonLayout = new HorizontalLayout(abbrechenButton, speichernButton, hilfeButton);
		buttonLayout.setSpacing(true);
		buttonLayout.setPadding(true);
		buttonLayout.getStyle().set("margin-top", "1rem");

		// Layout zusammenbauen

		HorizontalLayout top = new HorizontalLayout(htmlTop);
		top.setPadding(true);

		VerticalLayout content = new VerticalLayout(html);
		content.setPadding(true);
		
		
		Section section = new Section(content);
		Div div = new Div( section );
		Scroller scrolle = new Scroller(section);
		dialog.add(top, scrolle , buttonLayout);

		return dialog;
	}

	public void setTop(String sText) {
		String sZeile = "<div><h1>";
		sZeile = sZeile + sText;

		sZeile = sZeile + "</h1></div>";
		htmlTop = new Html(sZeile);

	}

	public void setContext(String[] Text) {
		String sZeile = "<div>";
		for (String s : Text) {
			sZeile = sZeile + "<p>" + s + "</p>";
		}
		sZeile = sZeile + "</div>";
		html = new Html(sZeile);
	}
}
