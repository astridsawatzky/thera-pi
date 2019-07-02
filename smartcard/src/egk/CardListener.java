package egk;

public interface CardListener {

    void cardInserted(CardTerminalEvent cardTerminalEvent);

    void cardRemoved(CardTerminalEvent cardTerminalEvent);

}
