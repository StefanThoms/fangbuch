package de.thoms.kuekels.data;

public class FangStatistikDTO {
    private String name;
    private Long menge;
    private Long gewicht;

    public FangStatistikDTO(String name, Long menge, Long gewicht) {
        this.name = name;
        this.menge = menge;
        this.gewicht = gewicht;
    }

    public String getName() { return name; }
    public Long getMenge() { return menge; }
    public Long getGewicht() { return gewicht; }
}
