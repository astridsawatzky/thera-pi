package Suchen;

public class SearchParameter {
    SearchType type = SearchType.TEXT;

    public SearchType type() {
        return type;
    }

    public String[] criteria() {
        return criteria;
    }

    public int limit() {
        return limit;
    }

    String[] criteria = {};
    int limit = 0;

    public SearchParameter(SearchType type, String searchtext, int limit) {
        this.type = type;
        this.criteria = searchtext.trim()
                                  .split(" ");
        this.limit = limit;
    }
}
