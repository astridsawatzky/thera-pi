package rehaSql;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RehaSqlTest {



    @Test
    public void path() {
        assertEquals("C:/mimimi/mimim/blob", RehaSql.exchangebackslashes("C:\\mimimi\\mimim\\blob"));

    }


    public static void main(String[] args) {
        String[] rehasqlargs = new String[4];
        final String path= "C:/Rehaverwaltung/";
         rehasqlargs[0]=path;
        final String aktik ="123456789";
        rehasqlargs[1]=aktik;
        rehasqlargs[2]=String.valueOf(6000);
        rehasqlargs[3] = "full";


        RehaSql.main(rehasqlargs );
    }


}
