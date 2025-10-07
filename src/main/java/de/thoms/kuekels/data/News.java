package de.thoms.kuekels.data;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Table(name = "News")
@Entity
public class News {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;
		
		private String user;
		private LocalDate datum;
		private String schlagwort;
		private String text;
	   
	    public String getSchlagwort() {
	    	return schlagwort;
	    }
	    
	    public String getText() {
	    	
	    	return (text);
	    }
	    
	    public void setSchlagwort(String schlagwort) {
	    	this.schlagwort = schlagwort;
	    }
	    
	   
	    public void setText(String text) {
	        this.text = text;
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
		
		
}
