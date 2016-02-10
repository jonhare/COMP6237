package uk.ac.soton.ecs.comp6237.l8;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.DisplayUtilities.ImageComponent;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.renderer.MBFImageRenderer;
import org.openimaj.image.renderer.RenderHints;
import org.openimaj.image.typography.FontStyle.HorizontalAlignment;
import org.openimaj.image.typography.general.GeneralFont;
import org.openimaj.image.typography.general.GeneralFontStyle;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Circle;
import org.openimaj.util.array.ArrayUtils;
import org.openimaj.util.pair.IndependentPair;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

/**
 * Demo showing MDS
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
@Demonstration(title = "Self Organising Map Demo")
public class SOMDemo implements Slide, ActionListener {
	private static final int MAX_ITER = 1000;
	private static final double INIT_LEARNING_RATE = 0.1;
	private MBFImage image;
	private ImageComponent ic;
	private BufferedImage bimg;
	private JButton runBtn;
	private JButton cnclBtn;
	private volatile boolean isRunning;
	private MBFImageRenderer renderer;
	// private BlogData rawdata = BlogData.loadSmallData(50);
	// private BlogData rawdata = new BlogData();
	private BlogData rawdata = RGBData.eightColors();
	float[][] data = normalise(rawdata.getCounts());

	private float[][][] som;
	private int iteration;
	private int somfactor = 8;

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel base = new JPanel();
		base.setOpaque(false);
		base.setPreferredSize(new Dimension(width, height));
		base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));

		image = new MBFImage(width, height - 56, ColourSpace.RGB);
		som = new float[(height - 56) / somfactor][width / somfactor][rawdata.getTerms().size()];
		renderer = image.createRenderer(RenderHints.ANTI_ALIASED);
		resetImage();

		ic = new DisplayUtilities.ImageComponent(true, false);
		ic.setShowPixelColours(false);
		ic.setShowXYPosition(false);
		ic.setAllowPanning(false);
		ic.setAllowZoom(false);
		base.add(ic);

		final JPanel controls = new JPanel();
		controls.setPreferredSize(new Dimension(width, 56));
		controls.setMaximumSize(new Dimension(width, 56));
		controls.setSize(new Dimension(width, 56));

		controls.add(new JSeparator(SwingConstants.VERTICAL));

		runBtn = new JButton("Run SOM");
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

		updateImage();

		return base;
	}

	private float[][] normalise(float[][] counts) {
		final float[][] out = new float[counts.length][];

		for (int i = 0; i < counts.length; i++)
			out[i] = ArrayUtils.normalise(counts[i].clone());

		return out;
	}

	private void resetImage() {
		image.fill(RGBColour.WHITE);
	}

	private void updateImage() {
		ic.setImage(bimg = ImageUtilities.createBufferedImageForDisplay(image, bimg));
	}

	private void initMDS() {
		// random init
		for (int i = 0; i < som.length; i++) {
			for (int j = 0; j < som[0].length; j++) {
				for (int k = 0; k < som[0][0].length; k++) {
					som[i][j][k] = (float) Math.random();
				}
			}
		}

		this.iteration = 0;

		drawPoints(false, null);
		updateImage();
	}

	private void drawPoints(boolean drawLabels, List<IndependentPair<String, Point2dImpl>> labels) {
		float minr = Float.MAX_VALUE, maxr = -Float.MAX_VALUE;
		float ming = Float.MAX_VALUE, maxg = -Float.MAX_VALUE;
		float minb = Float.MAX_VALUE, maxb = -Float.MAX_VALUE;

		for (int j = 0; j < som.length; j++) {
			for (int i = 0; i < som[0].length; i++) {
				if (som[j][i][0] < minr)
					minr = som[j][i][0];
				if (som[j][i][0] > maxr)
					maxr = som[j][i][0];

				if (som[j][i][1] < ming)
					ming = som[j][i][1];
				if (som[j][i][1] > maxg)
					maxg = som[j][i][1];

				if (som[j][i][2] < minb)
					minb = som[j][i][2];
				if (som[j][i][2] > maxb)
					maxb = som[j][i][2];
			}
		}

		// minr = 0;
		// maxr = 1;
		// ming = 0;
		// maxg = 1;
		// minb = 0;
		// maxb = 1;

		final float[][] r = image.bands.get(0).pixels;
		final float[][] g = image.bands.get(1).pixels;
		final float[][] b = image.bands.get(2).pixels;
		for (int j = 0; j < som.length; j++) {
			for (int i = 0; i < som[0].length; i++) {
				for (int jj = 0; jj < somfactor; jj++) {
					for (int ii = 0; ii < somfactor; ii++) {
						r[j * somfactor + jj][i * somfactor + ii] = (som[j][i][0] + minr) / (maxr - minr);
						g[j * somfactor + jj][i * somfactor + ii] = (som[j][i][1] + ming) / (maxg - ming);
						b[j * somfactor + jj][i * somfactor + ii] = (som[j][i][2] + minb) / (maxb - minb);
					}
				}
			}
		}

		if (drawLabels) {
			for (final IndependentPair<String, Point2dImpl> l : labels) {
				final String labelText = l.firstObject();
				final Point2dImpl pt = l.getSecondObject();

				drawPoint(pt, labelText);
			}
		}
	}

	private void drawPoint(final Point2dImpl pti, String text) {
		renderer.drawShapeFilled(new Circle(pti, 20), RGBColour.MAGENTA);
		final GeneralFontStyle<Float[]> style = new GeneralFontStyle<Float[]>(new GeneralFont("Arial", Font.BOLD),
				renderer, false);
		style.setHorizontalAlignment(HorizontalAlignment.HORIZONTAL_CENTER);
		renderer.drawText(text, (int) (pti.x), (int) (pti.y + 10), style);
	}

	List<IndependentPair<String, Point2dImpl>> performStep() {
		final List<IndependentPair<String, Point2dImpl>> labels = new ArrayList<>();

		for (int k = 0; k < data.length; k++) {
			final float[] vector = data[k];
			// find BMU
			float bestScore = Float.MAX_VALUE;
			int besti = 0, bestj = 0;
			for (int i = 0; i < som.length; i++) {
				for (int j = i + 1; j < som[0].length; j++) {
					final double score = FloatFVComparison.EUCLIDEAN.compare(vector, som[i][j]);

					if (score < bestScore) {
						besti = i;
						bestj = j;
						bestScore = (float) score;
					}
				}
			}

			System.out.println(rawdata.getBlogNames().get(k) + " " + bestScore);

			labels.add(IndependentPair.pair(rawdata.getBlogNames().get(k),
					new Point2dImpl(bestj * somfactor, besti * somfactor)));

			// Perform update
			for (int i = 0; i < som.length; i++) {
				for (int j = 0; j < som[0].length; j++) {
					final float lr = getLearningRate();
					final float neighbourhoodWeight = getNeighbourhoodWeight(i, j, besti, bestj);
					final float weight = lr * neighbourhoodWeight;

					if (Math.abs(weight) > 0.0000000001) {
						final float[] diff = ArrayUtils.subtract(vector.clone(), som[i][j]);
						ArrayUtils.multiply(diff, weight);
						ArrayUtils.sum(som[i][j], diff);
					}
				}
			}
		}

		iteration++;

		return labels;
	}

	private float getNeighbourhoodWeight(int x, int y, int cx, int cy) {
		final double distSq = ((cx - x) * (cx - x) + (cy - y) * (cy - y));

		final int gridRadius = Math.max(som.length, som[0].length) / 2;
		final double timeConstant = MAX_ITER / Math.log(gridRadius);
		final double radius = gridRadius * Math.exp(-iteration / timeConstant);

		return (float) Math.exp(-(distSq) / (2 * radius * radius));
	}

	private float getLearningRate() {
		return (float) (INIT_LEARNING_RATE * Math.exp(-iteration / MAX_ITER));
	}

	@Override
	public void close() {
		isRunning = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("button.clear")) {
			resetImage();
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
					List<IndependentPair<String, Point2dImpl>> labels = null;
					while (isRunning && iter++ < MAX_ITER) {
						labels = performStep();
						drawPoints(true, labels);
						updateImage();
					}
					drawPoints(true, labels);
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
		new SlideshowApplication(new SOMDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
