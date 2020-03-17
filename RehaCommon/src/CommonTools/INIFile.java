package CommonTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INIFile class provides methods for manipulating (Read/Write) windows ini
 * files.
 *
 * @author Prasad P. Khandekar
 * @version 1.0
 * @since 1.0
 */
public final class INIFile {

    private static final Logger LOG = LoggerFactory.getLogger(INIFile.class);

    /** Variable to represent the date format */
    private String dateFormatString = "dd.MM.yyyy";

    /** Variable to represent the timestamp format */
    private String dateTimeFormatString = "dd.MM.yyyy hh:mm:ss";

    /** Variable to hold the ini file name and full path */
    private String absolutFileNamePath;

    /** Variable to hold inputstream of the inifile-content in a table */
    private InputStream streamin;

    /** Variable to hold the sections in an ini file. */
    private LinkedHashMap<String, INISection> sectionMap;

    /** Variable to hold environment variables **/
    private Properties environment;

    /**
     * Create a iniFile object from the file named in the parameter.
     * 
     * @param pstrPathAndName The full path and name of the ini file to be used.
     */
    public INIFile(String pstrPathAndName) {
        if (pstrPathAndName == null || !new File(pstrPathAndName).exists()) {
            LOG.error("Inifile does not exist: {}", pstrPathAndName);
        }
        this.environment = getEnvVars();
        this.sectionMap = new LinkedHashMap<>();
        this.absolutFileNamePath = pstrPathAndName;
        // Load the specified INI file.
        if (checkFile(pstrPathAndName))
            loadFile();
    }

    public INIFile(InputStream istream, String pstrPathAndName) {
        if (!new File(pstrPathAndName).exists()) {
            LOG.debug("loading from Stream: {}", pstrPathAndName);
        }
        this.environment = getEnvVars();
        this.sectionMap = new LinkedHashMap<>();
        this.absolutFileNamePath = pstrPathAndName;
        this.streamin = istream;
        // read the specified INI file.
        if (streamin != null)
            readFile();
    }

    /**
     * Returns the ini file name being used.
     * 
     * @return the INI file name.
     */
    public String getFileName() {
        return this.absolutFileNamePath;
    }

    public InputStream getInputStream() {
        return this.streamin;
    }

