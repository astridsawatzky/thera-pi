package theraPi;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.ini4j.Ini;

import mandant.Mandant;

public class MandantList implements List<Mandant>{
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

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator<Mandant> iterator() {
		return list.iterator();
	}

	@Override
	public Mandant[] toArray() {

		Object[] os1 = list.toArray();
		return (Mandant[]) os1;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(Mandant e) {
		return list.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Mandant> c) {
		return list.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Mandant> c) {
		return list.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public Mandant get(int index) {
		return get(index);
	}

	@Override
	public Mandant set(int index, Mandant element) {
		return list.set(index, element);
	}

	@Override
	public void add(int index, Mandant element) {
		list.add(index, element);
	}

	@Override
	public Mandant remove(int index) {
		return list.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<Mandant> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<Mandant> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public List<Mandant> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

}
