package uk.ac.soton.ecs.comp6237.l8;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.TextAnchor;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;
import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparator;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.math.geometry.line.Line2d;
import org.openimaj.math.geometry.point.Point2dImpl;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

/**
 * Demo showing MDS
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
@Demonstration(title = "Multidimensional Scaling Demo")
public class MDSDemo implements Slide, ActionListener {
	private static final int MAX_ITER = 50000;
	private static final double INIT_LEARNING_RATE = 0.005;
	private JButton runBtn;
	private JButton cnclBtn;
	private volatile boolean isRunning;
	private FloatFVComparator distanceMeasure = null;
	private JComboBox<String> distCombo;
	private ItemTermData data = new ItemTermData("moduledata.txt");
	private List<Point2dImpl> points = new ArrayList<Point2dImpl>();
	private double[][] distances = new double[data.getItemNames().size()][data.getItemNames().size()];
	private double[][] fakeDistances = new double[data.getItemNames().size()][data.getItemNames().size()];

	class Dataset extends AbstractXYDataset {
		private static final long serialVersionUID = 1L;

		@Override
		public Number getY(int series, int item) {
			return points.get(item).y;
		}

		@Override
		public Number getX(int series, int item) {
			return points.get(item).x;
		}

		public String getLabel(int series, int item) {
			return data.getItemNames().get(item);
		}

		@Override
		public int getItemCount(int arg0) {
			return data.getItemNames().size();
		}

		@Override
		public int getSeriesCount() {
			return 1;
		}

		@Override
		public Comparable<String> getSeriesKey(int arg0) {
			return "DATA";
		}
	}

	Dataset dataset = new Dataset();
	private JFreeChart chart;
	private ChartPanel chartPanel;
	private JLabel iterLabel;

	public MDSDemo() {
		for (int i = 0; i < dataset.getItemCount(0); i++)
			this.points.add((Point2dImpl) Point2dImpl.createRandomPoint());
	}

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel base = new JPanel();
		base.setOpaque(false);
		base.setPreferredSize(new Dimension(width, height));
		base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));

		chart = ChartFactory.createScatterPlot("", "", "", dataset, PlotOrientation.VERTICAL, false, false, false);

		final XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true) {
			private static final long serialVersionUID = 1L;

			@Override
			public ItemLabelPosition getPositiveItemLabelPosition(int row, int column) {
				return new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_LEFT);
			}
		};
		final Font font = Font.decode("Helvetica Neue-22");
		renderer.setBaseItemLabelFont(font);
		chart.getXYPlot().setRenderer(renderer);
		// chart.getXYPlot().getDomainAxis().setRange(-0.5, 5.5)
		chart.getXYPlot().getDomainAxis().setTickLabelFont(font);
		// chart.getXYPlot().getDomainAxis().setTickUnit(new NumberTickUnit(1))
		// chart.getXYPlot().getRangeAxis().setRange(-0.5, 5.5)
		chart.getXYPlot().getRangeAxis().setTickLabelFont(font);
		// chart.getXYPlot().getRangeAxis().setTickUnit(new NumberTickUnit(1))

		chart.getXYPlot().getRenderer().setBaseItemLabelGenerator(new StandardXYItemLabelGenerator() {
			private static final long serialVersionUID = 1L;

			@Override
			public String generateLabel(XYDataset ds, int series, int item) {
				return ((Dataset) ds).getLabel(series, item);
			};
		});
		chart.getXYPlot().getRenderer().setBaseItemLabelsVisible(true);

		chartPanel = new ChartPanel(chart);
		chart.setBackgroundPaint(new java.awt.Color(255, 255, 255, 255));
		chart.getXYPlot().setBackgroundPaint(java.awt.Color.WHITE);
		chart.getXYPlot().setRangeGridlinePaint(java.awt.Color.GRAY);
		chart.getXYPlot().setDomainGridlinePaint(java.awt.Color.GRAY);

		chartPanel.setSize(width, height - 50);
		chartPanel.setPreferredSize(chartPanel.getSize());
		base.add(chartPanel);

		final JPanel controls = new JPanel();
		controls.setPreferredSize(new Dimension(width, 50));
		controls.setMaximumSize(new Dimension(width, 50));
		controls.setSize(new Dimension(width, 50));

		controls.add(new JSeparator(SwingConstants.VERTICAL));
		controls.add(new JLabel("Distance:"));

		distCombo = new JComboBox<String>();
		distCombo.addItem("Euclidean");
		distCombo.addItem("1-Pearson");
		distCombo.addItem("1-Cosine");
		controls.add(distCombo);

		controls.add(new JSeparator(SwingConstants.VERTICAL));

		runBtn = new JButton("Run MDS");
		runBtn.setActionCommand("button.run");
		runBtn.addActionListener(this);
		controls.add(runBtn);

		controls.add(new JSeparator(SwingConstants.VERTICAL));

		cnclBtn = new JButton("Cancel");
		cnclBtn.setEnabled(false);
		cnclBtn.setActionCommand("button.cancel");
		cnclBtn.addActionListener(this);
		controls.add(cnclBtn);

		base.add(controls);

		controls.add(new JSeparator(SwingConstants.VERTICAL));
		iterLabel = new JLabel("                         ");
		final Dimension size = iterLabel.getPreferredSize();
		iterLabel.setMinimumSize(size);
		iterLabel.setPreferredSize(size);
		controls.add(iterLabel);

		updateImage();

		return base;
	}

	private void initMDS() {
		if (this.distCombo.getSelectedItem().equals("Euclidean"))
			this.distanceMeasure = FloatFVComparison.EUCLIDEAN;
		else if (this.distCombo.getSelectedItem().equals("1-Cosine"))
			this.distanceMeasure = FloatFVComparison.COSINE_DIST;
		else if (this.distCombo.getSelectedItem().equals("1-Pearson")) {
			this.distanceMeasure = new FloatFVComparator() {

				@Override
				public double compare(FloatFV o1, FloatFV o2) {
					return 1 - FloatFVComparison.CORRELATION.compare(o1, o2);
				}

				@Override
				public boolean isDistance() {
					return true;
				}

				@Override
				public double compare(float[] h1, float[] h2) {
					return 1 - FloatFVComparison.CORRELATION.compare(h1, h2);
				}
			};
		}

		// random init
		this.points.clear();
		final float[][] counts = data.getCounts();
		for (int i = 0; i < distances.length; i++)
		{
			this.points.add((Point2dImpl) Point2dImpl.createRandomPoint());

			for (int j = i + 1; j < distances.length; j++) {
				double d = distanceMeasure.compare(counts[i], counts[j]);
				if (d == 0)
					d = 0.001;
				distances[i][j] = d;
				distances[j][i] = d;
			}
		}

		updateImage();
	}

	private void updateImage() {
		chart.getXYPlot().setDataset(chart.getXYPlot().getDataset());
	}

	double performStep(double lasterror, int iter) {
		for (int i = 0; i < distances.length; i++) {
			for (int j = i + 1; j < distances.length; j++) {
				final double d = Line2d.distance(points.get(i), points.get(j));
				fakeDistances[i][j] = d;
				fakeDistances[j][i] = d;
			}
		}

		final Point2dImpl[] grad = new Point2dImpl[distances.length];
		for (int i = 0; i < distances.length; i++)
			grad[i] = new Point2dImpl();

		double totalError = 0;
		for (int k = 0; k < distances.length; k++) {
			for (int j = k + 1; j < distances.length; j++) {
				if (k == j)
					continue;

				final double errorterm = (fakeDistances[j][k] - distances[j][k]) / distances[j][k];

				grad[k].x += ((points.get(k).x - points.get(j).x) / fakeDistances[j][k]) * errorterm;
				grad[k].y += ((points.get(k).y - points.get(j).y) / fakeDistances[j][k]) * errorterm;

				totalError += Math.abs(errorterm);
			}
		}

		if (totalError >= lasterror)
			return totalError;

		final float rate = getLearningRate(iter);
		for (int k = 0; k < distances.length; k++) {
			points.get(k).x -= rate * grad[k].x;
			points.get(k).y -= rate * grad[k].y;
		}

		return totalError;
	}

	private float getLearningRate(int iter) {
		return (float) (INIT_LEARNING_RATE * Math.exp(-iter / MAX_ITER));
	}

	@Override
	public void close() {
		isRunning = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("button.clear")) {
			updateImage();
		} else if (e.getActionCommand().equals("button.run")) {
			runBtn.setEnabled(false);
			cnclBtn.setEnabled(true);
			isRunning = true;

			new Thread(new Runnable() {
				@Override
				public void run() {
					if (isRunning) {
						initMDS();
						try {
							Thread.sleep(500);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}

					int iter = 0;
					double lasterror = Double.MAX_VALUE;
					while (isRunning && iter++ < MAX_ITER) {
						final double thiserror = performStep(lasterror, iter);
						iterLabel.setText(String.format("%4.2f %5d", thiserror, iter));
						updateImage();

						if (thiserror >= lasterror)
							break;

						lasterror = thiserror;
					}
					updateImage();

					runBtn.setEnabled(true);
					cnclBtn.setEnabled(false);
					isRunning = false;
				}
			}).start();
		} else if (e.getActionCommand().equals("button.cancel")) {
			isRunning = false;
			cnclBtn.setEnabled(false);
		}
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new MDSDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
