package rehaKassenbuch;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RehaKassenbuchTest {
public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
    
    String[] rehasqlargs = new String[4];
    final String path= "C:/Rehaverwaltung/";
     rehasqlargs[0]=path;
    final String aktik ="123456789";
    rehasqlargs[1]=aktik;
    rehasqlargs[2]=String.valueOf(6000);
    rehasqlargs[3] = "full";
    
   RehaKassenbuch.main(rehasqlargs);
}
}
