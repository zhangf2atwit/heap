package edu.wit.cs.comp2350.tests;

import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import edu.wit.cs.comp2350.A2;

public class ErrorChartMaker extends ApplicationFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new chart.
	 *
	 * @param title  the frame title.
	 */
	public ErrorChartMaker(final String title) {

		super(title);

		BoxAndWhiskerCategoryDataset dataset = createSampleDataset();

		CategoryAxis xAxis = new CategoryAxis("Input size");
		NumberAxis yAxis = new NumberAxis("Average roundoff error");
		yAxis.setAutoRangeIncludesZero(false);
		BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setFillBox(false);
		renderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator());

		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(1, Color.blue);
		renderer.setSeriesPaint(2, Color.green);
		renderer.setSeriesPaint(3, Color.magenta);

		CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

		JFreeChart chart = new JFreeChart(
				"Roundoff Error",
				new Font("SansSerif", Font.BOLD, 14),
				plot,
				true
				);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(850, 500));
		setContentPane(chartPanel);

	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return A sample dataset.
	 */
	private BoxAndWhiskerCategoryDataset createSampleDataset() {

		int sizeMax = 9000;
		int runs = 100;

		SortedMap<Character, String> algos = new TreeMap<Character, String>();
		algos.put('h', "heapAdd");
		algos.put('m', "min2scan");
		algos.put('r', "sort");
		algos.put('s', "sequential");

		DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

		// for all sizes
		for (int size = 3000; size <= sizeMax; size += 3000) {
			//		build map of lists
			SortedMap<Character, List<Double>> dataLists = new TreeMap<Character, List<Double>>();
			for (char c: algos.keySet())
				dataLists.put(c, new ArrayList<>(runs));
			//		for all runs
			for (int run = 0; run < runs; run++) {
				//			generate array and BigDecimal sum
				float[] values = generateRandFloatArray(size);
				BigDecimal trueSum = addBigDecimal(values);
				double roundTrueSum = trueSum.doubleValue();
				//			for all algos
				for (char c: algos.keySet()) {
					//				calculate sum
					float sum = -1;
					switch (c) {
					case 'h':
						sum = A2.heapAdd(Arrays.copyOf(values, values.length));
						break;
					case 'm':
						sum = A2.min2ScanAdd(Arrays.copyOf(values, values.length));
						break;
					case 's':
						sum = A2.seqAdd(Arrays.copyOf(values, values.length));
						break;
					case 'r':
						sum = A2.sortAdd(Arrays.copyOf(values, values.length));
						break;
					default:
						System.out.println("Invalid adding algorithm");
						System.exit(0);
						break;
					}
					//				add to appropriate list
					dataLists.get(c).add(sum-roundTrueSum);

				}
			}
			//		add 4 lists to dataset
			for (char c: algos.keySet())
				dataset.add(dataLists.get(c), algos.get(c), size);
		}

		return dataset;
	}

	private BigDecimal addBigDecimal(float[] arr) {
		BigDecimal ret = new BigDecimal(0);

		for (float f: arr)
			ret = ret.add(new BigDecimal(f));

		return ret;
	}

	private float[] generateRandFloatArray(int size) {
		float[] ret = new float[size];

		Random r = new Random();
		for (int i = 0; i < size; i++) {
			ret[i] = r.nextFloat();
		}
		return ret;
	}

	public static void main(final String[] args) {

		ErrorChartMaker demo = new ErrorChartMaker("Chart Window");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}

}
