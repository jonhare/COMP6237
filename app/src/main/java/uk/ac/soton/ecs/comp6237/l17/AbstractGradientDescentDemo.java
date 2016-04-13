package uk.ac.soton.ecs.comp6237.l17;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.openimaj.content.slideshow.Slide;

/**
 * Base class for gradient descent demos showing how to fit a line to data
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 *
 */
public abstract class AbstractGradientDescentDemo implements Slide, Runnable {

	protected static class ImageContainer extends Component {
		private static final long serialVersionUID = 1L;
		BufferedImage image;

		public ImageContainer(BufferedImage img) {
			this.image = img;
			this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.drawImage(image, 0, 0, null);
		}

		public void update(BufferedImage img) {
			image = img;
			this.repaint();
		}
	}

	protected double[] params = { 0, 0 };
	int iter = 0;
	private JFreeChart chart;
	private ImageContainer chartContainer;
	private DefaultXYDataset chartDataset;
	private JTextField paramsField;
	private DefaultXYDataset errorDataset;
	protected double[][] X;
	private JFreeChart errorChart;
	private ImageContainer errorContainer;
	private double[][] errorSeries;
	protected double alpha = 0.01;
	private int maxIter = 500;

	public AbstractGradientDescentDemo() {
		super();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel base = new JPanel();
		base.setOpaque(false);
		base.setPreferredSize(new Dimension(width, height));
		base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));

		chartDataset = new DefaultXYDataset();
		X = createData();
		chartDataset.addSeries("points", X);
		final double[][] lineData = computeLineData();
		chartDataset.addSeries("line", lineData);

		chart = ChartFactory.createXYLineChart(null, "x", "y", chartDataset, PlotOrientation.VERTICAL,
				false, false, false);
		((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()).setSeriesLinesVisible(0, false);
		((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()).setSeriesShapesVisible(0, true);
		((NumberAxis) chart.getXYPlot().getDomainAxis()).setRange(-5, 5);
		((NumberAxis) chart.getXYPlot().getRangeAxis()).setRange(-10, 10);

		((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()).setStroke(new BasicStroke(2.5f));

		chartContainer = new ImageContainer(chart.createBufferedImage(width, height / 2));
		base.add(chartContainer);

		final JPanel bottomPane = new JPanel();
		bottomPane.setPreferredSize(new Dimension(width, height / 2));
		base.add(bottomPane);

		final JPanel controlsdata = new JPanel();
		controlsdata.setLayout(new BoxLayout(controlsdata, BoxLayout.X_AXIS));
		bottomPane.add(controlsdata);
		final JButton button = new JButton("Go");
		controlsdata.add(button);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				button.setEnabled(false);
				base.requestFocus();
				new Thread(AbstractGradientDescentDemo.this).start();
			}
		});

		paramsField = new JTextField(20);
		paramsField.setOpaque(false);
		paramsField.setFont(Font.decode("Monaco-24"));
		paramsField.setHorizontalAlignment(JTextField.CENTER);
		paramsField.setEditable(false);
		paramsField.setBorder(null);
		paramsField.setText(String.format("%2.2f, %2.2f", params[0], params[1]));
		controlsdata.add(paramsField);

		errorDataset = new DefaultXYDataset();
		errorSeries = new double[][] { { 0 }, { computeError() } };
		errorDataset.addSeries("data", errorSeries);
		errorChart = ChartFactory.createXYLineChart("Error over time",
				"Epoch", "Error", errorDataset,
				PlotOrientation.VERTICAL,
				false, false, false);
		((NumberAxis) errorChart.getXYPlot().getDomainAxis()).setRange(0, 1);
		((NumberAxis) errorChart.getXYPlot().getRangeAxis()).setRange(0, computeError());
		errorContainer = new ImageContainer(errorChart.createBufferedImage((width - 5) / 2, (height - 5) / 2));
		bottomPane.add(errorContainer);

		return base;
	}

	private double computeError() {
		double cost = 0;
		for (int i = 0; i < X[0].length; i++) {
			final double e = error(new double[] { X[0][i], X[1][i] }, params);
			cost += e * e;
		}
		return cost / (2 * X.length);

	}

	protected double error(double[] x, double[] params) {
		return ((x[0] * params[0] + params[1]) - x[1]);
	}

	private double[][] computeLineData() {
		return new double[][] { { -10, 10 }, { -10 * params[0] + params[1], 10 * params[0] + params[1] } };
	}

	private double[][] createData() {
		final Random rng = new Random(0);
		final double[][] data = new double[2][1000];
		for (int i = 0; i < data[0].length; i++) {
			data[0][i] = (rng.nextDouble() - 0.5) * 10;
			data[1][i] = data[0][i] + rng.nextGaussian() + 3;
		}
		return data;
	}

	@Override
	public void close() {
		params = new double[] { 0, 0 };
		iter = 0;
	}

	@Override
	public void run() {
		while (iter < maxIter) {
			iter++;

			performIteration();

			updateDisplay();
		}
	}

	protected void updateDisplay() {
		final double[][] tmp = new double[][] { Arrays.copyOf(errorSeries[0], iter + 1),
				Arrays.copyOf(errorSeries[1], iter + 1) };
		tmp[0][iter] = iter;
		tmp[1][iter] = this.computeError();
		this.errorSeries = tmp;

		chartDataset.removeSeries("line");
		chartDataset.addSeries("line", computeLineData());
		chartContainer.update(chart.createBufferedImage(chartContainer.image.getWidth(),
				chartContainer.image.getHeight()));

		paramsField.setText(String.format("%2.2f, %2.2f", params[0], params[1]));

		errorDataset.removeSeries("data");
		errorDataset.addSeries("data", errorSeries);
		((NumberAxis) errorChart.getXYPlot().getDomainAxis()).setRange(0, iter);
		errorContainer.update(errorChart.createBufferedImage(errorContainer.image.getWidth(),
				errorContainer.image.getHeight()));
	}

	/**
	 * Perform a single iteration (epoch) of gradient descent. Superclasses
	 * should override.
	 */
	protected void performIteration() {

	}

	protected double[] errorv(double[][] X, double[] params) {
		final double[] ev = new double[X[0].length];
		for (int i = 0; i < X[0].length; i++) {
			ev[i] = ((X[0][i] * params[0] + params[1]) - X[1][i]);
		}
		return ev;
	}

}
