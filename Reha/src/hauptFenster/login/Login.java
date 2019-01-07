package hauptFenster.login;

import javax.sql.DataSource;

import CommonTools.GenericObservable;
import CommonTools.GenericObservable.GenericObserver;

public class Login {

	private static final int MAX_TRY = 3;
	private DataSource dataSource;
	public Login(DataSource dataSource) {
		this.dataSource = dataSource;

	}

	public User login() {
		for (int i = 1; i<MAX_TRY ;i++) {


		}
return null;
	}



}
