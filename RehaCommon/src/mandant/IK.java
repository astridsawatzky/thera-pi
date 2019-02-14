package mandant;

import java.util.regex.Pattern;

public class IK {
   private static final Pattern ikFormat = Pattern.compile("\\d{9}");
    private String ik;

    public IK(String ik) {
        if(!ikFormat.matcher(ik).matches()) {
            throw new IllegalArgumentException(ik + " is not a valid IK");
        }
        this.ik = ik;
    }

    public String asString() {
        return ik;
    }
    
    

}
