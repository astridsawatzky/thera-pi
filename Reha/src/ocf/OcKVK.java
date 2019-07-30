package ocf;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import CommonTools.StringTools;
import egk.CardListener;
import egk.CardTerminalEvent;
import environment.Path;
import hauptFenster.Reha;
import systemEinstellungen.SystemConfig;

public class OcKVK implements CardListener{

    // CLA || INS || P1 || P2 || Le (= erwartete Länge der Daten)
    final byte[] CMD_READ_BINARY = { (byte) 0x00, (byte) 0xB0, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
    final byte[] CMD_READ_BINARY_EF = { (byte) 0x00, (byte) 0xB0, (byte) 0x81, (byte) 0x00, (byte) 0x02 };
    final byte[] CMD_SELECT_KVKFILE = { (byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x06, (byte) 0xD2,
            (byte) 0x76, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01 };
    final byte[] CMD_SELECT_EGK_HDC = { (byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x0C, (byte) 0x06, (byte) 0xD2,
            (byte) 0x76, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x02 };
    final byte[] CMD_SELCT_VD = { (byte) 0x00, (byte) 0xA4, (byte) 0x02, (byte) 0x04, (byte) 0x02, (byte) 0xD0,
            (byte) 0x02, (byte) 0x00 };
    final byte[] CMD_SELCT_PD = { (byte) 0x00, (byte) 0xA4, (byte) 0x02, (byte) 0x04, (byte) 0x02, (byte) 0xD0,
            (byte) 0x01, (byte) 0x00 };

    ByteArrayInputStream in = null;
    ByteArrayOutputStream out = null;

    // Wird später ersetzt durch kvkTags
    final static String[] hmProperty = { "Rohdaten", "Krankenkasse", "Kassennummer", "Kartennummer",
            "Versichertennummer", "Status", "Statusext", "Titel", "Vorname", "Namenszusatz", "Nachname", "Geboren",
            "Strasse", "Land", "Plz", "Ort", "Gueltigkeit", "Checksumme", "Anrede" };
    // Wird später ersetzt durch kvkTags
    final int[] tags = { 0x60, 0x80, 0x81, 0x8F, 0x82, 0x83, 0x90, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8A, 0x8B, 0x8C,
            0x8D, 0x8E };
    private boolean mustdebug = false;

    String vsdPraefix = "";
    // Integer Tag-Identifier
    // String (Thera-Pi interner)
    // Tag-Name
    // boolean Optional
    // Integer Länge
    // String Inhalt
    static Object[][] kvkTags = { { 0x60, "Rohdaten", false, 0, "" }, { 0x80, "Krankenkasse", false, 0, "" },
            { 0x81, "Kassennummer", false, 0, "" }, { 0x8F, "Kartennummer", false, 0, "" },
            { 0x82, "Versichertennummer", false, 0, "" }, { 0x83, "Status", false, 0, "" },
            { 0x90, "Statusext", false, 0, "" }, { 0x84, "Titel", true, 0, "" }, { 0x85, "Vorname", false, 0, "" },
            { 0x86, "Namenszusatz", true, 0, "" }, { 0x87, "Nachname", false, 0, "" },
            { 0x88, "Geboren", false, 0, "" }, { 0x89, "Strasse", false, 0, "" }, { 0x8A, "Land", true, 0, "" },
            { 0x8B, "Plz", false, 0, "" }, { 0x8C, "Ort", false, 0, "" }, { 0x8D, "Gueltigkeit", false, 0, "" },
            { 0x8E, "Checksumme", false, 0, "" } };

    public boolean terminalOk = false;
    public boolean cardOk = false;

    public Vector<Vector<String>> vecreader = new Vector<Vector<String>>();
    // public String aktDeviceId;

    private CommandAPDU command;
    private ATR i;
    private int n, x;
    private String s, satrId;

    public boolean isCardReady;

    ResponseAPDU response;
    boolean blockIKKasse = false;
    String ikktraeger = "";
    String namektraeger = "";

    BufferedReader br = null;

    StringBuffer neustring = new StringBuffer();

    public static boolean lastCardIsEGK = false;
    CardListener listen;
    private Logger  logger = LoggerFactory.getLogger(OcKVK.class);
    public OcKVK() throws Exception, UnsatisfiedLinkError {
        // SCR335
        // ctpcsc31kv
        // nur in der Testphase der testphase
        SystemConfig.hmKVKDaten = new HashMap<String, String>();
        // danach wird das Gedönse in der SystemConfig initialisiert.

        terminalOk = true;
        listen = new CardListener(this);

    }

    public void lesen(CardTerminal cardTerminal) throws ClassNotFoundException, CardException {
        lastCardIsEGK = false;
        blockIKKasse = false;

        // Establish a connection with the card:
        Card sc = cardTerminal.connect("*");
        CardChannel ptcs = sc.getBasicChannel();
        // (PassThruCardService) sc.getCardService(PassThruCardService.class, true);
        /***** Karte testen *****/
        i = sc.getATR();
        s = "";
        satrId = "";
        for (n = 0; n < i.getBytes().length; n++) {
            x = 0x000000FF & i.getBytes()[n];
            s = Integer.toHexString(x)
                       .toUpperCase();
            if (s.length() == 1)
                s = "0" + s;
            satrId = satrId + String.valueOf(s);
        }
        if (satrId.trim()
                  .equals("AAFFFFFF")) {
            System.out.println("keine KV-Karte oder Karte defekt");
            // Karte ist keine KV-Karte, sondern eine x-beliebige Speicherkarte
            sc.disconnect(false);
            ;
        }
        command = new CommandAPDU(CMD_SELECT_KVKFILE);

        response = ptcs.transmit(command);
        if (response == null || response.getBytes().length == 0) {
            System.out.println("keine KV-Karte oder Karte defekt");
            sc.disconnect(false);
        }

        if (getResponseValue(response.getBytes()).equals("9000")) {
            // es ist eine KVK
            command = new CommandAPDU(CMD_READ_BINARY);
            response = ptcs.transmit(command);

            if (response.getBytes()[0] == (byte) 0x60) { // Nach ASN.1 Standard der KVK
                checkKVK_ASN1(response.getBytes(), kvkTags);
                sc.disconnect(false);
            } else {
                // Hier entweder neue Routine, falls betriebsintern
                // noch anderweitige Chipkarten eingesetzt werden z.B. Zugangskontrolle etc.
                // oder Fehlermeldung daß Karte keine KV-Karte ist.
                sc.disconnect(false);
            }
        } else {
            // Hier testen ob es eine eGK ist
            command = new CommandAPDU(CMD_SELECT_EGK_HDC);
            response = ptcs.transmit(command);
            if (getResponseValue(response.getBytes()).equals("9000")) {
                // ja es ist eine eGK;
                lastCardIsEGK = true;
                command = new CommandAPDU(this.CMD_READ_BINARY_EF);
                response = ptcs.transmit(command);
                // System.out.println("Response = "+getResponseValue(response.getBytes()));
                /*********** PD-Daten ********************/
                byte[] resultpd = new byte[850];
                byte[] offset = { (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04 };
                byte[] cmd = { (byte) 0x00, (byte) 0xB0, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
                int bytes;
                int zaehler = 0;
                try {
                    for (int of = 0; of < 4; of++) {
                        cmd[2] = offset[of];
                        command = new CommandAPDU(cmd);
                        response = ptcs.transmit(command);
                        /*********************/
                        bytes = response.getBytes().length;
                        for (n = 0; n < bytes; n++) {
                            if ((n < (bytes - 2))) {
                                try {
                                    if (((of == 0 && n > 1) || (of > 0)) /* && (zaehler < lang) */ ) {
                                        resultpd[zaehler] = (byte) response.getBytes()[n];
                                        zaehler++;
                                    }
                                } catch (Exception ex) {
                                    System.out.println("Fehler bei Zähler: " + zaehler);
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String resultString = null;
                try {

                    in = new ByteArrayInputStream(resultpd);
                    // System.out.println(new String(resultpd));
                    /***** ausschalten nach Test ******/
                    InDatei(Path.Instance.getProghome() + "temp/" + Reha.getAktIK() + "/eGKpd.zip", resultpd);
                    out = Unzip("", in);
                    in.close();
                    out.flush();
                    out.close();
                    resultString = new String(out.toByteArray()).replace("vsdp:", "")
                                                                .replace("vsda:", "")
                                                                .replace("vsdg:", "")
                                                                .replace("vsd:", "");
                    SystemConfig.hmKVKDaten.clear();
                    XML_PD_Parser(resultString.getBytes());

                } catch (Exception ex) {
                    ex.printStackTrace();
                    SystemConfig.hmKVKDaten.clear();
                    sc.disconnect(false);
                    try {
                        in.close();
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /*******************************
                 * VD-Daten**************************
                 *
                 *
                 *****/
                ikktraeger = "";
                namektraeger = "";

                command = new CommandAPDU(this.CMD_SELCT_VD);
                response = ptcs.transmit(command);
                byte[] resultvd = new byte[1250];
                zaehler = 0;

                /**********************************************************/
                for (int of = 0; of < 5; of++) {
                    cmd[2] = offset[of];
                    command = new CommandAPDU(cmd);
                    response = ptcs.transmit(command);
                    /*********************/
                    bytes = response.getBytes().length;
                    for (n = 0; n < bytes; n++) {
                        if ((n < (bytes - 2))) {
                            try {
                                if (((of == 0 && n > 7) || (of > 0))) {
                                    resultvd[zaehler] = (byte) response.getBytes()[n];
                                    zaehler++;
                                }

                            } catch (Exception ex) {
                                System.out.println("Fehler bei Zähler: " + zaehler);
                                ex.printStackTrace();
                            }
                        }
                    }
                }
                try {
                    in = new ByteArrayInputStream(resultvd);
                    /***** ausschalten nach Test ******/
                    InDatei(Path.Instance.getProghome() + "temp/" + Reha.getAktIK() + "/eGKvd.zip", resultvd);
                    out = Unzip("", in);
                    in.close();
                    out.flush();
                    out.close();
                    resultString = new String(out.toByteArray()).replace("vsdp:", "")
                                                                .replace("vsda:", "")
                                                                .replace("vsdg:", "")
                                                                .replace("vsd:", "");

                    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                    domFactory.setValidating(false);
                    DocumentBuilder builder = null;
                    try {
                        builder = domFactory.newDocumentBuilder();
                        builder.setErrorHandler(new ErrorHandler() {
                            @Override
                            public void error(SAXParseException exception) throws SAXException {
                                exception.printStackTrace();
                            }

                            @Override
                            public void fatalError(SAXParseException exception) throws SAXException {
                                exception.printStackTrace();
                            }

                            @Override
                            public void warning(SAXParseException exception) throws SAXException {
                                exception.printStackTrace();
                            }

                        });
                        // Die beiden Dateien aufteilen
                        String rs1 = resultString.substring(0, resultString.lastIndexOf("<?xml version"));
                        String rs2 = resultString.substring(resultString.lastIndexOf("<?xml version"));
                        if (mustdebug) {
                            InDatei(Path.Instance.getProghome() + "temp/" + Reha.getAktIK() + "/eGKvd", rs1.getBytes());
                            InDatei(Path.Instance.getProghome() + "temp/" + Reha.getAktIK() + "/eGKzd", rs2.getBytes());
                        }

                        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(rs1.getBytes()));

                        // wird nicht gebraucht
                        NodeList list = doc.getElementsByTagName("Beginn");

                        list = doc.getElementsByTagName("Kostentraegerkennung");
                        ikktraeger = list.item(0)
                                         .getTextContent()
                                         .toString()
                                         .substring(2);
                        SystemConfig.hmKVKDaten.put("Kassennummer", list.item(1)
                                                                        .getTextContent()
                                                                        .toString()
                                                                        .substring(2));

                        list = doc.getElementsByTagName("Name");
                        namektraeger = list.item(0)
                                           .getTextContent();
                        SystemConfig.hmKVKDaten.put("Krankenkasse", list.item(1)
                                                                        .getTextContent());

                        list = doc.getElementsByTagName("Versichertenart");
                        SystemConfig.hmKVKDaten.put("Status", list.item(0)
                                                                  .getTextContent());

                        // element wird nicht von jeder Kasse angegeben
                        list = doc.getElementsByTagName("Ende");
                        if (list != null) {
                            try {
                                String ende = list.item(0)
                                                  .getTextContent()
                                                  .trim();
                                SystemConfig.hmKVKDaten.put("Gueltigkeit", ende.substring(4, 6) + ende.substring(2, 4));
                            } catch (Exception ex) {
                                // System.out.println("1\n"+SystemConfig.hmKVKDaten+"\nFehler bei Ende der
                                // Gültigkeit");
                            }
                        }
                        // wird nicht gebraucht
                        // list = doc.getElementsByTagName("WOP");

                    } catch (ParserConfigurationException e1) {
                        e1.printStackTrace();
                        System.out.println("2\n" + SystemConfig.hmKVKDaten);
                    } catch (SAXException e) {
                        e.printStackTrace();
                        System.out.println("3\n" + SystemConfig.hmKVKDaten);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("4\n" + SystemConfig.hmKVKDaten);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    SystemConfig.hmKVKDaten.clear();
                    sc.disconnect(false);
                    try {
                        in.close();
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                sc.disconnect(false);
            } else {
                // es ist auch keine eGK;
                sc.disconnect(false);
                try {
                    in.close();
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        sc.disconnect(false);
    }

    private void XML_PD_Parser(byte[] result) {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setValidating(false);
        DocumentBuilder builder = null;
        try {
            builder = domFactory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void error(SAXParseException exception) throws SAXException {
                    exception.printStackTrace();
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    exception.printStackTrace();
                }

                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    exception.printStackTrace();
                }

            });
            org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(result));
            Node node = doc.getLastChild();
            NodeList nodeList = node.getChildNodes();
            NodeList subNodeList = null;
            int basic = 0;
            for (basic = 0; basic < nodeList.getLength(); basic++) {
                if (nodeList.item(basic)
                            .getNodeName()
                            .toString()
                            .equalsIgnoreCase("Versicherter")) {
                    subNodeList = nodeList.item(basic)
                                          .getChildNodes();
                    break;
                }
            }
            for (int x = 0; x < subNodeList.getLength(); x++) {
                if (subNodeList.item(x)
                               .hasChildNodes()
                        && subNodeList.item(x)
                                      .getChildNodes()
                                      .getLength() != 1) {
                    NodeList subNodeList2 = subNodeList.item(x)
                                                       .getChildNodes();
                    for (int i = 0; i < subNodeList2.getLength(); i++) {
                        if (subNodeList2.item(i)
                                        .hasChildNodes()
                                && subNodeList2.item(i)
                                               .getChildNodes()
                                               .getLength() != 1) {
                            NodeList subNodeList3 = subNodeList2.item(i)
                                                                .getChildNodes();
                            for (int y = 0; y < subNodeList3.getLength(); y++) {
                                if (subNodeList3.item(y)
                                                .hasChildNodes()
                                        && subNodeList3.item(y)
                                                       .getChildNodes()
                                                       .getLength() != 1) {
                                    NodeList subNodeList4 = subNodeList3.item(y)
                                                                        .getChildNodes();
                                    for (int y1 = 0; y1 < subNodeList3.getLength(); y1++) {
                                        try {
                                            testePD2(subNodeList4.item(y1)
                                                                 .getNodeName(),
                                                    subNodeList4.item(y1)
                                                                .getTextContent());
                                        } catch (Exception ex) {
                                        }
                                    }
                                } else {
                                    testePD2(subNodeList3.item(y)
                                                         .getNodeName(),
                                            subNodeList3.item(y)
                                                        .getTextContent());

                                }
                            }
                        } else {
                            testePD2(subNodeList2.item(i)
                                                 .getNodeName(),
                                    subNodeList2.item(i)
                                                .getTextContent());
                        }
                    }
                } else {
                    testePD2(subNodeList.item(x)
                                        .getNodeName(),
                            subNodeList.item(x)
                                       .getTextContent());
                }
            }
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void InDatei(String datei, byte[] bytes) {

        try {
            FileOutputStream fos = new FileOutputStream(datei);
            fos.write(bytes);
            fos.close();
        } catch (Exception ex) {

        }
    }

    private String getResponseValue(byte[] by) {
        String wert = "";
        int bytes = by.length;
        int x;
        for (n = 0; n < bytes; n++) {
            x = 0x000000FF & by[n];
            s = Integer.toHexString(x)
                       .toUpperCase();
            if (s.length() == 1)
                s = "0" + s;
            if ((n >= (bytes - 2))) {
                try {
                    wert = wert + s;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return wert;
    }

    /**********
    *
    *
    *
    *
    */
    private void testePD2(String key, String value) {
        // System.out.println(key + " = " + value);
        try {
            if (key.equals("Versicherten_ID")) {
                SystemConfig.hmKVKDaten.put("Versichertennummer", value);
                return;
            } else if (key.equals("Geburtsdatum")) {

                SystemConfig.hmKVKDaten.put("Geboren",
                        value.substring(6) + value.substring(4, 6) + value.substring(0, 4));
                return;
            } else if (key.equals("Vorname")) {
                SystemConfig.hmKVKDaten.put("Vorname", value);
                return;
            } else if (key.equals("Nachname")) {
                SystemConfig.hmKVKDaten.put("Nachname", value);
                return;
            } else if (key.equals("Titel")) {
                SystemConfig.hmKVKDaten.put("Titel", value);
                return;
            } else if (key.equals("Namenszusatz")) {
                SystemConfig.hmKVKDaten.put("Namenszusatz", value);
                return;
            } else if (key.equals("Postleitzahl")) {
                SystemConfig.hmKVKDaten.put("Plz", value);
                return;
            } else if (key.equals("Ort")) {
                SystemConfig.hmKVKDaten.put("Ort", value);
                return;
            } else if (key.equals("Geschlecht")) {
                SystemConfig.hmKVKDaten.put("Anrede", (value.equals("M") ? "HERR" : "FRAU"));
                return;
            } else if (key.equals("Strasse")) {
                SystemConfig.hmKVKDaten.put("Strasse", value);
                return;
            } else if (key.equals("Hausnummer")) {
                SystemConfig.hmKVKDaten.put("Strasse", SystemConfig.hmKVKDaten.get("Strasse") + " " + value);
                return;
            }
        } catch (Exception ex) {
        }
    }

    /*******
     *
     * @param inFilePath
     * @param in
     * @return
     * @throws Exception
     */
    private ByteArrayOutputStream Unzip(String inFilePath, ByteArrayInputStream in) throws Exception {
        ByteArrayOutputStream out = null;
        try {
            GZIPInputStream gzipInputStream = null;
            gzipInputStream = new GZIPInputStream(in);
            out = new ByteArrayOutputStream();

            byte[] buf = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buf)) > 0)
                out.write(buf, 0, len);

            gzipInputStream.close();
            out.close();
            if (in == null)
                new File(inFilePath).delete();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return out;
    }

    // Kann später gelöscht werden wird ersetzt durch die Methode
    public HashMap<String, String> getKVKDaten(byte[] daten) {
        SystemConfig.hmKVKDaten.clear();
        // System.out.println("Eintritt in getKVKDaten");
        try {
            byte[] bytes = daten; // daten.getBytes();
            String string = new String(daten);
            int found = -1;
            int lang;
            int stand = 0;
            for (int y = 1; y < tags.length; y++) {
                found = -1;
                for (int i = stand; i < bytes.length; i++) {
                    if (bytes[i] == (byte) tags[y]) {
                        found = i;
                        stand = i;
                        break;
                    }

                }
                if (found >= 0) {
                    try {
                        lang = 0x000000FF & bytes[found + 1];
                        found = found + 2;
                        SystemConfig.hmKVKDaten.put(hmProperty[y],
                                StringTools.do301NormalizeString(string.substring(found, found + lang)));
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                        // Karte ist keine KV-Karte, Anzahl Tags stimmt nicht.
                        JOptionPane.showMessageDialog(null, "keine KV-Karte oder Karte defekt");
                        SystemConfig.hmKVKDaten.clear();
                        isCardReady = false;
                        return SystemConfig.hmKVKDaten;
                    }
                } else {
                    try {
                        SystemConfig.hmKVKDaten.put(hmProperty[y], "");
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                        // Karte ist keine KV-Karte, Anzahl Tags stimmt nicht.
                        JOptionPane.showMessageDialog(null, "keine KV-Karte oder Karte defekt");
                        SystemConfig.hmKVKDaten.clear();
                        isCardReady = false;
                        return SystemConfig.hmKVKDaten;
                    }
                }
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
            // Karte ist keine KV-Karte, Anzahl Tags stimmt nicht.
            System.out.println("keine KV-Karte oder Karte defekt");
            SystemConfig.hmKVKDaten.clear();
            isCardReady = false;
            return SystemConfig.hmKVKDaten;
        }
        if (SystemConfig.hmKVKDaten.isEmpty()) {
            System.out.println("keine KV-Karte oder Karte defekt");
            SystemConfig.hmKVKDaten.clear();
            isCardReady = false;
        }
        return SystemConfig.hmKVKDaten;

    }

    private HashMap<String, String> checkKVK_ASN1(byte[] response, Object[][] tags) {
        int dataLength = -1;
        int tagLength = -1;
        int startByte = -1;
        int i = -1, i2 = -1;
        byte[] value = null;
        if ((0x000000FF & response[0]) != (Integer) tags[0][0]) {
            JOptionPane.showMessageDialog(null, "Chip-Karte ist defekt oder keine KV-Karte!");
            SystemConfig.hmKVKDaten.clear();
            // System.out.println("Die Karte ist keine KV-Karte");
            SystemConfig.hmKVKDaten.clear();
            return SystemConfig.hmKVKDaten;
        } else {
            dataLength = 0x000000FF & response[1];
        }

        // Falls nicht sofort mit dem Tag 1 begonnen wird;
        for (i = 2; i < dataLength; i++) {
            if ((0x000000FF & response[i]) == (Integer) tags[1][0]) {
                // System.out.println("Tag 1 beginnt bei Byte "+i);
                startByte = Integer.valueOf(i);
                break;
            }
        }

        if (startByte < 0) {
            /* System.out.println("Fehler Tag 1 nicht gefunden"); */
            SystemConfig.hmKVKDaten.clear();
            return SystemConfig.hmKVKDaten;
        }
        SystemConfig.hmKVKDaten.put("Anrede", "HERR");
        for (i = 1; i < tags.length; i++) {
            // Wenn eines der optionalen Tags nicht vorhanden ist...
            if ((0x000000FF & response[startByte]) != (Integer) tags[i][0]) {
                // kvinhalte.put((String)tags[i][1],"");
                if (!(Boolean) tags[i][2]) {
                    JOptionPane.showMessageDialog(null,
                            "Das Pflichtfeld " + (String) tags[i][1] + " ist auf der Karte nicht vorhanden");
                    SystemConfig.hmKVKDaten.clear();
                    return SystemConfig.hmKVKDaten;
                }
                SystemConfig.hmKVKDaten.put((String) tags[i][1], "");
                continue;
            }
            startByte += 1;
            tagLength = 0x000000FF & response[startByte];
            startByte += 1;
            value = new byte[tagLength];
            for (i2 = startByte; i2 < startByte + tagLength; i2++) {
                value[i2 - startByte] = response[i2];
            }
            // kvinhalte.put((String)tags[i][1],new String(value));
            SystemConfig.hmKVKDaten.put((String) tags[i][1], StringTools.do301NormalizeString(new String(value)));
            startByte += tagLength;
        }
        return SystemConfig.hmKVKDaten;

    }

    public class CardListener  {
        private Card smartcard = null;
        private CardTerminal terminal = null;
        private int slotID = 0;
        OcKVK eltern = null;

        public CardListener(OcKVK xeltern) {
            eltern = xeltern;
        }

        public void cardInserted(CardTerminalEvent event) throws CardException {

            System.out.println("karte eingesteckt");
            if (smartcard == null) {

                smartcard = event.getSmartCard();
                terminal = event.getCardTerminal();
                slotID = event.getSlotID();
                eltern.isCardReady = false;
                try {
                    eltern.lesen(terminal);
                    eltern.isCardReady = true;
                    if (Reha.instance.patpanel != null) {
                        if (Reha.instance.patpanel.getLogic().pneu != null) {
                            new SwingWorker<Void, Void>() {
                                @Override
                                protected Void doInBackground() throws Exception {
                                    Reha.instance.patpanel.getLogic().pneu.enableReaderButton();
                                    return null;
                                }
                            }.execute();
                        }
                    }
                } catch (CardException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                smartcard = event.getSmartCard();
                terminal = event.getCardTerminal();
                slotID = event.getSlotID();
                eltern.isCardReady = false;
            }
        }

        public void cardRemoved(CardTerminalEvent event) {
             System.out.println("karte ausgezogen");
            if ((event.getSlotID() == slotID) && (event.getCardTerminal() == terminal)) {
                smartcard = null;
                terminal = null;
                slotID = -1;
                eltern.isCardReady = false;
                if (Reha.instance.patpanel != null) {
                    if (Reha.instance.patpanel.getLogic().pneu != null) {
                        new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                Reha.instance.patpanel.getLogic().pneu.disableReaderButton();
                                return null;
                            }
                        }.execute();
                    }
                }

            } else {
                // System.out.println("anderer Slot oder anderer Terminal");
            }
        }

    }

@Override
public void cardInserted(CardTerminalEvent cardTerminalEvent) {
    System.out.println("yay in ockvk");

    logger.debug(cardTerminalEvent.getCardTerminal().getName());
    try {
        listen.cardInserted(cardTerminalEvent);
    } catch (CardException e) {
        System.out.println(e);
    }

}

@Override
public void cardRemoved(CardTerminalEvent cardTerminalEvent) {
   listen.cardRemoved(cardTerminalEvent);

}
}
