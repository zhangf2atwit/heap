package edu.wit.cs.comp2350.tests;


import java.awt.Color;
import java.util.Random;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.Function2D;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import edu.wit.cs.comp2350.A2;

public class ChartMaker extends ApplicationFrame {
	private static final long serialVersionUID = 1L;
	private static final int DATASETS = 4;


	/**
	 *
	 * @param title  the frame title.
	 */
	public ChartMaker(String title) {
		super(title);
		JPanel chartPanel = createDemoPanel();
		chartPanel.setPreferredSize(new java.awt.Dimension(850, 500));
		setContentPane(chartPanel);
	}

	/**
	 * Creates a chart with four datasets of algorithm runtimes and
	 * sets appearance of chart objects.
	 *
	 * @return A chart instance.
	 */
	private static JFreeChart createChart() {
		XYDataset[] data = new XYDataset[DATASETS];

		data[0] = createDataset('m', "min2scan");
		data[1] = createDataset('s', "sort");
		data[2] = createDataset('h', "heap");
		data[3] = createDataset('q', "sequential");


		JFreeChart chart = ChartFactory.createXYLineChart(
				"Floating point runtimes",// chart title
				"input size",              // x axis label
				"time(ms)",                // y axis label
				data[0],                   // data
				PlotOrientation.VERTICAL, 
				true,                      // include legend
				true,                      // tooltips
				false                      // urls
				);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.getDomainAxis().setLowerMargin(0.0);
		plot.getDomainAxis().setUpperMargin(0.0);
		XYLineAndShapeRenderer renderer[] = new XYLineAndShapeRenderer[DATASETS];

		for (int i = 0; i < DATASETS; i++) {
			plot.setDataset(i, data[i]);
			renderer[i] = new XYLineAndShapeRenderer(); 
			plot.setRenderer(i, renderer[i]);
		}

		plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.red); 
		plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(0, Color.blue);
		plot.getRendererForDataset(plot.getDataset(2)).setSeriesPaint(0, Color.green);
		plot.getRendererForDataset(plot.getDataset(3)).setSeriesPaint(0, Color.darkGray);

		return chart;
	}

	/**
	 * Creates a dataset based on algorithm runtimes.
	 *
	 * @param algo  The character used to represent algorithm
	 * @param name  The name of algorithm to appear in the legend
	 * 
	 * @return A sample dataset.
	 */
	public static XYDataset createDataset(char algo, String name) {
		XYDataset result = DatasetUtils.sampleFunction2D(new AddRuns(algo),
				0.0, 1000.0, 40, name);
		return result;
	}

	/**
	 *
	 * @return A panel.
	 */
	public static JPanel createDemoPanel() {
		JFreeChart chart = createChart();
		return new ChartPanel(chart);
	}

	/**
	 * Generates all the runtimes for the graph.
	 */
	static class AddRuns implements Function2D {
		private char algo;

		private float[] generateRandFloatArray(int size) {
			float[] ret = new float[size];

			Random r = new Random();
			for (int i = 0; i < size; i++) {
				ret[i] = r.nextFloat()*1000;
			}
			return ret;
		}	

		public AddRuns(char a) { algo = a; }

		public double getValue(double x) {
			float[] values = new float[Double.valueOf(x).intValue()];

			final int samples = 400;	// number of samples at each array size
			double sec = 0;

			for (int i = 0; i < samples; i++) {

				values = generateRandFloatArray(Double.valueOf(x).intValue());

				long start = System.nanoTime();
				switch (algo) {
				case 'h':
					A2.heapAdd(values);
					break;
				case 'm':
					A2.min2ScanAdd(values);
					break;
				case 'q':
					A2.seqAdd(values);
					break;
				case 's':
					A2.sortAdd(values);
					break;
				default:
					System.out.println("Invalid adding algorithm");
					System.exit(0);
					break;
				}
				long end = System.nanoTime();

				sec += (end-start)/1E6;
			}

			return sec/samples;
		}
	}

	/**
	 * Starting point for the chartmaker.
	 *
	 * @param args  ignored.
	 */
	public static void main(String[] args) {
		ChartMaker cm = new ChartMaker(
				"Chart Window");
		cm.pack();
		RefineryUtilities.centerFrameOnScreen(cm);
		cm.setVisible(true);
	}

}
