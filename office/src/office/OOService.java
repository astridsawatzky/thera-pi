package office;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.ini4j.Ini;

import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.document.DocumentException;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.event.ITerminateEvent;
import ag.ion.bion.officelayer.event.VetoTerminateListener;
import ag.ion.bion.officelayer.runtime.IOfficeProgressMonitor;

public class OOService {
    private static final CountDownLatch latch = new CountDownLatch(1);


    public static String OpenOfficeNativePfad;
    public static String OpenOfficePfad;

    public static void setLibpath(String libpath, String ooPath) {
        OpenOfficeNativePfad = libpath;
        OpenOfficePfad = ooPath;
    }

    public OOService() {
        if (OpenOfficeNativePfad == null || OpenOfficePfad == null) {
            throw new IllegalStateException("lib and path not set");
        }
    }

    public OOService(Ini officeIni) {
        OpenOfficePfad = officeIni.get("OpenOffice.org", "OfficePfad");
        OpenOfficeNativePfad = officeIni.get("OpenOffice.org", "OfficeNativePfad");
    }

    private static IOfficeApplication officeapplication;

    public IOfficeApplication getOfficeapplication() {
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return officeapplication;
    }

    public IOfficeApplication start() throws OfficeApplicationException, FileNotFoundException {
        Map<String, Object> config = loadConfiguration(OpenOfficeNativePfad, OpenOfficePfad);
        System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH, OpenOfficeNativePfad);
        officeapplication = activate(config);
        System.out.println("activation complete");
        return officeapplication;
    }

    private IOfficeApplication activate(Map<String, Object> config) throws OfficeApplicationException {
        IOfficeApplication officeapplication2 = OfficeApplicationRuntime.getApplication(config);

        try {
            officeapplication2.activate(new OOServiceMonitor());
            officeapplication2.getDesktopService()
                              .addTerminateListener(new VetoTerminateListenerExtension());
        } catch (NullPointerException | OfficeApplicationException e) {
            e.printStackTrace();
        }

        return officeapplication2;
    }

    private Map<String, Object> loadConfiguration(String libPath, String ooPath)
            throws OfficeApplicationException, FileNotFoundException {
        File file = new File(ooPath);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        ILazyApplicationInfo info = OfficeApplicationRuntime.getApplicationAssistant(libPath)
                                                            .findLocalApplicationInfo(ooPath);
        String[] names = info.getProperties()
                             .getPropertyNames();
        boolean isLibreOffice = false;
        for (int i = 0; i < names.length; i++) {
            System.out.println(names[i] + " = " + info.getProperties()
                                                      .getPropertyValue(names[i]));
            if (info.getProperties()
                    .getPropertyValue(names[i])
                    .contains("LibreOffice")) {
                isLibreOffice = true;
            }
        }
        Map<String, Object> config = new HashMap<>();
        config.put(IOfficeApplication.APPLICATION_HOME_KEY, ooPath);
        config.put(IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION);
        if (isLibreOffice) {
            config.put(IOfficeApplication.APPLICATION_ARGUMENTS_KEY, new String[] { "--nodefault", "--nologo",
                    "--nofirststartwizard", "--nocrashreport", "--norestore" });
        } else {
            config.put(IOfficeApplication.APPLICATION_ARGUMENTS_KEY,
                    new String[] { "-nodefault", "-nologo", "-nofirststartwizard", "-nocrashreport", "-norestore" });
        }
        return config;
    }



    private final class VetoTerminateListenerExtension extends VetoTerminateListener {
        @Override
        public void queryTermination(ITerminateEvent terminateEvent) {
            super.queryTermination(terminateEvent);
            IDocument[] docs = null;
            try {
                docs = getOfficeapplication().getDocumentService()
                                             .getCurrentDocuments();
            } catch (DocumentException | OfficeApplicationException e) {
                e.printStackTrace();
            }
            if (docs.length == 1) {
                docs[0].close();
            }
        }
    }

    private final class OOServiceMonitor implements IOfficeProgressMonitor {
        @Override
        public void worked(int arg0) {
        }

        @Override
        public void setCanceled(boolean arg0) {
        }

        @Override
        public boolean needsDone() {
            return true;
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public void done() {
            latch.countDown();
        }

        @Override
        public void beginTask(String arg0, int arg1) {
        }

        @Override
        public void beginSubTask(String arg0) {
        }
    }
}
