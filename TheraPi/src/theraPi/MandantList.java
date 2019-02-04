package theraPi;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ini4j.Ini;

import mandant.Mandant;

class MandantList {
	List<Mandant> list;
	private int defIndex=0;
	private boolean showAlways;
	public MandantList(Ini ini) {
		int anzahlMandanten = Integer.parseInt(ini.get("TheraPiMandanten", "AnzahlMandanten"));
		defIndex =  Integer.parseInt(ini.get("TheraPiMandanten", "DefaultMandant"));
		list = new LinkedList<Mandant>();
		for(int i = 1; i <= anzahlMandanten;i++){
			String ik = new String(ini.get("TheraPiMandanten", "MAND-IK"+(i)));
			String name = new String(ini.get("TheraPiMandanten", "MAND-NAME"+(i)));
			list.add(i-1, new Mandant(ik,name));

		}
		setShowAllways(ini);

	}

	private void setShowAllways(Ini ini) {
		String auswahlZeigen = ini.get("TheraPiMandanten", "AuswahlImmerZeigen");
		if (auswahlZeigen==null) {
			showAlways = true;
		} else if("1".equals(auswahlZeigen)){
			showAlways =true;
		} else {
			showAlways = Boolean.valueOf(auswahlZeigen);
		}
	}

	boolean showAllways() {
		return showAlways;
	}

	public Mandant defaultMandant() {
		return list.get(defIndex-1);
	}

   

    List<Mandant> asList() {
        return Collections.unmodifiableList(list);
    }

	
}
