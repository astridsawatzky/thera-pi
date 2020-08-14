package systemEinstellungen;

import java.util.LinkedList;
import java.util.List;

public class BehandlerSet{


    public static final BehandlerSet EMPTY = new BehandlerSet(7);

    private BehandlerSet(int laenge) {
        for( int i = 0 ; i<laenge;i++) {
            members.add("./.");
        }
        setName("./.");
    }
    public BehandlerSet() {
    }
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
