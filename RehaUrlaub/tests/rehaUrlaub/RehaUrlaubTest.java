package rehaUrlaub;

import java.io.IOException;

import org.ini4j.InvalidFileFormatException;

public class RehaUrlaubTest {


    public static void main(String[] args) throws InvalidFileFormatException, IOException {
        String[] urlaubargs = new String[4];
        final String path= "C:/Rehaverwaltung/";
         urlaubargs[0]=path;
        final String aktik ="123456789";
        urlaubargs[1]=aktik;
        RehaUrlaub.main(urlaubargs  );
    }

}
