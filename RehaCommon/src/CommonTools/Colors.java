package CommonTools;

import java.awt.Color;

/**
 *
 * Colors is an enumeration class that makes it easier to work with colors.
 * Methods are provided for
 *
 * conversion to hex strings, and for getting alpha channel colors.
 *
 *
 *
 * @author Nazmul Idris
 *
 * @version 1.0
 *
 * @since Apr 21, 2007, 12:55:24 PM
 *
 */

public enum Colors {

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// various colors in the pallete

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX



    Green(159, 205, 20),

    Yellow(Color.yellow),

    Blue(Color.blue),

    White(255, 255, 255),

    TaskPaneBlau(112, 141, 223),

    PiOrange(231, 120, 23),

    Gray(Color.gray.getRed(), Color.gray.getGreen(), Color.gray.getBlue());

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

// constructors

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    Colors(Color c) {

        _myColor = c;

    }

    Colors(int r, int g, int b) {

        _myColor = new Color(r, g, b);

    }





//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

// data

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    private Color _myColor;

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

// methods

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    public Color alpha(float t) {

        return new Color(_myColor.getRed(), _myColor.getGreen(), _myColor.getBlue(), (int) (t * 255f));

    }







    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("r=")

          .append(_myColor.getRed())

          .append(", g=")

          .append(_myColor.getGreen())

          .append(", b=")

          .append(_myColor.getBlue())

          .append("\n");

        return sb.toString();

    }



}// end enum Colors
