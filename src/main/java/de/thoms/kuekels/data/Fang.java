package de.thoms.kuekels.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.LocalDate;


@Table(name = "Fang")
@Entity
public class Fang {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String user;
	private LocalDate datum;
	private String fischart;
	private double menge;
	private double gewicht;
	private double laenge;
	private boolean released;
	private String bemerkung;
	private double lat;
	private double lon;
	
    public String getBemerkung() {
		return bemerkung;
	}
	public void setBemerkung(String bemerkung) {
		this.bemerkung = bemerkung;
	}
	@Lob
    private byte[] bild;
    
    public byte[] getBild() {
    	 return bild != null ? bild : new byte[0];
    }
    public void setBild(byte[] bild) {
        this.bild = bild;
    }
    public boolean getReleased() {
    	return released ;
    }
    
    public String getReleasedString() {
    	String sAusgabe = "";
    	if(released) sAusgabe = "Ja";
    	return (sAusgabe);
    }
    
    public void setReleased(boolean released) {
    	this.released = released;
    }
    
    public double getLaenge() {
        return laenge;
    }
    public void setLaenge(Double laenge) {
        this.laenge = laenge;
    }
    
	public Long getId() {
		return id;
	}
	
	public String getUser() {
	        return user;
	    }
	public void setUser(String user) {
	        this.user = user;
	    }
	
	public LocalDate getDatum() {
        return datum;
    }
	public void setDatum(LocalDate datum) {
	        this.datum = datum;
	    }
	
	public String getFischart() {
	    return fischart;
	}
	public void setFischart(String fischart) {
	    this.fischart = fischart;
	}
	
	public double getMenge() {
	    return menge;
	}
	public void setMenge(double menge) {
		this.menge = menge;
	}
	
	public double getGewicht() {
	    return gewicht;
	}
	public void setGewicht(double gewicht) {
	    this.gewicht = gewicht;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}

}
