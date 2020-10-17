package org.therapi.reha.patient.Berichte;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mandant.IK;
import sql.DatenquellenFactory;

public class GutachtenDto {

    private Logger logger = LoggerFactory.getLogger(GutachtenDto.class);

    private static final String dbTabellenName="bericht2";

    private IK ik;

    private Gutachten ofResultset(ResultSet rs) {
        Gutachten ret = new Gutachten();

        ResultSetMetaData meta;
        try {
            meta = rs.getMetaData();

            for(int o=1;o<=meta.getColumnCount();o++) {
                String field = meta.getColumnLabel(o).toUpperCase();
                switch (field) {

                case "PAT_INTERN":
                    ret.setPatIntern(rs.getString(field));
                    break;
                case "BERICHTID":
                    ret.setBerichtId(rs.getInt(field));
                    break;
                case "VNUMMER":
                    ret.setvNummer(rs.getString(field));
                    break;
                case "NAMEVOR":
                    ret.setNameVor(rs.getString(field));
                    break;
                case "GEBOREN":
                    ret.setGeboren(rs.getDate(field) == null ? null : rs.getDate(field).toLocalDate());
                    break;
                case "STRASSE":
                    ret.setStrasse(rs.getString(field));
                    break;
                case "PLZ":
                    ret.setPlz(rs.getString(field));
                    break;
                case "ORT":
                    ret.setOrt(rs.getString(field));
                    break;
                case "VNAMEVO":
                    ret.setvNameVO(rs.getString(field));
                    break;
                case "MSNR":
                    ret.setMsNr(rs.getString(field));
                    break;
                case "BNR":
                    ret.setbNr(rs.getString(field));
                    break;
                case "AUFDAT1":
                    ret.setAufDat1(rs.getDate(field) == null ? null : rs.getDate(field).toLocalDate());
                    break;
                case "ENTDAT1":
                    ret.setEntDat1(rs.getDate(field) == null ? null : rs.getDate(field).toLocalDate());
                    break;
                case "AUFDAT2":
                    ret.setAufDat2(rs.getDate(field) == null ? null : rs.getDate(field).toLocalDate());
                    break;
                case "ENTDAT2":
                    ret.setEntDat2(rs.getDate(field) == null ? null : rs.getDate(field).toLocalDate());
                    break;
                case "AUFDAT3":
                    ret.setAufDat3(rs.getDate(field) == null ? null : rs.getDate(field).toLocalDate());
                    break;
                case "ENTDAT3":
                    ret.setEntDat3(rs.getDate(field) == null ? null : rs.getDate(field).toLocalDate());
                    break;
                case "ENTFORM":
                    ret.setEntform(rs.getString(field));
                    break;
                case "ARBFAE":
                    ret.setArbfae(rs.getString(field));
                    break;
                case "DIAG1":
                    ret.setDiag1(rs.getString(field));
                    break;
                case "F_74":
                    ret.setF74(rs.getString(field));
                    break;
                case "F_79":
                    ret.setF79(rs.getString(field));
                    break;
                case "F_80":
                    ret.setF80(rs.getString(field));
                    break;
                case "F_81":
                    ret.setF81(rs.getString(field));
                    break;
                case "DIAG2":
                    ret.setDiag2(rs.getString(field));
                    break;
                case "F_82":
                    ret.setF82(rs.getString(field));
                    break;
                case "F_87":
                    ret.setF87(rs.getString(field));
                    break;
                case "F_88":
                    ret.setF88(rs.getString(field));
                    break;
                case "F_89":
                    ret.setF89(rs.getString(field));
                    break;
                case "DIAG3":
                    ret.setDiag3(rs.getString(field));
                    break;
                case "F_90":
                    ret.setF90(rs.getString(field));
                    break;
                case "F_95":
                    ret.setF95(rs.getString(field));
                    break;
                case "F_96":
                    ret.setF96(rs.getString(field));
                    break;
                case "F_97":
                    ret.setF97(rs.getString(field));
                    break;
                case "DIAG4":
                    ret.setDiag4(rs.getString(field));
                    break;
                case "F_98":
                    ret.setF98(rs.getString(field));
                    break;
                case "F_103":
                    ret.setF103(rs.getString(field));
                    break;
                case "F_104":
                    ret.setF104(rs.getString(field));
                    break;
                case "F_105":
                    ret.setF105(rs.getString(field));
                    break;
                case "DIAG5":
                    ret.setDiag5(rs.getString(field));
                    break;
                case "F_106":
                    ret.setF106(rs.getString(field));
                    break;
                case "F_111":
                    ret.setF111(rs.getString(field));
                    break;
                case "F_112":
                    ret.setF112(rs.getString(field));
                    break;
                case "F_113":
                    ret.setF113(rs.getString(field));
                    break;
                case "F_114":
                    ret.setF114(rs.getString(field));
                    break;
                case "F_117":
                    ret.setF117(rs.getString(field));
                    break;
                case "F_120":
                    ret.setF120(rs.getString(field));
                    break;
                case "F_123":
                    ret.setF123(rs.getString(field));
                    break;
                case "F_124":
                    ret.setF124(rs.getString(field));
                    break;
                case "F_125":
                    ret.setF125(rs.getString(field));
                    break;
                case "F_126":
                    ret.setF126(rs.getString(field));
                    break;
                case "F_127":
                    ret.setF127(rs.getString(field));
                    break;
                case "F_128":
                    ret.setF128(rs.getString(field));
                    break;
                case "F_129":
                    ret.setF129(rs.getString(field));
                    break;
                case "F_130":
                    ret.setF130(rs.getString(field));
                    break;
                case "F_131":
                    ret.setF131(rs.getString(field));
                    break;
                case "F_132":
                    ret.setF132(rs.getString(field));
                    break;
                case "F_133":
                    ret.setF133(rs.getString(field));
                    break;
                case "F_134":
                    ret.setF134(rs.getString(field));
                    break;
                case "F_135":
                    ret.setF135(rs.getString(field));
                    break;
                case "F_136":
                    ret.setF136(rs.getString(field));
                    break;
                case "F_137":
                    ret.setF137(rs.getString(field));
                    break;
                case "F_138":
                    ret.setF138(rs.getString(field));
                    break;
                case "F_139":
                    ret.setF139(rs.getString(field));
                    break;
                case "F_140":
                    ret.setF140(rs.getString(field));
                    break;
                case "F_141":
                    ret.setF141(rs.getString(field));
                    break;
                case "ERLAEUT":
                    ret.setErlaeut(rs.getString(field));
                    break;
                case "LMEDIKAT":
                    ret.setLmedikat(rs.getString(field));
                    break;
                case "TAET":
                    ret.setTaet(rs.getString(field));
                    break;
                case "BKS":
                    ret.setBks(rs.getString(field));
                    break;
                case "F_153":
                    ret.setF153(rs.getString(field));
                    break;
                case "F_154":
                    ret.setF154(rs.getString(field));
                    break;
                case "F_156":
                    ret.setF156(rs.getString(field));
                    break;
                case "F_157":
                    ret.setF157(rs.getString(field));
                    break;
                case "F_158":
                    ret.setF158(rs.getString(field));
                    break;
                case "F_159":
                    ret.setF159(rs.getString(field));
                    break;
                case "F_160":
                    ret.setF160(rs.getString(field));
                    break;
                case "F_161":
                    ret.setF161(rs.getString(field));
                    break;
                case "F_162":
                    ret.setF162(rs.getString(field));
                    break;
                case "F_163":
                    ret.setF163(rs.getString(field));
                    break;
                case "F_164":
                    ret.setF164(rs.getString(field));
                    break;
                case "F_165":
                    ret.setF165(rs.getString(field));
                    break;
                case "F_166":
                    ret.setF166(rs.getString(field));
                    break;
                case "F_167":
                    ret.setF167(rs.getString(field));
                    break;
                case "F_168":
                    ret.setF168(rs.getString(field));
                    break;
                case "F_169":
                    ret.setF169(rs.getString(field));
                    break;
                case "F_170":
                    ret.setF170(rs.getString(field));
                    break;
                case "F_171":
                    ret.setF171(rs.getString(field));
                    break;
                case "F_172":
                    ret.setF172(rs.getString(field));
                    break;
                case "F_173":
                    ret.setF173(rs.getString(field));
                    break;
                case "F_174":
                    ret.setF174(rs.getString(field));
                    break;
                case "F_175":
                    ret.setF175(rs.getString(field));
                    break;
                case "F_176":
                    ret.setF176(rs.getString(field));
                    break;
                case "F_177":
                    ret.setF177(rs.getString(field));
                    break;
                case "LEISTBI":
                    ret.setLeistbi(rs.getString(field));
                    break;
                case "F_178":
                    ret.setF178(rs.getString(field));
                    break;
                case "F_179":
                    ret.setF179(rs.getString(field));
                    break;
                case "F_181":
                    ret.setF181(rs.getString(field));
                    break;
                case "TERLEUT":
                    ret.setTerleut(rs.getString(field));
                    break;
                case "FREITEXT":
                    ret.setFreitext(rs.getString(field));
                    break;
                case "TMA1":
                    ret.setTma1(rs.getInt(field));
                    break;
                case "TMA2":
                    ret.setTma2(rs.getInt(field));
                    break;
                case "TMA3":
                    ret.setTma3(rs.getInt(field));
                    break;
                case "TMA4":
                    ret.setTma4(rs.getInt(field));
                    break;
                case "TMA5":
                    ret.setTma5(rs.getInt(field));
                    break;
                case "TMA6":
                    ret.setTma6(rs.getInt(field));
                    break;
                case "TMA7":
                    ret.setTma7(rs.getInt(field));
                    break;
                case "TMA8":
                    ret.setTma8(rs.getInt(field));
                    break;
                case "TMA9":
                    ret.setTma9(rs.getInt(field));
                    break;
                case "TMA10":
                    ret.setTma10(rs.getInt(field));
                    break;
                case "TMA11":
                    ret.setTma11(rs.getInt(field));
                    break;
                case "TMA12":
                    ret.setTma12(rs.getInt(field));
                    break;
                case "TMA13":
                    ret.setTma13(rs.getInt(field));
                    break;
                case "TMA14":
                    ret.setTma14(rs.getInt(field));
                    break;
                case "TMA15":
                    ret.setTma15(rs.getInt(field));
                    break;
                case "TMA16":
                    ret.setTma16(rs.getInt(field));
                    break;
                case "TMA17":
                    ret.setTma17(rs.getInt(field));
                    break;
                case "TMA18":
                    ret.setTma18(rs.getInt(field));
                    break;
                case "TMA19":
                    ret.setTma19(rs.getInt(field));
                    break;
                case "TMA20":
                    ret.setTma20(rs.getInt(field));
                    break;
                case "TMA21":
                    ret.setTma21(rs.getInt(field));
                    break;
                case "TMA22":
                    ret.setTma22(rs.getInt(field));
                    break;
                case "TMA23":
                    ret.setTma23(rs.getInt(field));
                    break;
                case "TMA24":
                    ret.setTma24(rs.getInt(field));
                    break;
                case "TMA25":
                    ret.setTma25(rs.getInt(field));
                    break;
                case "TAZ1":
                    ret.setTaz1(rs.getString(field));
                    break;
                case "TAZ2":
                    ret.setTaz2(rs.getString(field));
                    break;
                case "TAZ3":
                    ret.setTaz3(rs.getString(field));
                    break;
                case "TAZ4":
                    ret.setTaz4(rs.getString(field));
                    break;
                case "TAZ5":
                    ret.setTaz5(rs.getString(field));
                    break;
                case "TAZ6":
                    ret.setTaz6(rs.getString(field));
                    break;
                case "TAZ7":
                    ret.setTaz7(rs.getString(field));
                    break;
                case "TAZ8":
                    ret.setTaz8(rs.getString(field));
                    break;
                case "TAZ9":
                    ret.setTaz9(rs.getString(field));
                    break;
                case "TAZ10":
                    ret.setTaz10(rs.getString(field));
                    break;
                case "TAZ11":
                    ret.setTaz11(rs.getString(field));
                    break;
                case "TAZ12":
                    ret.setTaz12(rs.getString(field));
                    break;
                case "TAZ13":
                    ret.setTaz13(rs.getString(field));
                    break;
                case "TAZ14":
                    ret.setTaz14(rs.getString(field));
                    break;
                case "TAZ15":
                    ret.setTaz15(rs.getString(field));
                    break;
                case "TAZ16":
                    ret.setTaz16(rs.getString(field));
                    break;
                case "TAZ17":
                    ret.setTaz17(rs.getString(field));
                    break;
                case "TAZ18":
                    ret.setTaz18(rs.getString(field));
                    break;
                case "TAZ19":
                    ret.setTaz19(rs.getString(field));
                    break;
                case "TAZ20":
                    ret.setTaz20(rs.getString(field));
                    break;
                case "TAZ21":
                    ret.setTaz21(rs.getString(field));
                    break;
                case "TAZ22":
                    ret.setTaz22(rs.getString(field));
                    break;
                case "TAZ23":
                    ret.setTaz23(rs.getString(field));
                    break;
                case "TAZ24":
                    ret.setTaz24(rs.getString(field));
                    break;
                case "TAZ25":
                    ret.setTaz25(rs.getString(field));
                    break;
                case "LSEITE1":
                    ret.setlSeite1(rs.getString(field) == "T" ? true : false);
                    break;
                case "LSEITE3":
                    ret.setlSeite3(rs.getString(field) == "T" ? true : false);
                    break;
                case "LSEITE4":
                    ret.setlSeite4(rs.getString(field) == "T" ? true : false);
                    break;
                case "AIGR":
                    ret.setAigr(rs.getString(field));
                    break;
                case "ABTEILUNG":
                    ret.setAbteilung(rs.getString(field));
                    break;
                case "DMP":
                    ret.setDmp(rs.getString(field));
                    break;
                case "UNTDAT":
                    ret.setUntdat(rs.getDate(field) == null ? null : rs.getDate(field).toLocalDate());
                    break;
                case "ID":
                    ret.setId(rs.getInt(field));
                    break;
                case "ARZT1":
                    ret.setArzt1(rs.getString(field));
                    break;
                case "ARZT2":
                    ret.setArzt2(rs.getString(field));
                    break;
                case "ARZT3":
                    ret.setArzt3(rs.getString(field));
                    break;
                case "DIAG6":
                    ret.setDiag6(rs.getString(field));
                    break;
                default:
                    logger.error("Unhandled field in bericht2 found: " + meta.getColumnLabel(o) + " at pos: " + o);
                };
            }
        } catch (SQLException e) {
            logger.error("Couldn't retrieve dataset in Bericht2",e);
            return null;
        }

        return ret;
        }

