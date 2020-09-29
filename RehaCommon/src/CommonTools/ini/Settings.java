package CommonTools.ini;

import java.io.InputStream;

public interface Settings {

    /**
     * Returns the ini file name being used.
     *
     * @return the INI file name.
     */
    String getFileName();

    InputStream getInputStream();

    /**
     * Returns the specified string property from the specified section.
     *
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be retrieved.
     * @return the string property value.
     */
    String getStringProperty(String pstrSection, String pstrProp);

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
    Boolean getBooleanProperty(String pstrSection, String pstrProp);

    /**
     * Returns the specified integer property from the specified section.
     *
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be retrieved.
     * @return the integer property value.
     */
    Integer getIntegerProperty(String pstrSection, String pstrProp);

    /**
     * Returns the specified long property from the specified section.
     *
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be retrieved.
     * @return the long property value.
     */
    Long getLongProperty(String pstrSection, String pstrProp);

    /*------------------------------------------------------------------------------
     * Setters
    ------------------------------------------------------------------------------*/
    /**
     * Sets the comments associated with a section.
     *
     * @param pstrSection  the section name
     * @param pstrComments the comments.
     */
    void addSection(String pstrSection, String pstrComments);

    /**
     * Sets the specified string property.
     *
     * @param pstrSection    the INI section name.
     * @param newpstrSection the new Section name to be set.
     * @pstrVal the string value to be persisted
     */

    void renameSection(String pstrSection, String newpstrSection, String pstrComments);

    /**
     * Sets the specified string property.
     *
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be set.
     * @pstrVal the string value to be persisted
     */
    void setStringProperty(String pstrSection, String pstrProp, String pstrVal, String pstrComments);

    /**
     * Sets the specified boolean property.
     *
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be set.
     * @param pblnVal     the boolean value to be persisted
     */
    void setBooleanProperty(String pstrSection, String pstrProp, boolean pblnVal, String pstrComments);

    /**
     * Sets the specified integer property.
     *
     * @param pstrSection the INI section name.
     * @param pstrProp    the property to be set.
     * @param pintVal     the int property to be persisted.
     */
    void setIntegerProperty(String pstrSection, String pstrProp, int pintVal, String pstrComments);

    /**
     * Sets the format to be used to interpreat date values.
     *
     * @param pstrDtFmt the format string
     * @throws IllegalArgumentException if the if the given pattern is invalid
     */
    void setDateFormat(String pstrDtFmt) throws IllegalArgumentException;

    /**
     * Sets the format to be used to interpreat timestamp values.
     *
     * @param pstrTSFmt the format string
     * @throws IllegalArgumentException if the if the given pattern is invalid
     */
    void setTimeStampFormat(String pstrTSFmt);

    /*------------------------------------------------------------------------------
     * Public methods
    ------------------------------------------------------------------------------*/
    int getTotalSections();

    /**
     * Returns a string array containing names of all sections in INI file.
     *
     * @return the string array of section names
     */
    String[] getAllSectionNames();

    /**
     * Removes the specified section if one exists, otherwise does nothing.
     *
     * @param pstrSection the name of the section to be removed.
     */
    void removeSection(String pstrSection);

    /**
     * Flush changes back to the disk file. If the disk file does not exists then
     * creates the new one.
     */
    boolean save();

    StringBuffer saveToStringBuffer();

}