    /**
     * Returns the specified string property from the specified section.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be retrieved.
     * @return the string property value.
     */
    public String getStringProperty(String pstrSection, String pstrProp) {
        String strRet = null;
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec != null) {
            INIProperty objProp = objSec.getProperty(pstrProp);
            if (objProp != null) {
                strRet = objProp.getPropValue();
            }
        }
        return strRet;
    }

    /**
     * Returns the specified boolean property from the specified section. This
     * method considers the following values as boolean values.
     * <ol>
     * <li>YES/yes/Yes - boolean true</li>
     * <li>NO/no/No - boolean false</li>
     * <li>1 - boolean true</li>
     * <li>0 - boolean false</li>
     * <li>TRUE/True/true - boolean true</li>
     * <li>FALSE/False/false - boolean false</li>
     * </ol>
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be retrieved.
     * @return the boolean value
     */
    public boolean getBooleanProperty(String pstrSection, String pstrProp) {
        boolean blnRet = false;
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec != null) {
            INIProperty objProp = objSec.getProperty(pstrProp);
            if (objProp != null) {
                String strVal = objProp.getPropValue()
                                       .toUpperCase();
                if (strVal.equals("YES") || strVal.equals("TRUE") || strVal.equals("1")) {
                    blnRet = true;
                }

            }

        }
        return blnRet;
    }

    /**
     * Returns the specified integer property from the specified section.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be retrieved.
     * @return the integer property value.
     */
    public Integer getIntegerProperty(String pstrSection, String pstrProp) {
        Integer intRet = null;
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec != null) {
            INIProperty objProp = objSec.getProperty(pstrProp);
            try {
                if (objProp != null) {
                    String strVal = objProp.getPropValue();
                    if (strVal != null)
                        intRet = Integer.valueOf(strVal);
                }
            } catch (NumberFormatException exIgnore) {
            }
        }
        return intRet;
    }

    /**
     * Returns the specified long property from the specified section.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be retrieved.
     * @return the long property value.
     */
    public Long getLongProperty(String pstrSection, String pstrProp) {
        Long lngRet = null;
        String strVal = null;
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec != null) {
            INIProperty objProp = objSec.getProperty(pstrProp);
            try {
                if (objProp != null) {
                    strVal = objProp.getPropValue();
                    if (strVal != null)
                        lngRet = new Long(strVal);
                }
            } catch (NumberFormatException exIgnore) {
            }
        }
        return lngRet;
    }

    /**
     * Returns the specified double property from the specified section.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be retrieved.
     * @return the double property value.
     */
    public Double getDoubleProperty(String pstrSection, String pstrProp) {
        Double dblRet = null;
        String strVal = null;
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec != null) {
            INIProperty objProp = objSec.getProperty(pstrProp);
            try {
                if (objProp != null) {
                    strVal = objProp.getPropValue();
                    if (strVal != null)
                        dblRet = new Double(strVal);
                }
            } catch (NumberFormatException exIgnore) {
            }
        }
        return dblRet;
    }

    /**
     * Returns the specified property from the specified section as
     * {@link LocalDate}.
     * 
     * @param sectionName The INI section name.
     * @param propertyKey The property to be retrieved.
     * @return The property value as {@link LocalDate} instance, or
     *         <code>null</code>, if the property value can't be retrieved or
     *         parsed.
     */
    public LocalDate getDateProperty(String sectionName, String propertyKey) {
        LocalDate dtRet;

        INISection objSec = this.sectionMap.get(sectionName);
        if (objSec != null) {
            INIProperty objProp = objSec.getProperty(propertyKey);
            try {
                String strVal;
                if (objProp != null) {
                    strVal = objProp.getPropValue();
                    if (strVal != null) {
                        DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern(this.dateFormatString);
                        dtRet = LocalDate.parse(strVal, dtFmt);
                    } else {
                        LOG.debug("No property value set for in INIFile {}, Section {}, property name {}!",
                                this.absolutFileNamePath, sectionName, propertyKey);
                        dtRet = null;
                    }
                } else {
                    LOG.debug("No property {} could be found in INIFile {}, Section {}!", propertyKey,
                            this.absolutFileNamePath, sectionName);
                    dtRet = null;
                }
            } catch (IllegalArgumentException | DateTimeParseException ex) {
                LOG.error("Unable to parse property value of INIFile " + this.absolutFileNamePath + ", Section "
                        + sectionName + ", property " + propertyKey + "! Message:" + ex.getMessage(), ex);
                dtRet = null;
            }
        } else {
            LOG.debug("No section {} could be found in INIFile {}!", sectionName, this.absolutFileNamePath);
            dtRet = null;
        }
        return dtRet;
    }

    /**
     * Returns the specified date property from the specified section.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be retrieved.
     * @return the date property value.
     */
    public Date getTimestampProperty(String pstrSection, String pstrProp) {
        Timestamp tsRet = null;
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec != null) {
            INIProperty objProp = objSec.getProperty(pstrProp);
            try {
                String strVal = null;
                if (objProp != null)
                    strVal = objProp.getPropValue();
                if (strVal != null) {
                    DateFormat dtFmt = new SimpleDateFormat(this.dateFormatString);
                    Date dtTmp = dtFmt.parse(strVal);
                    tsRet = new Timestamp(dtTmp.getTime());
                }
            } catch (ParseException exIgnore) {
            } catch (IllegalArgumentException ex) {
            }
        }
        return tsRet;
    }

    /*------------------------------------------------------------------------------
     * Setters
    ------------------------------------------------------------------------------*/
    /**
     * Sets the comments associated with a section.
     * 
     * @param pstrSection  the section name
     * @param pstrComments the comments.
     */
    public void addSection(String pstrSection, String pstrComments) {
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec == null) {
            objSec = new INISection(pstrSection);
            this.sectionMap.put(pstrSection, objSec);
        }
        objSec.setSecComments(delRemChars(pstrComments));
    }

    /**
     * Sets the specified string property.
     * 
     * @param pstrSection    the INI section name.
     * @param newpstrSection the new Section name to be set.
     * @pstrVal the string value to be persisted
     */

    public void renameSection(String pstrSection, String newpstrSection, String pstrComments) {
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec != null) {
            this.sectionMap.put(newpstrSection, objSec);
            if (this.sectionMap.containsKey(pstrSection)) {
                this.sectionMap.remove(pstrSection);
            }
            objSec.setSecComments(delRemChars(pstrComments));
        }
    }

    /**
     * Sets the specified string property.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be set.
     * @pstrVal the string value to be persisted
     */
    public void setStringProperty(String pstrSection, String pstrProp, String pstrVal, String pstrComments) {
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec == null) {
            objSec = new INISection(pstrSection);
            this.sectionMap.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, pstrVal, pstrComments);
    }

    /**
     * Sets the specified boolean property.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be set.
     * @param pblnVal     the boolean value to be persisted
     */
    public void setBooleanProperty(String pstrSection, String pstrProp, boolean pblnVal, String pstrComments) {
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec == null) {
            objSec = new INISection(pstrSection);
            this.sectionMap.put(pstrSection, objSec);
        }
        if (pblnVal)
            objSec.setProperty(pstrProp, "TRUE", pstrComments);
        else
            objSec.setProperty(pstrProp, "FALSE", pstrComments);
    }

    /**
     * Sets the specified integer property.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be set.
     * @param pintVal     the int property to be persisted.
     */
    public void setIntegerProperty(String pstrSection, String pstrProp, int pintVal, String pstrComments) {
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec == null) {
            objSec = new INISection(pstrSection);
            this.sectionMap.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, Integer.toString(pintVal), pstrComments);
    }

    /**
     * Sets the specified long property.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be set.
     * @param plngVal     the long value to be persisted.
     */
    public void setLongProperty(String pstrSection, String pstrProp, long plngVal, String pstrComments) {
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec == null) {
            objSec = new INISection(pstrSection);
            this.sectionMap.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, Long.toString(plngVal), pstrComments);
    }

    /**
     * Sets the specified double property.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be set.
     * @param pdblVal     the double value to be persisted.
     */
    public void setDoubleProperty(String pstrSection, String pstrProp, double pdblVal, String pstrComments) {
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec == null) {
            objSec = new INISection(pstrSection);
            this.sectionMap.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, Double.toString(pdblVal), pstrComments);
    }

    /**
     * Sets the specified java.util.Date property.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be set.
     * @param pdtVal      the date value to be persisted.
     */
    public void setDateProperty(String pstrSection, String pstrProp, LocalDateTime pdtVal, String pstrComments) {
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec == null) {
            objSec = new INISection(pstrSection);
            this.sectionMap.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, utilDateToStr(pdtVal, this.dateFormatString), pstrComments);
    }

    /**
     * Sets the specified java.sql.Timestamp property.
     * 
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be set.
     * @param ptsVal      the timestamp value to be persisted.
     */
    public void setTimestampProperty(String pstrSection, String pstrProp, Timestamp ptsVal, String pstrComments) {
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec == null) {
            objSec = new INISection(pstrSection);
            this.sectionMap.put(pstrSection, objSec);
        }
        objSec.setProperty(pstrProp, timeToStr(ptsVal, this.dateTimeFormatString), pstrComments);
    }

    /**
     * Sets the format to be used to interpreat date values.
     * 
     * @param pstrDtFmt the format string
     * @throws IllegalArgumentException if the if the given pattern is invalid
     */
    public void setDateFormat(String pstrDtFmt) throws IllegalArgumentException {
        if (!checkDateTimeFormat(pstrDtFmt))
            throw new IllegalArgumentException("The specified date pattern is invalid!");
        this.dateFormatString = pstrDtFmt;
    }

    /**
     * Sets the format to be used to interpreat timestamp values.
     * 
     * @param pstrTSFmt the format string
     * @throws IllegalArgumentException if the if the given pattern is invalid
     */
    public void setTimeStampFormat(String pstrTSFmt) {
        if (!checkDateTimeFormat(pstrTSFmt))
            throw new IllegalArgumentException("The specified timestamp pattern is invalid!");
        this.dateTimeFormatString = pstrTSFmt;
    }

    /*------------------------------------------------------------------------------
     * Public methods
    ------------------------------------------------------------------------------*/
    public int getTotalSections() {
        return this.sectionMap.size();
    }

    /**
     * Returns a string array containing names of all sections in INI file.
     * 
     * @return the string array of section names
     */
    public String[] getAllSectionNames() {
        int iCntr = 0;
        String[] arrRet = null;

        try {
            if (this.sectionMap.size() > 0) {
                arrRet = new String[this.sectionMap.size()];
                for (Iterator<String> iter = this.sectionMap.keySet()
                                                            .iterator(); iter.hasNext();) {
                    arrRet[iCntr] = iter.next();
                    iCntr++;
                }
            }
        } catch (NoSuchElementException exIgnore) {
        }
        return arrRet;
    }

    /**
     * Returns a string array containing names of all the properties under specified
     * section.
     * 
     * @param pstrSection the name of the section for which names of properties is
     *                    to be retrieved.
     * @return the string array of property names.
     */
    public String[] getPropertyNames(String pstrSection) {
        String[] arrRet = null;
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec != null) {
            arrRet = objSec.getPropNames();
        }
        return arrRet;
    }

    /**
     * Returns a map containing all the properties under specified section.
     * 
     * @param pstrSection the name of the section for which properties are to be
     *                    retrieved.
     * @return the map of properties.
     */
    public Map<String, INIProperty> getProperties(String pstrSection) {
        Map<String, INIProperty> hmRet = null;
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec != null) {
            hmRet = objSec.getProperties();
        }
        return hmRet;
    }

    /**
     * Removed specified property from the specified section. If the specified
     * section or the property does not exist, does nothing.
     * 
     * @param pstrSection the section name.
     * @param pstrProp    the name of the property to be removed.
     */
    public void removeProperty(String pstrSection, String pstrProp) {
        INISection objSec = this.sectionMap.get(pstrSection);
        if (objSec != null) {
            objSec.removeProperty(pstrProp);
        }
    }

    /**
     * Removes the specified section if one exists, otherwise does nothing.
     * 
     * @param pstrSection the name of the section to be removed.
     */
    public void removeSection(String pstrSection) {
        this.sectionMap.remove(pstrSection);
    }

    /**
     * Flush changes back to the disk file. If the disk file does not exists then
     * creates the new one.
     */
    public synchronized boolean save() {
        boolean blnRet = false;

        File objFile = new File(this.absolutFileNamePath);
        try (FileWriter objWriter = new FileWriter(objFile);) {
            if (this.sectionMap.size() == 0)
                return false;
            if (objFile.exists())
                objFile.delete();

            Iterator<String> itrSec = this.sectionMap.keySet()
                                                     .iterator();
            while (itrSec.hasNext()) {
                String strName = itrSec.next();
                INISection objSec = this.sectionMap.get(strName);
                String strTemp = objSec.toString();
                objWriter.write(strTemp);
                objWriter.write("\r\n");
            }
            blnRet = true;
        } catch (IOException exIgnore) {
            exIgnore.printStackTrace();
        }
        return blnRet;
    }

    public synchronized StringBuffer saveToStringBuffer() {
        StringBuffer buf = new StringBuffer();
        try {
            for (Map.Entry<String, INISection> entry : this.sectionMap.entrySet()) {
                INISection objSec = entry.getValue();
                String strTemp = objSec.toString();
                buf.append(strTemp);
                buf.append("\r\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return buf;
    }

    /*------------------------------------------------------------------------------
     * Helper functions
     *----------------------------------------------------------------------------*/
    /**
     * Procedure to read environment variables. Thanx to
     * http://www.rgagnon.com/howto.html for this implementation.
     */
    private Properties getEnvVars() {
        Properties envVars = new Properties();
        try {
            Runtime r = Runtime.getRuntime();
            String os = System.getProperty("os.name")
                              .toLowerCase();
            Process p;
            if (os.contains("windows 9")) {
                p = r.exec("command.com /c set");
            } else if ((os.contains("nt")) || (os.contains("windows 2000")) || (os.contains("windows xp"))) {
                p = r.exec("cmd.exe /c set");
            } else {
                // our last hope, we assume Unix (thanks to H. Ware for the fix)
                p = r.exec("env");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                int idx = line.indexOf('=');
                String key = line.substring(0, idx);
                String value = line.substring(idx + 1);
                envVars.setProperty(key, value);
            }
        } catch (Exception exIgnore) {
        }
        return envVars;
    }

    /**
     * Helper function to check the date time formats.
     * 
     * @param pstrDtFmt the date time format string to be checked.
     * @return true for valid date/time format, false otherwise.
     */
    private boolean checkDateTimeFormat(String pstrDtFmt) {
        boolean blnRet;

        try {
            DateTimeFormatter.ofPattern(pstrDtFmt);
            blnRet = true;
        } catch (NullPointerException | IllegalArgumentException exIgnore) {
            blnRet = false;
        }
        return blnRet;
    }

    /**
     * Reads the INI file and load its contentens into a section collection after
     * parsing the file line by line.
     */
    private void loadFile() {
        INISection objSec = null;

        try (FileReader objFRdr = new FileReader(this.absolutFileNamePath);
                BufferedReader objBRdr = new BufferedReader(objFRdr)) {
            String strSection = null;
            String strRemarks = null;
            while (objBRdr.ready()) {
                int iPos = -1;
                String strLine = objBRdr.readLine()
                                        .trim();
                if (strLine.length() == 0) {
                } else if (strLine.substring(0, 1)
                                  .equals(";")) {
                    if (strRemarks == null)
                        strRemarks = strLine.substring(1);
                    else if (strRemarks.length() == 0)
                        strRemarks = strLine.substring(1);
                    else
                        strRemarks = strRemarks + "\r\n" + strLine.substring(1);
                } else if (strLine.startsWith("[") && strLine.endsWith("]")) {
                    // Section start reached create new section
                    if (objSec != null)
                        this.sectionMap.put(strSection.trim(), objSec);
                    strSection = strLine.substring(1, strLine.length() - 1);
                    objSec = new INISection(strSection.trim(), strRemarks);
                    strRemarks = null;
                } else if ((iPos = strLine.indexOf('=')) > 0 && objSec != null) {
                    // read the key value pair 012345=789
                    objSec.setProperty(strLine.substring(0, iPos)
                                              .trim(),
                            strLine.substring(iPos + 1)
                                   .trim(),
                            strRemarks);
                    strRemarks = null;
                }
            }
            if (objSec != null)
                this.sectionMap.put(strSection.trim(), objSec);
        } catch (NullPointerException | IOException exIgnore) {
            this.sectionMap.clear();
        }
    }

    private void readFile() {
        int iPos;
        String strLine;
        String strSection = null;
        String strRemarks = null;
        INISection objSec = null;

        try (InputStreamReader objFRdr = new InputStreamReader(streamin);
                BufferedReader objBRdr = new BufferedReader(objFRdr)) {
            while (objBRdr.ready()) {
                strLine = objBRdr.readLine()
                                 .trim();
                if (strLine.length() == 0) {
                } else if (strLine.substring(0, 1)
                                  .equals(";")) {
                    if (strRemarks == null)
                        strRemarks = strLine.substring(1);
                    else if (strRemarks.length() == 0)
                        strRemarks = strLine.substring(1);
                    else
                        strRemarks = strRemarks + "\r\n" + strLine.substring(1);
                } else if (strLine.startsWith("[") && strLine.endsWith("]")) {
                    // Section start reached create new section
                    if (objSec != null)
                        this.sectionMap.put(strSection.trim(), objSec);
                    objSec = null;
                    strSection = strLine.substring(1, strLine.length() - 1);
                    objSec = new INISection(strSection.trim(), strRemarks);
                    strRemarks = null;
                } else if ((iPos = strLine.indexOf("=")) > 0 && objSec != null) {
                    // read the key value pair 012345=789
                    objSec.setProperty(strLine.substring(0, iPos)
                                              .trim(),
                            strLine.substring(iPos + 1)
                                   .trim(),
                            strRemarks);
                    strRemarks = null;
                }
            }
            if (objSec != null)
                this.sectionMap.put(strSection.trim(), objSec);
        } catch (IOException | NullPointerException e) {
            this.sectionMap.clear();
        }
    }

    /**
     * Helper method to check the existance of a file.
     * 
     * @param pstrFile the full path and name of the file to be checked.
     * @return true if file exists, false otherwise.
     */
    private boolean checkFile(String pstrFile) {
        boolean blnRet;

        try {
            File objFile = new File(pstrFile);
            blnRet = (objFile.exists() && objFile.isFile());
        } catch (Exception e) {
            blnRet = false;
        }
        return blnRet;
    }

    /**
     * Converts a java.util.date into String
     * 
     * @param pdt     Date that need to be converted to String
     * @param pstrFmt The date format pattern.
     * @return String
     */
    private String utilDateToStr(LocalDateTime pdt, String pstrFmt) {
        String strRet;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pstrFmt);
            strRet = pdt.format(formatter);
        } catch (Exception e) {
            LOG.error("Exception: " + e.getMessage(), e);
            strRet = null;
        }
        return strRet;
    }

    /**
     * Converts the given sql timestamp object to a string representation. The
     * format to be used is to be obtained from the configuration file.
     *
     * @param pobjTS  the sql timestamp object to be converted.
     * @param pstrFmt If true formats the string using GMT timezone otherwise using
     *                local timezone.
     * @return the formatted string representation of the timestamp.
     */
    private String timeToStr(Timestamp pobjTS, String pstrFmt) {
        String strRet;

        try {
            SimpleDateFormat dtFmt = new SimpleDateFormat(pstrFmt);
            strRet = dtFmt.format(pobjTS);
        } catch (IllegalArgumentException | NullPointerException iae) {
            strRet = "";
        }
        return strRet;
    }

    /**
     * This function deletes the remark characters ';' from source string
     * 
     * @param pstrSrc the source string
     * @return the converted string
     */
    private String delRemChars(String pstrSrc) {
        int intPos;

        if (pstrSrc == null)
            return null;
        while ((intPos = pstrSrc.indexOf(';')) >= 0) {
            if (intPos == 0)
                pstrSrc = pstrSrc.substring(intPos + 1);
            else if (intPos > 0)
                pstrSrc = pstrSrc.substring(0, intPos) + pstrSrc.substring(intPos + 1);
        }
        return pstrSrc;
    }

    /**
     * This function adds a remark character ';' in source string.
     * 
     * @param pstrSrc source string
     * @return converted string.
     */
    private String addRemChars(String pstrSrc) {
        int intLen;
        int intPos = 0;
        int intPrev = 0;

        String strLeft;
        String strRight;

        if (pstrSrc == null)
            return null;
        while (intPos >= 0) {
            intLen = 2;
            intPos = pstrSrc.indexOf("\r\n", intPrev);
            if (intPos < 0) {
                intLen = 1;
                intPos = pstrSrc.indexOf("\n", intPrev);
                if (intPos < 0)
                    intPos = pstrSrc.indexOf("\r", intPrev);
            }
            if (intPos == 0) {
                pstrSrc = ";\r\n" + pstrSrc.substring(intPos + intLen);
                intPrev = intPos + intLen + 1;
            } else if (intPos > 0) {
                strLeft = pstrSrc.substring(0, intPos);
                strRight = pstrSrc.substring(intPos + intLen);
                if (strRight == null)
                    pstrSrc = strLeft;
                else if (strRight.length() == 0)
                    pstrSrc = strLeft;
                else
                    pstrSrc = strLeft + "\r\n;" + strRight;
                intPrev = intPos + intLen + 1;
            }
        }
        if (!pstrSrc.substring(0, 1)
                    .equals(";"))
            pstrSrc = ";" + pstrSrc;
        pstrSrc = pstrSrc + "\r\n";
        return pstrSrc;
    }

    /*------------------------------------------------------------------------------
     * Main entry point to test the functionality.
     *----------------------------------------------------------------------------*/
    /**
     * The main entry point for testing.
     * 
     * @param pstrArgs the command line arguments array if any.
     */
    public static void main(String[] pstrArgs) {
        INIFile objINI = null;
        String strFile = null;

        if (pstrArgs.length == 0)
            return;

        strFile = pstrArgs[0];
        // Following call will load the strFile if one exists.
        objINI = new INIFile(strFile);

        objINI.setStringProperty("Folders", "folder1", "G:\\Temp", null);
        objINI.setStringProperty("Folders", "folder2", "G:\\Temp\\Backup", null);

        // Save changes back to strFile.
        objINI.save();
    }

    /*------------------------------------------------------------------------------
     * Private class representing the INI Section.
     *----------------------------------------------------------------------------*/
    /**
     * Class to represent the individual ini file section.
     * 
     * @author Prasad P. Khandekar
     * @version 1.0
     * @since 1.0
     */
    private class INISection {
        /** Variable to hold any comments associated with this section */
        private String mstrComment;

        /** Variable to hold the section name. */
        private String mstrName;

        /** Variable to hold the properties falling under this section. */
        private LinkedHashMap<String, INIProperty> mhmapProps;

        /**
         * Construct a new section object identified by the name specified in parameter.
         * 
         * @param pstrSection The new sections name.
         */
        public INISection(String pstrSection) {
            this.mstrName = pstrSection;
            this.mhmapProps = new LinkedHashMap<String, INIProperty>();
        }

        /**
         * Construct a new section object identified by the name specified in parameter
         * and associated comments.
         * 
         * @param pstrSection  The new sections name.
         * @param pstrComments the comments associated with this section.
         */
        public INISection(String pstrSection, String pstrComments) {
            this.mstrName = pstrSection;
            this.mstrComment = delRemChars(pstrComments);
            this.mhmapProps = new LinkedHashMap<String, INIProperty>();
        }

        /**
         * Sets the comments associated with this section.
         * 
         * @param pstrComments the comments
         */
        public void setSecComments(String pstrComments) {
            this.mstrComment = delRemChars(pstrComments);
        }

        /**
         * Removes specified property value from this section.
         * 
         * @param pstrProp The name of the property to be removed.
         */
        public void removeProperty(String pstrProp) {
            if (this.mhmapProps.containsKey(pstrProp))
                this.mhmapProps.remove(pstrProp);
        }

        /**
         * Creates or modifies the specified property value.
         * 
         * @param pstrProp     The name of the property to be created or modified.
         * @param pstrValue    The new value for the property.
         * @param pstrComments the associated comments
         */
        public void setProperty(String pstrProp, String pstrValue, String pstrComments) {
            this.mhmapProps.put(pstrProp, new INIProperty(pstrProp, pstrValue, pstrComments));
        }

        /**
         * Returns a map of all properties.
         * 
         * @return a map of all properties
         */
        public Map<String, INIProperty> getProperties() {
            return Collections.unmodifiableMap(this.mhmapProps);
        }

        /**
         * Returns a string array containing names of all the properties under this
         * section.
         * 
         * @return the string array of property names.
         */
        public String[] getPropNames() {
            int iCntr = 0;
            String[] arrRet = null;
            Iterator<String> iter = null;

            try {
                if (this.mhmapProps.size() > 0) {
                    arrRet = new String[this.mhmapProps.size()];
                    for (iter = this.mhmapProps.keySet()
                                               .iterator(); iter.hasNext();) {
                        arrRet[iCntr] = iter.next();
                        iCntr++;
                    }
                }
            } catch (NoSuchElementException exIgnore) {
                arrRet = null;
            }
            return arrRet;
        }

        /**
         * Returns underlying value of the specified property.
         * 
         * @param pstrProp the property whose underlying value is to be etrieved.
         * @return the property value.
         */
        public INIProperty getProperty(String pstrProp) {
            INIProperty objRet = null;

            if (this.mhmapProps.containsKey(pstrProp))
                objRet = this.mhmapProps.get(pstrProp);
            return objRet;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            Set<String> colKeys = null;
            String strRet = "";
            Iterator<String> iter = null;
            INIProperty objProp = null;
            StringBuffer objBuf = new StringBuffer();

            if (this.mstrComment != null)
                objBuf.append(addRemChars(this.mstrComment));
            objBuf.append("[" + this.mstrName + "]\r\n");
            colKeys = this.mhmapProps.keySet();
            if (colKeys != null) {
                iter = colKeys.iterator();
                if (iter != null) {
                    while (iter.hasNext()) {
                        objProp = this.mhmapProps.get(iter.next());
                        objBuf.append(objProp.toString());
                        objBuf.append("\r\n");
                    }
                }
            }
            strRet = objBuf.toString();
            return strRet;
        }
    }

    /*------------------------------------------------------------------------------
     * Private class representing the INI Property.
     *----------------------------------------------------------------------------*/
    /**
     * This class represents a key value pair called property in an INI file.
     * 
     * @author Prasad P. Khandekar
     * @version 1.0
     * @since 1.0
     */
    class INIProperty {
        /** Variable to hold name of this property */
        private String mstrName;
        /** Variable to hold value of this property */
        private String mstrValue;
        /** Variable to hold comments associated with this property */
        private String mstrComments;

        /**
         * Constructor
         * 
         * @param pstrName     the name of this property.
         * @param pstrValue    the value of this property.
         * @param pstrComments the comments associated with this property.
         */
        public INIProperty(String pstrName, String pstrValue, String pstrComments) {
            this.mstrName = pstrName;
            this.mstrValue = pstrValue;
            this.mstrComments = delRemChars(pstrComments);
        }

        /**
         * Returns value of this property. If value contains a reference to environment
         * avriable then this reference is replaced by actual value before the value is
         * returned.
         * 
         * @return the value of this property.
         */
        public String getPropValue() {
            int intStart = 0;
            int intEnd = 0;
            String strVal = null;
            String strVar = null;
            String strRet = null;

            strRet = this.mstrValue;
            intStart = strRet.indexOf("%");
            try {
                if (intStart >= 0) {
                    intEnd = strRet.indexOf("%", intStart + 1);
                    strVar = strRet.substring(intStart + 1, intEnd);
                    strVal = environment.getProperty(strVar);
                    if (strVal != null) {
                        strRet = strRet.substring(0, intStart) + strVal + strRet.substring(intEnd + 1);
                    }
                }
            } catch (Exception ex) {
                return strRet;
            }
            return strRet;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            String strRet = "";

            if (this.mstrComments != null)
                strRet = addRemChars(mstrComments);
            strRet = strRet + this.mstrName + " = " + this.mstrValue;
            return strRet;
        }
    }
}
