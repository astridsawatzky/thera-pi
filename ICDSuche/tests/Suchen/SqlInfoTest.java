package Suchen;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SqlInfoTest {

	@Test
	public void whereclaus() throws Exception {
		String[] felder = { "felder" };
		assertEquals(" (felder like '%test%') ", SqlInfo.macheWhereKlausel(null, "test", felder));

		String[] mehrerefelder = { "feld1", "feld2" };
		assertEquals(" (feld1 like '%test%' OR feld2 like '%test%') ",
				SqlInfo.macheWhereKlausel(null, "test", mehrerefelder));

		String mehrereSuchbegriffe = "criteria1 criteria2";
		assertEquals("(  (felder like '%criteria1%')  AND  (felder like '%criteria2%') ) ",
				SqlInfo.macheWhereKlausel(null, mehrereSuchbegriffe, felder));
		assertEquals(
				"(  (feld1 like '%criteria1%' OR feld2 like '%criteria1%')  AND  (feld1 like '%criteria2%' OR feld2 like '%criteria2%') ) ",
				SqlInfo.macheWhereKlausel(null, mehrereSuchbegriffe, mehrerefelder));


		String vieleLeerZeichen = "criteria1   criteria2    wasn        du";
		assertEquals(
				"(  (felder like '%criteria1%')  AND  (felder like '%criteria2%')  AND  (felder like '%wasn%')  AND  (felder like '%du%') ) ",
				SqlInfo.macheWhereKlausel(null, vieleLeerZeichen, felder));

	}

}
