package CommonTools;

import java.io.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INIFile class provides methods for manipulating (Read/Write) windows ini
 * files.
 */
public final class INIFile {

    /**
     * Logger for logging any output via logging framework, instead of using
     * System.out.
     */
    private static final Logger LOG = LoggerFactory.getLogger(INIFile.class);

    /**
     * Constant field, used as String representation for a boolean <code>true</code>
     * value.
     */
    private static final String INI_FILE_VALUE_STRING_TRUE = "TRUE";

    /**
     * Constant field, used as String representation for a boolean
     * <code>false</code> value.
     */
    private static final String INI_FILE_VALUE_STRING_FALSE = "FALSE";

    /**
     * Constant string part, used in error logging output about a parsing error of
     * an string value into the target type.
     */
    private static final String PARSE_EXCEPTION_BASE_MESSAGE = "File %s: Unable to parse the value for sectionKey %s, property %s %s ";

    /** Variable to represent the date format */
    private String dateFormatString = "dd.MM.yyyy";

    /** Variable to represent the timestamp format */
    private String timeStampFormatString = "dd.MM.yyyy hh:mm:ss";

    /** Variable to hold the ini file name and full path */
    private String absoluteFileNamePath;

    /** Variable to hold the sections in an ini file. */
    private LinkedHashMap<String, INISection> sectionMap;

    /**
     * Create a iniFile object from the file named in the parameter.
     * 
     * @param absoluteFileNamePath The full path and name of the ini file to be
     *                             used.
     */
    public INIFile(String absoluteFileNamePath) {
        if (absoluteFileNamePath == null || !new File(absoluteFileNamePath).exists()) {
            LOG.error("Inifile does not exist: {}", absoluteFileNamePath);
        }
        this.sectionMap = new LinkedHashMap<>();
        this.absoluteFileNamePath = absoluteFileNamePath;
        // Load the specified INI file.
        if (checkFile(absoluteFileNamePath))
            loadFile();
    }

    public INIFile(InputStream inputStream, String absoluteFileNamePath) {
        if (!new File(absoluteFileNamePath).exists()) {
            LOG.debug("loading from Stream: {}", absoluteFileNamePath);
        }
        this.sectionMap = new LinkedHashMap<>();
        this.absoluteFileNamePath = absoluteFileNamePath;
        // read the specified INI file.
        if (inputStream != null)
            readFile(inputStream);
    }

    /**
     * Returns the ini file name being used.
     * 
     * @return the INI file name.
     */
    public String getFileName() {
        return this.absoluteFileNamePath;
    }

    /**
     * Returns the specified string property from the specified sectionKey.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be retrieved.
     * @return the string property value.
     */
    public String getStringProperty(String sectionKey, String propertyKey) {
        return getProperty(sectionKey, propertyKey, Function.identity());
    }

    /**
     * Returns the specified boolean property from the specified sectionKey. This
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
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be retrieved.
     * @return the boolean value
     */
    public Boolean getBooleanProperty(String sectionKey, String propertyKey) {
        Boolean result = null;
        try {
            result = getProperty(sectionKey, propertyKey, Boolean::valueOf);
        } catch (NumberFormatException ex) {
            LOG.error(String.format(PARSE_EXCEPTION_BASE_MESSAGE, this.absoluteFileNamePath, sectionKey, propertyKey,
                    "as Boolean value!"), ex);
        }
        return result;
    }

    /**
     * Returns the specified integer property from the specified sectionKey.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be retrieved.
     * @return the integer property value.
     */
    public Integer getIntegerProperty(String sectionKey, String propertyKey) {
        Integer result = null;
        try {
            result = getProperty(sectionKey, propertyKey, Integer::valueOf);
        } catch (NumberFormatException ex) {
            LOG.error(String.format(PARSE_EXCEPTION_BASE_MESSAGE, this.absoluteFileNamePath, sectionKey, propertyKey,
                    "as Integer value!"), ex);
        }
        return result;
    }

    /**
     * Returns the specified long property from the specified sectionKey.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be retrieved.
     * @return the long property value.
     */
    public Long getLongProperty(String sectionKey, String propertyKey) {
        Long result = null;
        try {
            result = getProperty(sectionKey, propertyKey, Long::valueOf);
        } catch (NumberFormatException ex) {
            LOG.error(String.format(PARSE_EXCEPTION_BASE_MESSAGE, this.absoluteFileNamePath, sectionKey, propertyKey,
                    "as Long value!"), ex);
        }
        return result;
    }

