package egk;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgkReader implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(EgkReader.class);
    private final String terminalname;
    private CardListener listener;

    public EgkReader(String terminalname) {
        this.terminalname = terminalname;
    }

    public void addCardListener(CardListener listener) {
        this.listener = listener;

    }

    @Override
    public void run() {
        CardTerminal terminal = TerminalFactory.getDefault()
                                               .terminals()
                                               .getTerminal(terminalname);
        if (terminal == null) {
            terminalnotfound(terminalname);
        }

        else {
            logger.info("EGK listener auf Terminal [" + terminalname + "] gestartet.");
            while (true) {
                try {
                    terminal.waitForCardPresent(0);
                    fireCardInserted(terminal);
                    terminal.waitForCardAbsent(0);
                    fireCardremoved(terminal);
                } catch (CardException e) {
                    logger.error("neuer Fehler bitte Bugreport", e);
                }

            }

        }

    }

    private void fireCardremoved(CardTerminal terminal) {
        listener.cardRemoved(new CardTerminalEvent() {

            @Override
            public Card getSmartCard() {
                try {
                    return terminal.connect("*");
                } catch (CardException e) {

                    logger.error("neuer Fehler bitte Bugreport", e);
                    return null;
                }
            }

            @Override
            public int getSlotID() {
                return -1;
            }

            @Override
            public CardTerminal getCardTerminal() {
                return terminal;
            }
        });

    }

    private void fireCardInserted(CardTerminal terminal) throws CardException {
        listener.cardInserted(new CardTerminalEvent() {

            @Override
            public Card getSmartCard() {
                try {
                    return terminal.connect("*");
                } catch (CardException e) {

                    logger.error("neuer Fehler bitte Bugreport", e);
                    return null;
                }
            }

            @Override
            public int getSlotID() {
                return -1;
            }

            @Override
            public CardTerminal getCardTerminal() {
                return terminal;
            }
        });
        logger.debug("neue Karte in: " + terminal.getName());

    }

    private void terminalnotfound(String terminalname2) {
        logger.error("der Kartenleser [" + terminalname2 + "] wurde nicht gefunden");

    }

}
