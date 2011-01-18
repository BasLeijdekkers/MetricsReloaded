package org.jfree.data;

import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntegerHistogramDataset extends HistogramDataset {
    /**
     * A list of maps.
     */
    private final List list = new ArrayList();

    /**
     * Adds a series.
     *
     * @param name   the series name.
     * @param values the values (<code>null</code> not permitted).
     */
    public void addSeries(String name, double[] values) {
        if (values == null) {
            throw new IllegalArgumentException(
                    "HistogramDataset.addSeries(...): 'values' argument must not be null.");
        }
        // work out bin strategy
        final double minimum = getMinimum(values);
        final double maximum = getMaximum(values);
        final int numberOfBins = 1 + (int) maximum - (int) minimum;

        final double binWidth = 1.0;
        // set up the bins
        double tmp = minimum;
        final HistogramBin[] bins = new HistogramBin[numberOfBins];
        for (int i = 0; i < bins.length; i++) {
            // make sure bins[bins.length]'s upper boundary ends at maxium
            // to avoid the rounding issue. the bins[0] lower boundary is
            // guaranteed start from min
            final HistogramBin bin;
            if (i == bins.length - 1) {
                bin = new HistogramBin(tmp, maximum);
            } else {
                bin = new HistogramBin(tmp, tmp + binWidth);
            }
            tmp += binWidth;
            bins[i] = bin;
        }
        // fill the bins
        for (double value : values) {
            for (HistogramBin bin : bins) {
                if (value >= bin.getStartBoundary()
                        && value <= bin.getEndBoundary()) {
                    // note the greedy <=
                    bin.incrementCount();
                    break; // break out of inner loop
                }
            }
        }
        // generic map for each series
        @NonNls final Map map = new HashMap();
        map.put("name", name);
        map.put("bins", bins);
        map.put("values.length", values.length);
        map.put("bin width", binWidth);
        list.add(map);
    }

    /**
     * Returns the minimum value from an array.
     *
     * @param values the values.
     * @return The minimum value.
     */
    private double getMinimum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException();
        }

        double min = Double.MAX_VALUE;
        for (double value : values) {
            if (value < min) {
                min = value;
            }
        }
        return min - 0.5;
    }

    /**
     * Returns the maximum value from an array.
     *
     * @param values the values.
     * @return The maximum value.
     */
    private double getMaximum(double[] values) {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException();
        }

        double max = -Double.MAX_VALUE;
        for (double value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max + 0.5;
    }

    /**
     * Returns the bins for a series.
     *
     * @param series the series index.
     * @return An array of bins.
     */
    HistogramBin[] getBins(int series) {
        @NonNls final Map map = (Map) list.get(series);
        return (HistogramBin[]) map.get("bins");
    }

    /**
     * Returns the total number of observations for a series.
     *
     * @param series the series index.
     * @return The total.
     */
    private int getTotal(int series) {
        @NonNls final Map map = (Map) list.get(series);
        return (Integer) map.get("values.length");
    }

    /**
     * Returns the bin width for a series.
     *
     * @param series the series index (zero based).
     * @return The bin width.
     */
    private double getBinWidth(int series) {
        @NonNls final Map map = (Map) list.get(series);
        return (Double) map.get("bin width");
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return The series count.
     */
    public int getSeriesCount() {
        return list.size();
    }

    /**
     * Returns the name for a series.
     *
     * @param series the series index (zero based).
     * @return The series name.
     */
    public String getSeriesName(int series) {
        @NonNls final Map map = (Map) list.get(series);
        return (String) map.get("name");
    }

    /**
     * Returns the number of data items for a series.
     *
     * @param series the series index (zero based).
     * @return The item count.
     */
    public int getItemCount(int series) {
        return getBins(series).length;
    }

    /**
     * Returns the X value for a bin.
     * <p/>
     * This value won't be used for plotting histograms, since the renderer will
     * ignore it. But other renderers can use it (for example, you could use the
     * dataset to create a line chart).
     *
     * @param series the series index (zero based).
     * @param item   the item index (zero based).
     * @return The start value.
     */
    public Number getXValue(int series, int item) {
        final HistogramBin[] bins = getBins(series);
        final HistogramBin bin = bins[item];
        return (bin.getStartBoundary() + bin.getEndBoundary()) / 2.0;
    }

    /**
     * Returns the Y value for a bin.
     *
     * @param series the series index (zero based).
     * @param item   the item index (zero based).
     * @return The Y value.
     */
    public Number getYValue(int series, int item) {
        final HistogramBin[] bins = getBins(series);
        final double total = (double) getTotal(series);
        final double binWidth = getBinWidth(series);

        if (getType() == FREQUENCY) {
            return (double) bins[item].getCount();
        } else if (getType() == RELATIVE_FREQUENCY) {
            return (double) bins[item].getCount() / total;
        } else if (getType() == SCALE_AREA_TO_1) {
            return (double) bins[item].getCount() / (binWidth * total);
        } else { // pretty sure this shouldn't ever happen
            throw new IllegalStateException();
        }
    }

    /**
     * Returns the start value for a bin.
     *
     * @param series the series index (zero based).
     * @param item   the item index (zero based).
     * @return The start value.
     */
    public Number getStartXValue(int series, int item) {
        final HistogramBin[] bins = getBins(series);
        return bins[item].getStartBoundary();
    }

    /**
     * Returns the end value for a bin.
     *
     * @param series the series index (zero based).
     * @param item   the item index (zero based).
     * @return The end value.
     */
    public Number getEndXValue(int series, int item) {
        final HistogramBin[] bins = getBins(series);
        return bins[item].getEndBoundary();
    }

    /**
     * Returns the Y value for a bin.
     *
     * @param series the series index (zero based).
     * @param item   the item index (zero based).
     * @return The Y value.
     */
    public Number getStartYValue(int series, int item) {
        //HistogramBin[] bins = getBins(series);
        return getYValue(series, item);
    }

    /**
     * Returns the Y value for a bin.
     *
     * @param series the series index (zero based).
     * @param item   the item index (zero based).
     * @return The Y value.
     */
    public Number getEndYValue(int series, int item) {
        //HistogramBin[] bins = getBins(series);
        return getYValue(series, item);
    }
}
