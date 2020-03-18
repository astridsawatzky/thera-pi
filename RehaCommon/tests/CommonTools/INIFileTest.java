package CommonTools;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class INIFileTest {

    private static final int Current_Section_count = 6;
    private static final String INTEGER_SECTION = "Integer";
    private static final String BOOL_SECTION = "Bools";
    private static final String LONG_SECTION = "Longs";
    private static final String DOUBLE_SECTION = "Doubles";
    private static final String DATE_SECTION = "Dates";

    private Path tempIniTestDirectory;

    private Path tempIniFilePath;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws IOException {
        this.tempIniFilePath = Files.createTempFile("IniFile", ".ini");
        this.tempIniTestDirectory = Files.createTempDirectory("tempTestPath");
    }

    @Test
    public void testCreateIniFileWithNullValueAsParameterFailsWithException() {
        // Arrange

        // Act
        new INIFile(null);

        // Assert
        // no exception will be thrown is the expectation here
    }

    @Test
    public void testCreateIniFileWithDirectoryAsFilePath() {
        // Arrange
        String path = this.tempIniTestDirectory.toString();

        // Act
        new INIFile(path);

        // Assert
        // no exception will be thrown is the expectation here
    }

    @Test
    public void testINIFileWithArgs() {
        // Arrange
        String path =this.tempIniFilePath.toString();
                // Act
        new INIFile(path);

        // Arrange
    }

    @Test
    public void testGetFileName() {
        // Arrange
        String path =this.tempIniFilePath.toString();

        // Act
        INIFile myIniFile = new INIFile(path);

        // Arrange
        assertEquals(path, myIniFile.getFileName());
    }

    @Test
    public void testGetStringProperty() {
        // Arrange
        String path =this.tempIniFilePath.toString();

        // Act
        INIFile myIniFile = new INIFile(path);

        // Arrange
        assertEquals("thisissimple", myIniFile.getStringProperty("Strings", "simpleString"));
        assertEquals("spaces in String", myIniFile.getStringProperty("Strings", "spacyString"));

    }

    @Test
    public void testGetKnownIntegerPropertyWillReturnExpectedValue() {
        String path =this.tempIniFilePath.toString();

        // Act
        INIFile myIniFile = new INIFile(path);

        // Arrange
        assertFalse("Non existing value is returned as FALSE",
                myIniFile.getBooleanProperty("Verzeichnisse", "Fahrdienstrohdatei"));
        assertTrue("true is returned as TRUE", myIniFile.getBooleanProperty(BOOL_SECTION, "right"));
        assertFalse("false is returned as FALSE", myIniFile.getBooleanProperty(BOOL_SECTION, "wrong"));
        assertFalse("invalid value is returned as false", myIniFile.getBooleanProperty(BOOL_SECTION, "invalid"));
        assertTrue("1 is returned as TRUE", myIniFile.getBooleanProperty(BOOL_SECTION, "one"));
        assertFalse("0 is returned as FALSE", myIniFile.getBooleanProperty(BOOL_SECTION, "none"));
        assertFalse("5 is returned as FALSE", myIniFile.getBooleanProperty(BOOL_SECTION, "five"));
    }

    @Test
    public void testGetUnknownIntegerPropertyWillReturnNull() {
        String path =this.tempIniFilePath.toString();

        // Act
        INIFile myIniFile = new INIFile(path);

        // Assert
        assertEquals(null, myIniFile.getIntegerProperty(INTEGER_SECTION, "invalid"));
    }

    @Test
    public void testSetIntegerProperty() {
        // Arrange
        String path =this.tempIniFilePath.toString();
        INIFile myIniFile = new INIFile(path);

        // Act
        myIniFile.setIntegerProperty(INTEGER_SECTION, "vierganze", 4, null);

        // Assert
        Integer actual = myIniFile.getIntegerProperty(INTEGER_SECTION, "vierganze");
        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(4));
    }

    @Test
    public void testLongProperty() {

        String path =this.tempIniFilePath.toString();
        INIFile myIniFile = new INIFile(path);
        assertEquals(Long.valueOf(1), myIniFile.getLongProperty(LONG_SECTION, "one"));
        assertEquals(Long.valueOf(2), myIniFile.getLongProperty(LONG_SECTION, "two"));
        assertEquals(Long.valueOf(-2134), myIniFile.getLongProperty(LONG_SECTION, "negativ"));

        assertEquals(null, myIniFile.getLongProperty(LONG_SECTION, "toobig"));
        assertEquals(null, myIniFile.getLongProperty(LONG_SECTION, "invalid"));

        myIniFile.setLongProperty(LONG_SECTION, "15lange", 15L, null);
        assertEquals(Long.valueOf(15L), myIniFile.getLongProperty(LONG_SECTION, "15lange"));
    }

    @Test
    public void testDoubleProperty() {
        String path =this.tempIniFilePath.toString();
        INIFile myIniFile = new INIFile(path);

        assertEquals(Double.valueOf(1), myIniFile.getDoubleProperty(DOUBLE_SECTION, "one"));
        assertEquals(Double.valueOf(2), myIniFile.getDoubleProperty(DOUBLE_SECTION, "two"));
        assertEquals(Double.valueOf(-6.3), myIniFile.getDoubleProperty(DOUBLE_SECTION, "negativ"));
        assertEquals(Double.valueOf(0.5), myIniFile.getDoubleProperty(DOUBLE_SECTION, "osomething"));
        assertEquals(Double.valueOf(0.8), myIniFile.getDoubleProperty(DOUBLE_SECTION, "decimalonly"));
        assertEquals(Double.valueOf(-0.7), myIniFile.getDoubleProperty(DOUBLE_SECTION, "negativDecimalonly"));

        assertEquals(null, myIniFile.getDoubleProperty(DOUBLE_SECTION, "invalid"));

        myIniFile.setDoubleProperty(DOUBLE_SECTION, "13einhalb", 13.5, null);
        assertEquals(Double.valueOf(13.5), myIniFile.getDoubleProperty(DOUBLE_SECTION, "13einhalb"));

    }

    @Test
    @Ignore("the method is not used in project")
    public void testGetTimestampProperty() {
        // the method is not used in project
    }

    @Test
    public void testSetBooleanProperty() {
        String path =this.tempIniFilePath.toString();
        INIFile myIniFile = new INIFile(path);

        myIniFile.setBooleanProperty(DOUBLE_SECTION, "wahr", true, null);
        myIniFile.setBooleanProperty(DOUBLE_SECTION, "falsch", false, null);
        assertTrue("true is returned as TRUE", myIniFile.getBooleanProperty(DOUBLE_SECTION, "wahr"));
        assertFalse("false is returned as FALSE", myIniFile.getBooleanProperty(DOUBLE_SECTION, "falsch"));

    }

    @Test
    public void testSetDatePropertyWillAlsoCreateSectionAndPropertyIfNotAvailable() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        LocalDateTime dateIn = LocalDateTime.parse("2013.12.31 00:00:00", formatter);

        INIFile inifile = new INIFile(null);

        // Act
        inifile.setDateProperty(DOUBLE_SECTION, DATE_SECTION, dateIn, null);

        // Assert
        Map<String, INIFile.INIProperty> section = inifile.getProperties(DOUBLE_SECTION);
        assertThat(section, is(notNullValue()));

        INIFile.INIProperty property = section.get(DATE_SECTION);
        assertThat(property, is(notNullValue()));

        assertThat(property.getPropValue(), is("31.12.2013"));
    }

    @Test
    public void testSetDatePropertyWillOverwriteExistingPropertyInExistingSection() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        LocalDateTime dateIn = LocalDateTime.parse("2013.12.31 00:00:00", formatter);
        LocalDateTime overwriteDate = LocalDateTime.parse("2020.03.09 00:00:00", formatter);

        INIFile inifile = new INIFile(null);
        inifile.setDateProperty(DOUBLE_SECTION, DATE_SECTION, dateIn, null);

        // Act
        inifile.setDateProperty(DOUBLE_SECTION, DATE_SECTION, overwriteDate, null);

        // Assert
        Map<String, INIFile.INIProperty> section = inifile.getProperties(DOUBLE_SECTION);
        INIFile.INIProperty property = section.get(DATE_SECTION);
        assertThat(property.getPropValue(), is("09.03.2020"));
    }

    @Test
    public void testGetDatePropertyAfterEntry() {
        // Arrange
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        LocalDateTime dateIn = LocalDateTime.parse("2013.12.31 00:00:00", formatter);
        LocalDate dateOut = LocalDate.parse("2013.12.31 00:00:00", formatter);

        INIFile inifile = new INIFile(null);
        inifile.setDateProperty(DOUBLE_SECTION, DATE_SECTION, dateIn, null);

        // Act
        LocalDate actual = inifile.getDateProperty(DOUBLE_SECTION, DATE_SECTION);

        // Assert
        assertThat(actual, is(dateOut));
    }

    @Test
    public void testGetDatePropertyWhenNoValueIsAvailable() {
        // Arrange
        INIFile inifile = new INIFile(null);

        // Act
        LocalDate actual = inifile.getDateProperty(DOUBLE_SECTION, DATE_SECTION);

        // Assert
        assertThat(actual, is(nullValue()));
    }

    @Test
    @Ignore("the method is not used in project")
    public void testSetTimestampProperty() {
        // the method is not used in project
    }

    @Test
    @Ignore("the method is not used in project")
    public void testSetTimeStampFormat() {
        // the method is not used in project
    }

    @Test
    public void testGetTotalSections() {
        String path =this.tempIniFilePath.toString();
        INIFile myIniFile = new INIFile(path);
        assertEquals(Current_Section_count, myIniFile.getTotalSections());
    }

    @Test
    public void testGetAllSectionNames() {
        String path =this.tempIniFilePath.toString();
        INIFile myIniFile = new INIFile(path);
        String[] names = { "Strings", "Bools", "Integer", "Longs", "Doubles", "Dates" };
        assertArrayEquals(names, myIniFile.getAllSectionNames());
    }

    @Test
    public void testGetPropertyNames() {
        String path =this.tempIniFilePath.toString();
        INIFile myIniFile = new INIFile(path);
        String[] names = { "Strings", "Bools", "Integer" };
        String[] propertyNames = { "simpleString", "escapedString", "spacyString" };

        assertArrayEquals(propertyNames, myIniFile.getPropertyNames(names[0]));
    }

    @Test
    public void testGetProperties() {
        INIFile expected = new INIFile(null);
        String path =this.tempIniFilePath.toString();
        INIFile myIniFile = new INIFile(path);

        expected.addSection(BOOL_SECTION, null);
        expected.setStringProperty(BOOL_SECTION, "right", "true", null);
        expected.setStringProperty(BOOL_SECTION, "wrong", "false", null);
        expected.setStringProperty(BOOL_SECTION, "invalid", "nonsense", null);
        expected.setStringProperty(BOOL_SECTION, "none", "0", null);

        expected.setStringProperty(BOOL_SECTION, "one", "1", null);
        expected.setStringProperty(BOOL_SECTION, "five", "5", null);
        // assertEquals(expected.getProperties(BOOL_SECTION),
        // myIniFile.getProperties(BOOL_SECTION));
        // XXX: not equal even though values are equal. inner classINIProperty does not
        // overwrite equals
    }

    @Test
    public void testRemoveProperty() {
        INIFile iniFile = new INIFile("");
        iniFile.setStringProperty(LONG_SECTION, "eins", "1", null);
        iniFile.setStringProperty(LONG_SECTION, "zwei", "2", null);
        assertEquals("2", iniFile.getStringProperty(LONG_SECTION, "zwei"));

        iniFile.removeProperty(LONG_SECTION, "zwei");

        assertEquals(null, iniFile.getStringProperty(LONG_SECTION, "zwei"));
        assertEquals("1", iniFile.getStringProperty(LONG_SECTION, "eins"));

    }

    @Test
    public void testRemoveSection() {
        INIFile iniFile = new INIFile("");
        iniFile.addSection(LONG_SECTION, null);
        iniFile.addSection(BOOL_SECTION, null);

        assertTrue(Arrays.asList(iniFile.getAllSectionNames())
                         .contains(LONG_SECTION));
        iniFile.removeSection(LONG_SECTION);

        assertFalse(Arrays.asList(iniFile.getAllSectionNames())
                          .contains(LONG_SECTION));
        assertTrue(Arrays.asList(iniFile.getAllSectionNames())
                         .contains(BOOL_SECTION));

    }

    @Test
    public void testSaveIntoNotExistingFile() throws IOException {
        // Arrange
        File newFile = File.createTempFile("IniFileSaveTest",".ini");
        boolean deleted = newFile.delete();
        if(!deleted){
            fail("PreConditions are not ready.");
        }

        INIFile fixture = new INIFile(newFile.getAbsolutePath());
        fixture.setLongProperty(LONG_SECTION, "TEST", Long.MAX_VALUE, "Comments");

        // Act
        fixture.save();

        // Assert
        INIFile actual = new INIFile(newFile.getAbsolutePath());
        Long actualLongValue = actual.getLongProperty(LONG_SECTION, "TEST");
        assertThat(actualLongValue, is(Long.MAX_VALUE));
    }

    @Test
    public void testRenameSection() {
        INIFile iniFile = new INIFile("");
        iniFile.addSection(LONG_SECTION, null);
        assertTrue(Arrays.asList(iniFile.getAllSectionNames())
                         .contains(LONG_SECTION));
        iniFile.renameSection(LONG_SECTION, BOOL_SECTION, null);
        // FIXME: the sections name field is set to the new value , but the hashmap is
        // not updated.
        // assertTrue(Arrays.asList(iniFile.getAllSectionNames()).contains(LONG_SECTION));
        // assertFalse(Arrays.asList(iniFile.getAllSectionNames()).contains(BOOL_SECTION));

    }

}
