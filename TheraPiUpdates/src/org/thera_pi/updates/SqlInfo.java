package org.thera_pi.updates;





import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;





public class SqlInfo {
	
/**
 * @throws SQLException *********************************/	
	public static void sqlAusfuehren(Connection conn,String sstmt) throws SQLException{

		Statement stmt = null;
		stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );

			stmt.execute(sstmt);
			
			if (stmt != null) {
					stmt.close();
			}

		return;
	}
	
/*****************************************/

	public static Vector<String> holeSatz(Connection conn,String tabelle, String felder, String kriterium){
		Statement stmt = null;
		ResultSet rs = null;
		Vector<String> retvec = new Vector<String>();
			
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			String sstmt = "select "+felder+" from "+tabelle+" where "+kriterium;
			//System.out.println(sstmt);
			rs = stmt.executeQuery(sstmt);

			if(rs.next()){
				 ResultSetMetaData rsMetaData = rs.getMetaData() ;
				 int numberOfColumns = rsMetaData.getColumnCount()+1;
				 for(int i = 1 ; i < numberOfColumns;i++){
						 retvec.add((rs.getString(i)==null  ? "" :  rs.getString(i)));
				 }
			}
		}catch(SQLException ev){
			System.out.println("SQLException: " + ev.getMessage());
			System.out.println("SQLState: " + ev.getSQLState());
			System.out.println("VendorError: " + ev.getErrorCode());
		}	
		finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		return (Vector<String>)retvec;
	}
	
	public static void zeigeTabellen(Connection conn){
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sstmt = "show tables";
		try {
			rs = stmt.executeQuery(sstmt);
			if(rs.next()){
				//System.out.println("**************************"+rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static Vector<String> holeFeld(Connection conn,String sstmt){
		Statement stmt = null;
		ResultSet rs = null;
		String ret = "";
		Vector<String> vecret = new Vector<String>();
			
		try {
			stmt =  conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			            ResultSet.CONCUR_UPDATABLE );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			rs = stmt.executeQuery(sstmt);

			while(rs.next()){
				ret = (rs.getString(1)==null  ? "" :  rs.getString(1));
				vecret.add(String.valueOf(ret));
			}
		}catch(SQLException ev){
			//System.out.println("SQLException: " + ev.getMessage());
			//System.out.println("SQLState: " + ev.getSQLState());
			//System.out.println("VendorError: " + ev.getErrorCode());
		}	
		finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException sqlEx) { // ignore }
					rs = null;
				}
			}	
			if (stmt != null) {
				try {
					stmt.close();
					stmt = null;
				} catch (SQLException sqlEx) { // ignore }
					stmt = null;
				}
			}
		}
		return vecret;
	}
	
	
}
