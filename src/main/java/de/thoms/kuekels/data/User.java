package de.thoms.kuekels.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "application_user")
public class User extends AbstractEntity {

    private String username;
    private String name;
    private String verein;
    @JsonIgnore
    private String hashedPassword;
    private String email;
    private String telefon;
    private boolean gps;
    private String bootsnummer;
    
    public String getBootsnummer() {
		return bootsnummer;
	}
	public void setBootsnummer(String bootsnummer) {
		this.bootsnummer = bootsnummer;
	}
	@Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getVerein() {
        return verein;
    }
    public void setVerein(String verein) {
        this.verein = verein;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public Set<Role> getRoles() {
    	return roles;
    }
    
    public String getRolesString() {
    	Set<Role> r = getRoles();
    	
    	return r.toString();
    }
    
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    public byte[] getProfilePicture() {
    	
        return profilePicture != null ? profilePicture : new byte[0];
    }
    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
	public String getEmail() {
		// TODO Auto-generated method stub
		return email;
	}
	public String getTelefon() {
		// TODO Auto-generated method stub
		return telefon;
	}
	public void setEmail(String email) {
		// TODO Auto-generated method stub
		this.email = email;
	}
	public void setTelefon(String telefon) {
		// TODO Auto-generated method stub
		this.telefon = telefon;
	}
	public boolean isGps() {
		return gps;
	}
	public void setGps(boolean gps) {
		this.gps = gps;
	}

}