    public void saveToDB(Gutachten gutachten) {
        String sql = "insert into " + dbTabellenName + " set "
                                + "PAT_INTERN='" + gutachten.getPatIntern() + "',"
                                + "BERICHTID='" + gutachten.getBerichtId() + "',"
                                + "VNUMMER='" + gutachten.getvNummer() + "',"
                                + "NAMEVOR='" + gutachten.getNameVor() + "',"
                                + "GEBOREN='" + gutachten.getGeboren() + "',"
                                + "STRASSE='" + gutachten.getStrasse() + "',"
                                + "PLZ='" + gutachten.getPlz() + "',"
                                + "ORT='" + gutachten.getOrt() + "',"
                                + "VNAMEVO='" + gutachten.getvNameVO() + "',"
                                + "MSNR='" + gutachten.getMsNr() + "',"
                                + "BNR='" + gutachten.getbNr() + "',"
                                + "AUFDAT1='" + gutachten.getAufDat1() + "',"
                                + "ENTDAT1='" + gutachten.getEntDat1() + "',"
                                + "AUFDAT2='" + gutachten.getAufDat2() + "',"
                                + "ENTDAT2='" + gutachten.getEntDat2() + "',"
                                + "AUFDAT3='" + gutachten.getAufDat3() + "',"
                                + "ENTDAT3='" + gutachten.getEntDat3() + "',"
                                + "ENTFORM='" + gutachten.getEntform() + "',"
                                + "ARBFAE='" + gutachten.getArbfae() + "',"
                                + "DIAG1='" + gutachten.getDiag1() + "',"
                                + "F_74='" + gutachten.getF74() + "',"
                                + "F_79='" + gutachten.getF79() + "',"
                                + "F_80='" + gutachten.getF80() + "',"
                                + "F_81='" + gutachten.getF81() + "',"
                                + "DIAG2='" + gutachten.getDiag2() + "',"
                                + "F_82='" + gutachten.getF82() + "',"
                                + "F_87='" + gutachten.getF87() + "',"
                                + "F_88='" + gutachten.getF88() + "',"
                                + "F_89='" + gutachten.getF89() + "',"
                                + "DIAG3='" + gutachten.getDiag3() + "',"
                                + "F_90='" + gutachten.getF90() + "',"
                                + "F_95='" + gutachten.getF95() + "',"
                                + "F_96='" + gutachten.getF96() + "',"
                                + "F_97='" + gutachten.getF97() + "',"
                                + "DIAG4='" + gutachten.getDiag4() + "',"
                                + "F_98='" + gutachten.getF98() + "',"
                                + "F_103='" + gutachten.getF103() + "',"
                                + "F_104='" + gutachten.getF104() + "',"
                                + "F_105='" + gutachten.getF105() + "',"
                                + "DIAG5='" + gutachten.getDiag5() + "',"
                                + "F_106='" + gutachten.getF106() + "',"
                                + "F_111='" + gutachten.getF111() + "',"
                                + "F_112='" + gutachten.getF112() + "',"
                                + "F_113='" + gutachten.getF113() + "',"
                                + "F_114='" + gutachten.getF114() + "',"
                                + "F_117='" + gutachten.getF117() + "',"
                                + "F_120='" + gutachten.getF120() + "',"
                                + "F_123='" + gutachten.getF123() + "',"
                                + "F_124='" + gutachten.getF124() + "',"
                                + "F_125='" + gutachten.getF125() + "',"
                                + "F_126='" + gutachten.getF126() + "',"
                                + "F_127='" + gutachten.getF127() + "',"
                                + "F_128='" + gutachten.getF128() + "',"
                                + "F_129='" + gutachten.getF129() + "',"
                                + "F_130='" + gutachten.getF130() + "',"
                                + "F_131='" + gutachten.getF131() + "',"
                                + "F_132='" + gutachten.getF132() + "',"
                                + "F_133='" + gutachten.getF133() + "',"
                                + "F_134='" + gutachten.getF134() + "',"
                                + "F_135='" + gutachten.getF135() + "',"
                                + "F_136='" + gutachten.getF136() + "',"
                                + "F_137='" + gutachten.getF137() + "',"
                                + "F_138='" + gutachten.getF138() + "',"
                                + "F_139='" + gutachten.getF139() + "',"
                                + "F_140='" + gutachten.getF140() + "',"
                                + "F_141='" + gutachten.getF141() + "',"
                                + "ERLAEUT='" + gutachten.getErlaeut() + "',"
                                + "LMEDIKAT='" + gutachten.getLmedikat() + "',"
                                + "TAET='" + gutachten.getTaet() + "',"
                                + "BKS='" + gutachten.getBks() + "',"
                                + "F_153='" + gutachten.getF153() + "',"
                                + "F_154='" + gutachten.getF154() + "',"
                                + "F_156='" + gutachten.getF156() + "',"
                                + "F_157='" + gutachten.getF157() + "',"
                                + "F_158='" + gutachten.getF158() + "',"
                                + "F_159='" + gutachten.getF159() + "',"
                                + "F_160='" + gutachten.getF160() + "',"
                                + "F_161='" + gutachten.getF161() + "',"
                                + "F_162='" + gutachten.getF162() + "',"
                                + "F_163='" + gutachten.getF163() + "',"
                                + "F_164='" + gutachten.getF164() + "',"
                                + "F_165='" + gutachten.getF165() + "',"
                                + "F_166='" + gutachten.getF166() + "',"
                                + "F_167='" + gutachten.getF167() + "',"
                                + "F_168='" + gutachten.getF168() + "',"
                                + "F_169='" + gutachten.getF169() + "',"
                                + "F_170='" + gutachten.getF170() + "',"
                                + "F_171='" + gutachten.getF171() + "',"
                                + "F_172='" + gutachten.getF172() + "',"
                                + "F_173='" + gutachten.getF173() + "',"
                                + "F_174='" + gutachten.getF174() + "',"
                                + "F_175='" + gutachten.getF175() + "',"
                                + "F_176='" + gutachten.getF176() + "',"
                                + "F_177='" + gutachten.getF177() + "',"
                                + "LEISTBI='" + gutachten.getLeistbi() + "',"
                                + "F_178='" + gutachten.getF178() + "',"
                                + "F_179='" + gutachten.getF179() + "',"
                                + "F_181='" + gutachten.getF181() + "',"
                                + "TERLEUT='" + gutachten.getTerleut() + "',"
                                + "FREITEXT='" + gutachten.getFreitext() + "',"
                                + "TMA1='" + gutachten.getTma1() + "',"
                                + "TMA2='" + gutachten.getTma2() + "',"
                                + "TMA3='" + gutachten.getTma3() + "',"
                                + "TMA4='" + gutachten.getTma4() + "',"
                                + "TMA5='" + gutachten.getTma5() + "',"
                                + "TMA6='" + gutachten.getTma6() + "',"
                                + "TMA7='" + gutachten.getTma7() + "',"
                                + "TMA8='" + gutachten.getTma8() + "',"
                                + "TMA9='" + gutachten.getTma9() + "',"
                                + "TMA10='" + gutachten.getTma10() + "',"
                                + "TMA11='" + gutachten.getTma11() + "',"
                                + "TMA12='" + gutachten.getTma12() + "',"
                                + "TMA13='" + gutachten.getTma13() + "',"
                                + "TMA14='" + gutachten.getTma14() + "',"
                                + "TMA15='" + gutachten.getTma15() + "',"
                                + "TMA16='" + gutachten.getTma16() + "',"
                                + "TMA17='" + gutachten.getTma17() + "',"
                                + "TMA18='" + gutachten.getTma18() + "',"
                                + "TMA19='" + gutachten.getTma19() + "',"
                                + "TMA20='" + gutachten.getTma20() + "',"
                                + "TMA21='" + gutachten.getTma21() + "',"
                                + "TMA22='" + gutachten.getTma22() + "',"
                                + "TMA23='" + gutachten.getTma23() + "',"
                                + "TMA24='" + gutachten.getTma24() + "',"
                                + "TMA25='" + gutachten.getTma25() + "',"
                                + "TAZ1='" + gutachten.getTaz1() + "',"
                                + "TAZ2='" + gutachten.getTaz2() + "',"
                                + "TAZ3='" + gutachten.getTaz3() + "',"
                                + "TAZ4='" + gutachten.getTaz4() + "',"
                                + "TAZ5='" + gutachten.getTaz5() + "',"
                                + "TAZ6='" + gutachten.getTaz6() + "',"
                                + "TAZ7='" + gutachten.getTaz7() + "',"
                                + "TAZ8='" + gutachten.getTaz8() + "',"
                                + "TAZ9='" + gutachten.getTaz9() + "',"
                                + "TAZ10='" + gutachten.getTaz10() + "',"
                                + "TAZ11='" + gutachten.getTaz11() + "',"
                                + "TAZ12='" + gutachten.getTaz12() + "',"
                                + "TAZ13='" + gutachten.getTaz13() + "',"
                                + "TAZ14='" + gutachten.getTaz14() + "',"
                                + "TAZ15='" + gutachten.getTaz15() + "',"
                                + "TAZ16='" + gutachten.getTaz16() + "',"
                                + "TAZ17='" + gutachten.getTaz17() + "',"
                                + "TAZ18='" + gutachten.getTaz18() + "',"
                                + "TAZ19='" + gutachten.getTaz19() + "',"
                                + "TAZ20='" + gutachten.getTaz20() + "',"
                                + "TAZ21='" + gutachten.getTaz21() + "',"
                                + "TAZ22='" + gutachten.getTaz22() + "',"
                                + "TAZ23='" + gutachten.getTaz23() + "',"
                                + "TAZ24='" + gutachten.getTaz24() + "',"
                                + "TAZ25='" + gutachten.getTaz25() + "',"
                                + "LSEITE1='" + gutachten.getlSeite1() + "',"
                                + "LSEITE3='" + gutachten.getlSeite3() + "',"
                                + "LSEITE4='" + gutachten.getlSeite4() + "',"
                                + "AIGR='" + gutachten.getAigr() + "',"
                                + "ABTEILUNG='" + gutachten.getAbteilung() + "',"
                                + "DMP='" + gutachten.getDmp() + "',"
                                + "UNTDAT='" + gutachten.getUntdat() + "',"
                                + "ID='" + gutachten.getId() + "',"
                                + "ARZT1='" + gutachten.getArzt1() + "',"
                                + "ARZT2='" + gutachten.getArzt2() + "',"
                                + "ARZT3='" + gutachten.getArzt3() + "',"
                                + "DIAG6='" + gutachten.getDiag6() + "'";
        try {
            Connection conn = new DatenquellenFactory(ik.digitString()).createConnection();
             conn.createStatement().execute(sql);
        } catch (SQLException e) {
            logger.error("Could not save dataset " + gutachten.toString( ) + " to Database, table bericht2", e);
        }
    }

}
