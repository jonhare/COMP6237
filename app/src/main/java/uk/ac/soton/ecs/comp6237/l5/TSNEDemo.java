package uk.ac.soton.ecs.comp6237.l5;

import static org.nd4j.linalg.factory.Nd4j.randn;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.deeplearning4j.plot.BarnesHutTsne;
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
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dimensionalityreduction.PCA;
import org.nd4j.linalg.factory.Nd4j;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.l3.ItemTermData;
import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

@Demonstration(title = "t-SNE Demo")
public class TSNEDemo implements Slide, ActionListener {
	private final int maxIter = 1000;
	private final double realMin = 1e-12f;
	private final double initialMomentum = 5e-1f;
	private final double finalMomentum = 8e-1f;
	private final double momentum = 5e-1f;
	private final int switchMomentumIteration = 100;
	private final boolean normalize = true;
	private final int stopLyingIteration = 100;
	private final double tolerance = 1e-5f;
	private final double learningRate = 5e-1;// 1e-1f;
	private final boolean useAdaGrad = true;
	private final double perplexity = 30;
	private final double minGain = 1e-1f;
	private final double theta = 0.5;
	private final boolean invert = true;
	private final int numDim = 2;
	private final String similarityFunction = "cosinesimilarity";

	private ItemTermData rawdata = new ItemTermData("moduledata.txt");
	INDArray Y = randn(rawdata.getCounts().length, 2, Nd4j.getRandom()).muli(1e-3f);

	class Dataset extends AbstractXYDataset {
		private static final long serialVersionUID = 1L;

		@Override
		public Number getY(int series, int item) {
			return Y.getDouble(item, 1);
		}

		@Override
		public Number getX(int series, int item) {
			return Y.getDouble(item, 0);
		}

		public String getLabel(int series, int item) {
			return rawdata.getItemNames().get(item);
		}

		@Override
		public int getItemCount(int arg0) {
			return rawdata.getItemNames().size();
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

	private JButton runBtn;
	private JButton cnclBtn;
	private volatile boolean isRunning;
	private JFreeChart chart;
	private ChartPanel chartPanel;
	private JLabel iterLabel;

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

		runBtn = new JButton("Run t-SNE");
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

	private void updateImage() {
		chart.getXYPlot().setDataset(chart.getXYPlot().getDataset());
	}

	public void run() {

		final INDArray data = PCA.pca(Nd4j.create(rawdata.getCounts()), 50, true);
		// final INDArray data = Nd4j.create(rawdata.getCounts());

		System.out.println(data);

		final BarnesHutTsne tsne = new BarnesHutTsne(numDim, similarityFunction, theta, invert, maxIter, realMin,
				initialMomentum,
				finalMomentum, momentum, switchMomentumIteration, normalize, stopLyingIteration, tolerance,
				learningRate, useAdaGrad, perplexity, null, minGain)
		{
			@Override
			public void step(INDArray p, int i) {
				if (isRunning) {
					super.step(p, i);
					TSNEDemo.this.update();
				} else {
					this.maxIter = 0;
				}
			}
		};

		try {
			Y = randn(rawdata.getCounts().length, 2, Nd4j.getRandom()).muli(1e-3f);
			tsne.setData(Y);
			updateImage();
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tsne.fit(data);
	}

	void update() {
		updateImage();
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
					TSNEDemo.this.run();

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
		new SlideshowApplication(new TSNEDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
