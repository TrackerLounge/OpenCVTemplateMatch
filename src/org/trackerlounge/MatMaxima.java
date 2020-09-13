package org.trackerlounge;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

/* 
 * Code extracted from:
 * https://github.com/openpnp/openpnp/blob/develop/src/main/java/org/openpnp/util/OpenCvUtils.java
*/
public class MatMaxima {
	
	private enum MinMaxState {
        BEFORE_INFLECTION,
        AFTER_INFLECTION
    }
	
	 /**
     * Ported from the C++ version in FireSight by Karl Lew, which is licensed under the 
     * MIT license.
     * https://github.com/firepick1/FireSight
     * @param mat
     * @param rangeMin
     * @param rangeMax
     * @return
     */
    public static List<java.awt.Point> matMaxima(Mat mat, double rangeMin, double rangeMax) {
        List<java.awt.Point> locations = new ArrayList<>();

        int rEnd = mat.rows() - 1;
        int cEnd = mat.cols() - 1;

        // CHECK EACH ROW MAXIMA FOR LOCAL 2D MAXIMA
        for (int r = 0; r <= rEnd; r++) {
            MinMaxState state = MinMaxState.BEFORE_INFLECTION;
            double curVal = mat.get(r, 0)[0];
            for (int c = 1; c <= cEnd; c++) {
                double val = mat.get(r, c)[0];

                if (val == curVal) {
                    continue;
                }
                else if (curVal < val) {
                    if (state == MinMaxState.BEFORE_INFLECTION) {
                        // n/a
                    }
                    else {
                        state = MinMaxState.BEFORE_INFLECTION;
                    }
                }
                else { // curVal > val
                    if (state == MinMaxState.BEFORE_INFLECTION) {
                        if (rangeMin <= curVal && curVal <= rangeMax) { // ROW
                                                                        // MAXIMA
                            if (0 < r && (mat.get(r - 1, c - 1)[0] >= curVal
                                    || mat.get(r - 1, c)[0] >= curVal)) {
                                // cout << "reject:r-1 " << r << "," << c-1 <<
                                // endl;
                                // - x x
                                // - - -
                                // - - -
                            }
                            else if (r < rEnd && (mat.get(r + 1, c - 1)[0] > curVal
                                    || mat.get(r + 1, c)[0] > curVal)) {
                                // cout << "reject:r+1 " << r << "," << c-1 <<
                                // endl;
                                // - - -
                                // - - -
                                // - x x
                            }
                            else if (1 < c && (0 < r && mat.get(r - 1, c - 2)[0] >= curVal
                                    || mat.get(r, c - 2)[0] > curVal
                                    || r < rEnd && mat.get(r + 1, c - 2)[0] > curVal)) {
                                // cout << "reject:c-2 " << r << "," << c-1 <<
                                // endl;
                                // x - -
                                // x - -
                                // x - -
                            }
                            else {
                                locations.add(new java.awt.Point(c - 1, r));
                            }
                        }
                        state = MinMaxState.AFTER_INFLECTION;
                    }
                    else {
                        // n/a
                    }
                }

                curVal = val;
            }

            // PROCESS END OF ROW
            if (state == MinMaxState.BEFORE_INFLECTION) {
                if (rangeMin <= curVal && curVal <= rangeMax) { // ROW MAXIMA
                    if (0 < r && (mat.get(r - 1, cEnd - 1)[0] >= curVal
                            || mat.get(r - 1, cEnd)[0] >= curVal)) {
                        // cout << "rejectEnd:r-1 " << r << "," << cEnd-1 <<
                        // endl;
                        // - x x
                        // - - -
                        // - - -
                    }
                    else if (r < rEnd && (mat.get(r + 1, cEnd - 1)[0] > curVal
                            || mat.get(r + 1, cEnd)[0] > curVal)) {
                        // cout << "rejectEnd:r+1 " << r << "," << cEnd-1 <<
                        // endl;
                        // - - -
                        // - - -
                        // - x x
                    }
                    else if (1 < r && mat.get(r - 1, cEnd - 2)[0] >= curVal
                            || mat.get(r, cEnd - 2)[0] > curVal
                            || r < rEnd && mat.get(r + 1, cEnd - 2)[0] > curVal) {
                        // cout << "rejectEnd:cEnd-2 " << r << "," << cEnd-1 <<
                        // endl;
                        // x - -
                        // x - -
                        // x - -
                    }
                    else {
                        locations.add(new java.awt.Point(cEnd, r));
                    }
                }
            }
        }

        return locations;
    }   
}
