package CommonTools;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Test;

public class INIToolTest {


    @Test
    public void pathSplitsIntoDirAndFile() throws Exception {
        String pfad = "C:\\RehaVerwaltung\\ini\\987654321\\whatever.ini";
        assertEquals("whatever.ini", Paths.get(pfad).getFileName().toString());
        assertEquals("C:\\RehaVerwaltung\\ini\\987654321", Paths.get(pfad).getParent().toString());

    }

}
