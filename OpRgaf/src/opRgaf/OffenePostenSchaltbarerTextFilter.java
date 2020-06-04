package opRgaf;

public class OffenePostenSchaltbarerTextFilter extends OffenePostenTextFilter {

    private boolean ein=false;

    public OffenePostenSchaltbarerTextFilter(int ColumnIdx) {
        super(ColumnIdx);
    }

    public OffenePostenSchaltbarerTextFilter(int columnIndex, String text, boolean ein) {
        super(columnIndex,text);
        this.ein = ein;
    }


     public OffenePostenSchaltbarerTextFilter(int columnIndex, String text) {
         super(columnIndex,text);
    }

    void set(boolean ein) {
        this.ein = ein;

    }


    @Override
    protected boolean validate(Object object) {

        return super.validate(object) && ein;
    }

}
