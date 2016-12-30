package abrechnung;

import java.util.ArrayList;
import java.util.Arrays;

import systemEinstellungen.SystemConfig;
import CommonTools.JRtaComboBox;

public class Disziplinen {
	private String[] typeOfVerordnung;
	private ArrayList<String> diszis;
	private String[] rezeptKlassen; 
	private JRtaComboBox cmbDiszi;


	/**
	 * verwaltet die enthaltenen Heilmittelsparten
	 */
	public Disziplinen() {
		diszis = new ArrayList<String>(Arrays.asList("Physio","Massage","Ergo","Logo","Podo","Rsport","Ftrain"));
		rezeptKlassen = new String [] {"KG","MA","ER","LO","PO","RS","FT"};

		// erst eine Arraylist mit Auswahleinträgen erzeugen (<- die kann 'wachsen'; ein Array nicht), ...
		ArrayList<String> ldiszis = new ArrayList<String>(Arrays.asList("Physio-Rezept","Massage/Lymphdrainage-Rezept","Ergotherapie-Rezept","Logopädie-Rezept","Podologie-Rezept"));
		if(SystemConfig.mitRs){
			ldiszis.addAll(Arrays.asList("Rehasport-Rezept","Funktionstraining-Rezept"));
		}
		typeOfVerordnung = new String[ ldiszis.size() ];		// ... daraus das Array für die ComboBox erstellen ...
		ldiszis.toArray( typeOfVerordnung );					// ... und füllen
		this.cmbDiszi = new JRtaComboBox(typeOfVerordnung);
		cmbDiszi.setSelectedItem("Physio-Rezept");				// default setzen
	}

	public JRtaComboBox getComboBox(){
		return this.cmbDiszi;
	}
	
	public String getCurrDiszi(){
		if (diszis.size() >= cmbDiszi.getSelectedIndex()){
			return diszis.get(cmbDiszi.getSelectedIndex());			
		}else{
			System.out.println("getCurrDiszi err: size "+diszis.size()+" vs idx "+cmbDiszi.getSelectedIndex());
			return diszis.get(0);	// use default ("Physio")
		}
	}

	public void setCurrDiszi(String currTypeOfVO) {
		this.cmbDiszi.setSelectedItem(currTypeOfVO);
		
	}

	public String getCurrRezClass(){
		if (rezeptKlassen.length >= cmbDiszi.getSelectedIndex()){
			return rezeptKlassen[cmbDiszi.getSelectedIndex()];			
		}else{
			System.out.println("getCurrDiszi err: size "+diszis.size()+" vs idx "+cmbDiszi.getSelectedIndex());
			return rezeptKlassen[0];	// use default ("KG")
		}
	}

	public int getIndex(String Disziplin) {
		return diszis.indexOf(Disziplin);
	}


}