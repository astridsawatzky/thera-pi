package CommonTools;

public interface Monitor {

    static final Object    START =new Object();
    static final Object    STOP = new Object();
    public void statusChange(Object status );

}
