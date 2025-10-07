package de.thoms.kuekels.data;

public class StatistikJahr {
	private String name;
    private String jahr;
    private Long menge;
    private long letztesJahr;
    private long aktuellesJahr;
    private long naechstesJahr;

    public StatistikJahr(String name, String jahr) {
        this.name = name;
        this.jahr = jahr;
    }

    public StatistikJahr(String name, String jahr,Long menge) {
        this.name = name;
        this.jahr = jahr;
    }
    public StatistikJahr(String name, long l, long l2, long l3) {
        this.name = name;
        this.letztesJahr = l;
        this.aktuellesJahr = l2;
        this.naechstesJahr = l3;
    }
    
    public String getName() { return name; }
    public String getJahr() { return jahr; }
    public Long getMenge() { return menge; }

	public long getLetztesJahr() {
		return letztesJahr;
	}

	public long getAktuellesJahr() {
		return aktuellesJahr;
	}

	public long getNaechstesJahr() {
		return naechstesJahr;
	}

}
