package egk;

import javax.smartcardio.Card;
import javax.smartcardio.CardTerminal;

public interface CardTerminalEvent {

     CardTerminal getCardTerminal();

     int getSlotID();

     Card getSmartCard();

}
