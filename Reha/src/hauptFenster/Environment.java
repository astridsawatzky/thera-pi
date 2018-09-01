package hauptFenster;

import java.io.File;

public enum Environment {
	Instance;
	
	String proghome="";
	OS currentOS = OS.WIN;
	
	public  String getProghome() {
		return proghome;
	}
	 void setProghome(String proghome) {
		this.proghome = proghome;
	}
	public boolean isLinux() {
		return currentOS.isLinux();
	}
	public boolean isWindows() {
		return currentOS.isWindows();
	}
	
	Environment() {
		String osVersion;
		String prog = java.lang.System.getProperty("user.dir");
		String homedir = java.lang.System.getProperty("user.home");
		osVersion = System.getProperty("os.name");
		if(osVersion.contains("Linux")){
			setProghome("/opt/RehaVerwaltung/");
			currentOS=OS.Linux;
		}else if(osVersion.contains("Windows")){
			setProghome(prog.substring(0, 2)+"/RehaVerwaltung/");
			currentOS = OS.WIN;
		}else if(osVersion.contains("Mac OS X")){
			currentOS=OS.MAC;
			/**XXX: es wird zwar geprueft, ob man auf Mac ist, hat aber ausser dem pfad keine weitere Konsequenzen
			welcher Rueckgaberwert im Fall von OSX erfolgt muß durch einen Mac-Anhänger ermittelt werden **/
			System.out.println("Vermutlich MAC, Output = "+osVersion);
			setProghome(homedir+"/RehaVerwaltung/");
		}
		if (! (new File(getProghome() + "Rehaverwaltung").exists())) {
			System.out.println("setting Directory to default");
			setProghome("C:/RehaVerwaltung/");
		}
		System.out.println("Programmverzeichnis = "+getProghome());
	}
	private enum OS{
		WIN {
			@Override
			boolean isLinux() {
				return false;
			}

			@Override
			boolean isWindows() {
				return true;
			}
		},
		Linux {
			@Override
			boolean isLinux() {
				return true;
			}

			@Override
			boolean isWindows() {
				return false;
			}
		},
		MAC {
			@Override
			boolean isLinux() {
				return false;
			}

			@Override
			boolean isWindows() {
				return false;
			}
		};
		abstract boolean isLinux();
		abstract boolean isWindows();
		
		
	}
}