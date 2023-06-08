package api.model;

public class FilterModel {
    public String filter;
    public long id;

    public FilterModel(long id,String filter) {
        this.id = id;
        this.filter = filter;
    }
}