    /**
     * Returns the specified double property from the specified sectionKey.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be retrieved.
     * @return the double property value.
     */
    public Double getDoubleProperty(String sectionKey, String propertyKey) {
        Double result = null;
        try {
            result = getProperty(sectionKey, propertyKey, Double::valueOf);
        } catch (NumberFormatException ex) {
            LOG.error(String.format(PARSE_EXCEPTION_BASE_MESSAGE, this.absoluteFileNamePath, sectionKey, propertyKey,
                    "as Double value!"), ex);
        }
        return result;
    }

    /**
     * Returns the specified property from the specified sectionKey as
     * {@link LocalDate}.
     * 
     * @param sectionKey  The INI sectionKey name.
     * @param propertyKey The property to be retrieved.
     * @return The property value as {@link LocalDate} instance, or
     *         <code>null</code>, if the property value can't be retrieved or
     *         parsed.
     */
    public LocalDate getDateProperty(String sectionKey, String propertyKey) {
        LocalDate result = null;
        try {
            result = getProperty(sectionKey, propertyKey, s -> {
                DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern(this.dateFormatString);
                return LocalDate.parse(s, dtFmt);

            });
        } catch (NumberFormatException ex) {
            LOG.error(String.format(PARSE_EXCEPTION_BASE_MESSAGE, this.absoluteFileNamePath, sectionKey, propertyKey,
                    "as LocalDate with format string" + this.dateFormatString + "!"), ex);
        }
        return result;
    }

    /**
     * Returns the specified date property from the specified sectionKey.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be retrieved.
     * @return the date property value.
     */
    public Timestamp getTimestampProperty(String sectionKey, String propertyKey) {
        return getProperty(sectionKey, propertyKey, s -> {
            Timestamp result = null;
            try {
                DateFormat dtFmt = new SimpleDateFormat(this.timeStampFormatString);
                Date dtTmp = dtFmt.parse(s);
                result = new Timestamp(dtTmp.getTime());
            } catch (ParseException ex) {
                LOG.error(String.format(PARSE_EXCEPTION_BASE_MESSAGE, this.absoluteFileNamePath, sectionKey,
                        propertyKey, "with timestamp format " + this.timeStampFormatString + "!"), ex);
            }
            return result;
        });
    }

    public <T> T getProperty(String sectionKey, String propertyKey, Function<String, T> converter) {
        T result = null;
        INISection section = this.sectionMap.get(sectionKey);
        if (section != null) {
            INIProperty objProp = section.getProperty(propertyKey);
            if (objProp != null) {
                String strVal = objProp.getPropValue();
                if (strVal != null) {
                    result = converter.apply(strVal);
                } else {
                    LOG.debug("No property with name {} available in sectionKey {}!", propertyKey, sectionKey);
                }
            } else {
                LOG.debug("No property set  for sectionKey {}, property {}!", sectionKey, propertyKey);
            }
        } else {
            LOG.debug("No sectionKey with name {} available!", sectionKey);
        }
        return result;
    }

    /*------------------------------------------------------------------------------
     * Setters
    ------------------------------------------------------------------------------*/
    /**
     * Sets the comments associated with a sectionKey.
     * 
     * @param sectionKey the sectionKey name
     * @param comments   the comments.
     */
    public void addSection(String sectionKey, String comments) {
        INISection section = this.sectionMap.get(sectionKey);
        if (section == null) {
            section = new INISection(sectionKey);
            this.sectionMap.put(sectionKey, section);
        }
        section.setSecComments(delRemChars(comments));
    }

    /**
     * Sets the specified string property.
     * 
     * @param sectionKey    the INI sectionKey name.
     * @param newSectionKey the new Section name to be set.
     */

    public void renameSection(String sectionKey, String newSectionKey, String comments) {
        INISection section = this.sectionMap.get(sectionKey);
        if (section != null) {
            this.sectionMap.put(newSectionKey, section);
            this.sectionMap.remove(sectionKey);
            section.setSecComments(delRemChars(comments));
        }
    }

