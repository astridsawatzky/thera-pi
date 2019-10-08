package Suchen;

public enum SearchType {
    ICD("ICD-10-Code eingeben -> ICD-10-Text suchen"),
    TEXT("ICD-10-Text eingeben -> ICD-10-Code suchen");

    String display;

    private SearchType(String text) {
        display = text;
    }

    @Override
    public String toString() {
        return display;
    }

}
