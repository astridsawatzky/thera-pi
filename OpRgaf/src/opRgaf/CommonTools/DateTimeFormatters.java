package opRgaf.CommonTools;

import java.time.format.DateTimeFormatter;

public interface DateTimeFormatters {
     DateTimeFormatter ddMMYYYYmitPunkt= DateTimeFormatter.ofPattern("dd.MM.yyyy");
     DateTimeFormatter ddMMYYmitPunkt= DateTimeFormatter.ofPattern("dd.MM.yy");
     DateTimeFormatter dMYYYYmitPunkt= DateTimeFormatter.ofPattern("d.M.yyyy");
     DateTimeFormatter yyyyMMddmitBindestrich = DateTimeFormatter.ofPattern("yyyy-MM-dd");

}