    /**
     * Sets the specified string property.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be set.
     * @param value       the string value to be persisted
     */
    public void setStringProperty(String sectionKey, String propertyKey, String value, String comments) {
        setPropertyOfTypeWithComments(sectionKey, propertyKey, value, comments);
    }

    /**
     * Sets the specified boolean property.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be set.
     * @param value       the boolean value to be persisted
     */
    public void setBooleanProperty(String sectionKey, String propertyKey, boolean value, String comments) {
        setPropertyOfTypeWithComments(sectionKey, propertyKey,
                value ? INI_FILE_VALUE_STRING_TRUE : INI_FILE_VALUE_STRING_FALSE, comments);
    }

    /**
     * Sets the specified integer property.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be set.
     * @param pintVal     the int property to be persisted.
     */
    public void setIntegerProperty(String sectionKey, String propertyKey, int pintVal, String comments) {
        setPropertyOfTypeWithComments(sectionKey, propertyKey, Integer.toString(pintVal), comments);
    }

    /**
     * Sets the specified long property.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be set.
     * @param value       the long value to be persisted.
     */
    public void setLongProperty(String sectionKey, String propertyKey, long value, String comments) {
        setPropertyOfTypeWithComments(sectionKey, propertyKey, Long.toString(value), comments);
    }

    /**
     * Sets the specified double property.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be set.
     * @param value       the double value to be persisted.
     */
    public void setDoubleProperty(String sectionKey, String propertyKey, double value, String comments) {
        setPropertyOfTypeWithComments(sectionKey, propertyKey, Double.toString(value), comments);
    }

    /**
     * Sets the specified java.util.Date property.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be set.
     * @param value       the date value to be persisted.
     */
    public void setDateProperty(String sectionKey, String propertyKey, LocalDateTime value, String comments) {
        setPropertyOfTypeWithComments(sectionKey, propertyKey, utilDateToString(value, this.dateFormatString),
                comments);
    }

    /**
     * Sets the specified java.sql.Timestamp property.
     * 
     * @param sectionKey  the INI sectionKey name.
     * @param propertyKey the property to be set.
     * @param value       the timestamp value to be persisted.
     */
    public void setTimestampProperty(String sectionKey, String propertyKey, Timestamp value, String comments) {
        setPropertyOfTypeWithComments(sectionKey, propertyKey, timeToString(value, this.timeStampFormatString),
                comments);
    }

    private void setPropertyOfTypeWithComments(String sectionKey, String propertyKey, String value, String comments) {
        INISection section = this.sectionMap.computeIfAbsent(sectionKey, s -> new INISection(sectionKey));
        section.setProperty(propertyKey, value, comments);
    }

    /**
     * Sets the format to be used to interpreat date values.
     * 
     * @param dateFormatString the format string
     * @throws IllegalArgumentException if the if the given pattern is invalid
     */
    public void setDateFormat(String dateFormatString) {
        // check if the string is parsable. Go ahead when the format is ok, otherwise
        // throw an runtime exception.
        checkIfTemporalFormatStringIsParsable(dateFormatString);
        this.dateFormatString = dateFormatString;
    }

