package logging;

import ch.qos.logback.classic.util.ContextInitializer;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class Logging {
    public Logging(String string) {
        init(string);
    }

    private void init(String string) {

        String path = "./logs/conf/" + string + ".xml";
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, path);

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
    }
}
