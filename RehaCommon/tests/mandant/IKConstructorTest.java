package mandant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IKConstructorTest {

    @Test(expected = IllegalArgumentException.class)
    public final void emptyStringThrowsIAE() throws Exception {
        new IK("");

    }

    @Test(expected = IllegalArgumentException.class)
    public final void stringwithNonDigitsThrowsIAE() throws Exception {
        new IK("asdb");

    }

    @Test(expected = IllegalArgumentException.class)
    public final void stringWithLessThanNineDigitsThrowsIAE() throws Exception {
        new IK("12345678");

    }

    @Test(expected = IllegalArgumentException.class)
    public final void stringWithMoreThanNineDigitsThrowsIAE() throws Exception {
        new IK("1234567891");

    }

    @Test
    public final void anyNineDigitPasses() throws Exception {
        new IK("000000000");
        new IK("000000123");
        new IK("000013000");
        new IK("985642132");

    }

    @Test
    public void verify() throws Exception {
        assertFalse(new IK("123456789").isValid());
        assertTrue(new IK("510841109").isValid());
        assertFalse(new IK("510814109").isValid());
    }

}