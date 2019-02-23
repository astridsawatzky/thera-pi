package environment;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class SWTJarLoader implements Runnable{
	public static void main(String[] args) {
		new SWTJarLoader().loadSwtJar();
	}
	public SWTJarLoader() {
		
	}

private void loadSwtJar() {
   

    String osName = System.getProperty("os.name").toLowerCase();
    String osArch = System.getProperty("os.arch").toLowerCase();

    //NOTE - I have not added the mac and *nix swt jars.
    String osPart = 
        osName.contains("win") ? "win" :
        osName.contains("mac") ? "cocoa" :
        osName.contains("linux") || osName.contains("nix") ? "gtk" :
        null;

    if (null == osPart)
        throw new RuntimeException ("Cannot determine correct swt jar from os.name [" + osName + "] and os.arch [" + osArch + "]");

    String archPart = osArch.contains ("64") ? "64" : "32";

    System.out.println ("Architecture and OS == "+archPart+"bit "+osPart);

    String swtFileName = "swt-" +osPart + archPart +"-3.7.jar";
    String workingDir = System.getProperty("user.dir");
    String libDir = "\\C:\\repos\\develop\\Libraries\\lib\\djnative\\";
    File file = new File(libDir, swtFileName);
    if (!file.exists ())
        System.out.println("Can't locate SWT Jar " + file.getAbsolutePath());

    try {
        URLClassLoader classLoader = (URLClassLoader) getClass().getClassLoader ();
        Method addUrlMethod = URLClassLoader.class.getDeclaredMethod ("addURL", URL.class);
        addUrlMethod.setAccessible (true);

        URL swtFileUrl = file.toURI().toURL();
        //System.out.println("Adding to classpath: " + swtFileUrl);
        addUrlMethod.invoke (classLoader, swtFileUrl);
    }
    catch (Exception e) {
        throw new RuntimeException ("Unable to add the swt jar to the class path: " + file.getAbsoluteFile (), e);
    }
}
@Override
public void run() {
	loadSwtJar();
	
}
}