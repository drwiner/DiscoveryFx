package com.discoveryfx;

import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DataTableHelper {

    public static Color expInterpolateColor(Color startValue, Color endValue, double t){
        if (t <= 0.0) return startValue;
        if (t >= 1.0) return endValue;
        float ft = (float) (Math.pow(2, t) -1);
        //(2 ^ (.4) - 1)
        return new Color(
                startValue.getRed() +(endValue.getRed()  - startValue.getRed() )     * ft ,
                startValue.getGreen() + (endValue.getGreen()  - startValue.getGreen()  )   * ft,
                startValue.getBlue()    + (endValue.getBlue()    - startValue.getBlue())    * ft,
                startValue.getOpacity() + (endValue.getOpacity() - startValue.getOpacity()) * ft
        );
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
