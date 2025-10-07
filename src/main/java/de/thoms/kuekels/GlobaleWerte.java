package de.thoms.kuekels;

public class GlobaleWerte {
	public static final String Titel = "Fangbuch Mözener See";
	public static final String Version ="v07.10.2025";
	public static final String smtpAbsender = "sthoms@thoms-edv.de";
	
	public static final String[] Aenderungen = { 
			"<b>07.10.2025</b>	'Alle Fänge' Datumsfeld. Anpassen Thoms-Dialog",
			"<b>04.10.2025</b>	'Alle Fänge' den Refresh angepasst und Defaulwerte gesetzt.",
			"<b>03.10.2025-b</b>	'Alle Fänge' -> User auf STERN , 'Mein Fangbuch' -> Datum richtig sortiert. ",
			"<b>03.10.2025-a</b>	Anpassung in 'Alle Fänge'",
			"<b>03.10.2025</b>	Versionskontrolle in globalen Werten",
			"<b>01.10.2025</b>	Versionskontrolle , GPS im Profil genehmigen",
			"<b>01.09.2025</b>	GPS der Fangdaten werden mit erfasst", 
			"<b>01.08.2025</b>	News und Statistiken" };
	/*
	public static final double nordBreite = 53.92546895055041;
	public static final double sueddBreite = 53.900219772870145;
	public static final double westLaenge = 10.231967724223354;
	public static final double ostLaenge = 10.260128617451837;
	*/
	
	public static final double nordBreite = 53.954903434478155;
	public static final double sueddBreite = 53.86428103530648;
	public static final double westLaenge = 9.467752223709311;
	public static final double ostLaenge = 9.610717924537987;
	
	
	// Mit der Funktion wird geprüft ob die Koordinaten auf den See passen.
	
	// 53.9015, Longitude: 9.5417
	
	public boolean pruefeGPS(double x , double y) {
		if ( ( x >= nordBreite & x <=  sueddBreite ) & ( y >= westLaenge & y <=  ostLaenge ) )
		return true;
		else return false;
	}
}