    /**
     * Sets the format to be used to interpret timestamp values.
     * 
     * @param timeStampFormatString the format string.
     * @throws IllegalArgumentException if the if the given pattern is invalid.
     */
    public void setTimeStampFormat(String timeStampFormatString) {
        // check if the string is parsable. Go ahead when the format is ok, otherwise
        // throw a RuntimeException.
        checkIfTemporalFormatStringIsParsable(dateFormatString);
        this.timeStampFormatString = timeStampFormatString;
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
     * @return the string array of sectionKey names
     */
    public String[] getAllSectionNames() {
        List<String> sectionNamesList = new ArrayList<>(this.sectionMap.keySet());
        return sectionNamesList.toArray(new String[0]);
    }

    /**
     * Returns a string array containing names of all the properties under specified
     * sectionKey.
     * 
     * @param sectionKey the name of the sectionKey for which names of properties is
     *                   to be retrieved.
     * @return the string array of property names.
     */
    public String[] getPropertyNames(String sectionKey) {
        String[] arrRet = null;
        INISection section = this.sectionMap.get(sectionKey);
        if (section != null) {
            arrRet = section.getPropNames();
        }
        return arrRet;
    }

    /**
     * Returns a map containing all the properties under specified sectionKey.
     * 
     * @param sectionKey the name of the sectionKey for which properties are to be
     *                   retrieved.
     * @return the map of properties.
     */
    public Map<String, INIProperty> getProperties(String sectionKey) {
        Map<String, INIProperty> result = null;
        INISection section = this.sectionMap.get(sectionKey);
        if (section != null) {
            result = section.getProperties();
        }
        return result;
    }

    /**
     * Removed specified property from the specified sectionKey. If the specified
     * sectionKey or the property does not exist, does nothing.
     * 
     * @param sectionKey  the sectionKey name.
     * @param propertyKey the name of the property to be removed.
     */
    public void removeProperty(String sectionKey, String propertyKey) {
        INISection section = this.sectionMap.get(sectionKey);
        if (section != null) {
            section.removeProperty(propertyKey);
        }
    }

    /**
     * Removes the specified sectionKey if one exists, otherwise does nothing.
     * 
     * @param sectionKey the name of the sectionKey to be removed.
     */
    public void removeSection(String sectionKey) {
        this.sectionMap.remove(sectionKey);
    }

    /**
     * Flush changes back to the disk file. If the disk file does not exists then
     * creates the new one.
     */
    public synchronized boolean save() {
        boolean blnRet = false;

        File objFile = new File(this.absoluteFileNamePath);
        try (FileWriter objWriter = new FileWriter(objFile)) {
            if (this.sectionMap.size() == 0) {
                LOG.warn("Nothing to save into file {}. The job is done!", this.absoluteFileNamePath);
                return false;
            }
            if (objFile.exists()) {
                LOG.warn("File {} is existing, will be deleted before saving the new content!",
                        this.absoluteFileNamePath);
                boolean deleted = objFile.delete();
                if (deleted) {
                    LOG.debug("File {} was deleted successfully. Starting the writing of the new content!",
                            this.absoluteFileNamePath);
                } else {
                    LOG.warn("Unable to delete the file {} before saving the new content!", this.absoluteFileNamePath);
                }
            } else {
                LOG.warn("File {} is not existing yet. Start saving the new content!", this.absoluteFileNamePath);
            }

            LOG.trace("Following content will be written into the file named {}.", this.absoluteFileNamePath);
            for (Map.Entry<String, INISection> entry : this.sectionMap.entrySet()) {
                INISection section = entry.getValue();
                String sectionAsString = section.toString();
                LOG.trace(sectionAsString);
                objWriter.write(sectionAsString);
                objWriter.write("\r\n");
            }
            LOG.trace("Done! All content written into the file named {}.", this.absoluteFileNamePath);
            blnRet = true;
        } catch (IOException ex) {
            LOG.error("Error during saving INI Content into file " + this.absoluteFileNamePath + "!", ex);
        }
        return blnRet;
    }

    public synchronized String saveToString() {
        StringBuilder builder = new StringBuilder();
        try {
            for (Map.Entry<String, INISection> entry : this.sectionMap.entrySet()) {
                INISection section = entry.getValue();
                builder.append(section.toString())
                       .append("\r\n");
            }
        } catch (Exception ex) {
            LOG.error("Exception in saveToString!", ex);
        }

        return builder.toString();
    }

    /*------------------------------------------------------------------------------
     * Helper functions
     *----------------------------------------------------------------------------*/
    /**
     * Helper function to check if the date time formats is parsable.
     * 
     * @param dateFormatString the date time format string to checked.
     * 
     * @throws IllegalArgumentException if the format string is not parsable by a
     *                                  {@link DateTimeFormatter}.
     */
    private void checkIfTemporalFormatStringIsParsable(String dateFormatString) {
        DateTimeFormatter.ofPattern(dateFormatString);
    }

    /**
     * Reads the INI file and load its contentens into a sectionKey collection after
     * parsing the file line by line.
     */
    private void loadFile() {
        try (FileInputStream inputStream = new FileInputStream(this.absoluteFileNamePath)) {
            readFile(inputStream);
        } catch (IOException ex) {
            LOG.error("Exception in loadFile!", ex);
            this.sectionMap.clear();
        }
    }

    private void readFile(InputStream inputStream) {
        try (InputStreamReader objFRdr = new InputStreamReader(inputStream);
                BufferedReader objBRdr = new BufferedReader(objFRdr)) {
            int iPos;
            String strSection = null;
            INISection section = null;
            String strRemarks = null;
            while (objBRdr.ready()) {
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
                    // Section start reached create new sectionKey
                    if (section != null)
                        this.sectionMap.put(strSection.trim(), section);
                    strSection = strLine.substring(1, strLine.length() - 1);
                    section = new INISection(strSection.trim(), strRemarks);
                } else if ((iPos = strLine.indexOf('=')) > 0 && section != null) {
                    // read the key value pair 012345=789
                    section.setProperty(strLine.substring(0, iPos)
                                               .trim(),
                            strLine.substring(iPos + 1)
                                   .trim(),
                            strRemarks);
                }
            }
            if (section != null)
                this.sectionMap.put(strSection.trim(), section);
        } catch (IOException | NullPointerException ex) {
            LOG.error("Exception in readFile!", ex);
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
    private String utilDateToString(LocalDateTime pdt, String pstrFmt) {
        String strRet;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pstrFmt);
            strRet = pdt.format(formatter);
        } catch (Exception ex) {
            LOG.error("Exception in utilDateToString!", ex);
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
    private String timeToString(Timestamp pobjTS, String pstrFmt) {
        String strRet;

        try {
            SimpleDateFormat dtFmt = new SimpleDateFormat(pstrFmt);
            strRet = dtFmt.format(pobjTS);
        } catch (IllegalArgumentException | NullPointerException ex) {
            LOG.error("Exception in utilDateToString!", ex);
            strRet = "";
        }
        return strRet;
    }

    /**
     * This function deletes the remark characters ';' from source string
     * 
     * @param src the source string
     * @return the converted string
     */
    private String delRemChars(String src) {
        int intPos;

        if (src == null)
            return null;
        while ((intPos = src.indexOf(';')) >= 0) {
            if (intPos == 0) {
                src = src.substring(intPos + 1);
            } else {
                src = src.substring(0, intPos) + src.substring(intPos + 1);
            }
        }
        return src;
    }



    /*------------------------------------------------------------------------------
     * Private class representing the INI Section.
     *----------------------------------------------------------------------------*/
    /**
     * Class to represent the individual ini file sectionKey.
     */
    private class INISection {

        /** Variable to hold any comments associated with this sectionKey */
        private String sectionComment;

        /** Variable to hold the sectionKey name. */
        private String sectionName;

        /** Variable to hold the properties falling under this sectionKey. */
        private LinkedHashMap<String, INIProperty> iniSectionProperties;

        /**
         * Construct a new sectionKey object identified by the name specified in
         * parameter.
         * 
         * @param sectionKey The new sections name.
         */
        INISection(String sectionKey) {
            this.sectionName = sectionKey;
            this.iniSectionProperties = new LinkedHashMap<>();
        }

        /**
         * Construct a new sectionKey object identified by the name specified in
         * parameter and associated comments.
         * 
         * @param sectionKey The new sections name.
         * @param comments   the comments associated with this sectionKey.
         */
        INISection(String sectionKey, String comments) {
            this.sectionName = sectionKey;
            this.sectionComment = delRemChars(comments);
            this.iniSectionProperties = new LinkedHashMap<>();
        }

        /**
         * Sets the comments associated with this sectionKey.
         * 
         * @param comments the comments
         */
        void setSecComments(String comments) {
            this.sectionComment = delRemChars(comments);
        }

        /**
         * Removes specified property value from this sectionKey.
         * 
         * @param propertyKey The name of the property to be removed.
         */
        void removeProperty(String propertyKey) {
            this.iniSectionProperties.remove(propertyKey);
        }

        /**
         * Creates or modifies the specified property value.
         * 
         * @param propertyKey The name of the property to be created or modified.
         * @param pstrValue   The new value for the property.
         * @param comments    the associated comments
         */
        void setProperty(String propertyKey, String pstrValue, String comments) {
            this.iniSectionProperties.put(propertyKey, new INIProperty(propertyKey, pstrValue, comments));
        }

        /**
         * Returns a map of all properties.
         * 
         * @return a map of all properties
         */
        Map<String, INIProperty> getProperties() {
            return Collections.unmodifiableMap(this.iniSectionProperties);
        }

        /**
         * Returns a string array containing names of all the properties under this
         * sectionKey.
         * 
         * @return the string array of property names.
         */
        String[] getPropNames() {
            return this.iniSectionProperties.keySet()
                                            .toArray(new String[0]);
        }

        /**
         * Returns underlying value of the specified property.
         * 
         * @param propertyKey the property whose underlying value is to be etrieved.
         * @return the property value.
         */
        INIProperty getProperty(String propertyKey) {
            INIProperty objRet = null;
            if (this.iniSectionProperties.containsKey(propertyKey)) {
                objRet = this.iniSectionProperties.get(propertyKey);
            }
            return objRet;
        }

        @Override
        public String toString() {
            StringBuilder objBuf = new StringBuilder();
            if (this.sectionComment != null) {
                objBuf.append(addRemarkCharacter(this.sectionComment));
            }
            objBuf.append("[")
                  .append(this.sectionName)
                  .append("]\r\n");
            Set<String> colKeys = this.iniSectionProperties.keySet();
            for (String colKey : colKeys) {
                INIProperty objProp = this.iniSectionProperties.get(colKey);
                objBuf.append(objProp.toString());
                objBuf.append("\r\n");
            }
            return objBuf.toString();
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
        private String propertyKey;
        /** Variable to hold value of this property */
        private String propertyValue;
        /** Variable to hold comments associated with this property */
        private String comments;

        /**
         * Constructor
         * 
         * @param propertyKey   the name of this property.
         * @param propertyValue the value of this property.
         * @param comments      the comments associated with this property.
         */
        INIProperty(String propertyKey, String propertyValue, String comments) {
            this.propertyKey = propertyKey;
            this.propertyValue = propertyValue;
            this.comments = delRemChars(comments);
        }

        /**
         * Returns value of this property. If value contains a reference to environment
         * variable then this reference is replaced by actual value before the value is
         * returned.
         * 
         * @return the value of this property.
         */
        String getPropValue() {
            final Pattern environmentVariableRegEx = Pattern.compile("%(?<environmentVariableKey>.+)%");
            String result;
            Matcher matcher = environmentVariableRegEx.matcher(this.propertyValue);
            if (matcher.matches()) {
                LOG.debug("Property value {} could be an environment variable, we will try to resolve the value.",
                        this.propertyValue);
                String environmentVariableKey = matcher.group("environmentVariableKey");
                result = System.getenv(environmentVariableKey);
                if (result == null) {
                    LOG.debug(
                            "Property value {} could not be resolved into an environment variable. The property value itself is returned.",
                            this.propertyValue);
                    result = this.propertyValue;
                } else {
                    LOG.debug("Property value {} is environment variable with value {}.", this.propertyValue, result);
                }
            } else {
                LOG.debug("Property value {} is no environment variable. The property value itself is returned.",
                        this.propertyValue);
                result = this.propertyValue;
            }
            return result;
        }

        @Override
        public String toString() {
            String result = "";
            if (this.comments != null) {
                result = addRemarkCharacter(comments);
            }
            return result + this.propertyKey + " = " + this.propertyValue;
        }
    }

    /**
     * This function adds a remark character ';' in source string.
     *
     * @param src source string
     * @return converted string.
     */
    private String addRemarkCharacter(String src) {
        int intLen;
        int intPos = 0;
        int intPrev = 0;

        String strLeft;
        String strRight;

        if (src == null)
            return null;
        while (intPos >= 0) {
            intLen = 2;
            intPos = src.indexOf("\r\n", intPrev);
            if (intPos < 0) {
                intLen = 1;
                intPos = src.indexOf('\n', intPrev);
                if (intPos < 0)
                    intPos = src.indexOf('\r', intPrev);
            }
            if (intPos == 0) {
                src = ";\r\n" + src.substring(intPos + intLen);
                intPrev = intPos + intLen + 1;
            } else if (intPos > 0) {
                strLeft = src.substring(0, intPos);
                strRight = src.substring(intPos + intLen);
                if (strRight == null)
                    src = strLeft;
                else if (strRight.length() == 0)
                    src = strLeft;
                else
                    src = strLeft + "\r\n;" + strRight;
                intPrev = intPos + intLen + 1;
            }
        }
        if (!src.substring(0, 1)
                .equals(";"))
            src = ";" + src;
        src = src + "\r\n";
        return src;
    }
}
