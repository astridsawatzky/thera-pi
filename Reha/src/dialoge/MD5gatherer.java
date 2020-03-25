package dialoge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import environment.Path;

class MD5gatherer extends SwingWorker<Map<String, String>, String> {

    private final ValuesReceiver valueReceiver;
    private static final Logger logger = LoggerFactory.getLogger(MD5gatherer.class);

    MD5gatherer(ValuesReceiver valuereceiver) {
        this.valueReceiver = valuereceiver;
    }

    /**
     * collects all jars from 'proghome' and computes its md5s.
     *
     * @return Map <Filename, MD5hash>
     **/
    @Override
    protected Map<String, String> doInBackground() throws Exception {
        File dir = new File(Path.Instance.getProghome());
        List<File> files = Arrays.asList(dir.listFiles((dir1, name) -> name.endsWith(".jar")));

        Map<String, String> md5Hashes = new HashMap<String, String>();

        for (File file : files) {
            String string = md5HashOf(file);

            md5Hashes.put(file.getName(), string);
        }
        return md5Hashes;
    }

    @Override
    protected void done() {
        try {
            this.valueReceiver.setValues(get());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("could not set values on md5panel", e);
        }
    }

    protected String md5HashOf(File file) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(Files.readAllBytes(Paths.get(file.getPath())));
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest)
                                .toUpperCase();
    }

}
