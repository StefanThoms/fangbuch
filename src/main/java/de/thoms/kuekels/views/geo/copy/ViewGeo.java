package de.thoms.kuekels.views.geo.copy;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("map")
@AnonymousAllowed
public class ViewGeo extends Div  {


    public ViewGeo() {
        setId("map"); // div-container für die Karte
        getStyle().set("height", "700px");
        getStyle().set("width", "60%");
        
        
        UI.getCurrent().getPage().addStyleSheet("https://unpkg.com/leaflet/dist/leaflet.css");
        UI.getCurrent().getPage().addJavaScript("https://unpkg.com/leaflet/dist/leaflet.js");

        // JS zum Initialisieren der Karte
        // Nord-West 	-> 	Breitengrad : 53.920371 | Längengrad : 10.232128
        // Süd-Ost 		->	Breitengrad : 53.899756 | Längengrad : 10.260969
        // 54.1032448, Longitude: 9.060352
        String sString = 
        	    "var map = L.map($0).setView([53.90697583182032, 10.242771290018965], 15);" +
        	    "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(map);" +
        	    "L.marker([53.903316, 10.239692]).addTo(map).bindPopup('Pos 1');" +
        	    "L.marker([53.903356, 10.249672]).addTo(map).bindPopup('Pos 2');" +
        	    "L.marker([53.903916, 10.236492]).addTo(map).bindPopup('Pos 3');" +
        	    "setTimeout(() => { map.invalidateSize(); }, 100);";
        
        UI.getCurrent().getPage().executeJs(sString,
        	    getElement()
        	);
    }
}