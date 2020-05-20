package systemEinstellungen;

import java.util.LinkedList;
import java.util.List;

public class BehandlerSet{

    private static final class BehandlerSetExtension extends BehandlerSet {

        public BehandlerSetExtension() {
            for( int i = 0 ; i<7;i++) {
                members.add("./.");
            }
            setName("./.");

        }

    }
    public static final BehandlerSet EMPTY = new BehandlerSetExtension();

    @Override
    public String toString() {
        return "BehandlerSet [name=" + name + ", members=" + members + ", index=" + index + "]";
    }
    private String name;
    List<String> members = new LinkedList<>();
    public int index;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<String> getMembers() {
        return members;
    }

}
