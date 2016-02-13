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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;
import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparator;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.DisplayUtilities.ImageComponent;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.renderer.MBFImageRenderer;
import org.openimaj.image.renderer.RenderHints;
import org.openimaj.image.typography.general.GeneralFont;
import org.openimaj.image.typography.general.GeneralFontStyle;
import org.openimaj.math.geometry.line.Line2d;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.point.PointList;
import org.openimaj.math.geometry.shape.Circle;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.math.geometry.transforms.TransformUtilities;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;
import Jama.Matrix;

/**
 * Demo showing MDS
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
@Demonstration(title = "Multidimensional Scaling Demo")
public class MDSDemo2 implements Slide, ActionListener {
	private MBFImage image;
	private ImageComponent ic;
	private BufferedImage bimg;
	private JButton runBtn;
	private JButton cnclBtn;
	private volatile boolean isRunning;
	private MBFImageRenderer renderer;
	private FloatFVComparator distanceMeasure = null;
	private JComboBox<String> distCombo;
	private ItemTermData data = new ItemTermData("moduledata-small.txt");
	// private BlogData data = new BlogData();
	private List<Point2dImpl> points = new ArrayList<Point2dImpl>();
	private double[][] distances = new double[data.getItemNames().size()][data.getItemNames().size()];
	private double[][] fakeDistances = new double[data.getItemNames().size()][data.getItemNames().size()];
	private float rate = 0.0001f;

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel base = new JPanel();
		base.setOpaque(false);
		base.setPreferredSize(new Dimension(width, height));
		base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));

		image = new MBFImage(width, height - 50, ColourSpace.RGB);
		renderer = image.createRenderer(RenderHints.ANTI_ALIASED);
		resetImage();

		ic = new DisplayUtilities.ImageComponent(true, false);
		ic.setShowPixelColours(false);
		ic.setShowXYPosition(false);
		ic.setAllowPanning(false);
		ic.setAllowZoom(false);
		base.add(ic);

		final JPanel controls = new JPanel();
		controls.setPreferredSize(new Dimension(width, 50));
		controls.setMaximumSize(new Dimension(width, 50));
		controls.setSize(new Dimension(width, 50));

		controls.add(new JSeparator(SwingConstants.VERTICAL));
		controls.add(new JLabel("Distance:"));

		distCombo = new JComboBox<String>();
		distCombo.addItem("1-Pearson");
		distCombo.addItem("Euclidean");
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

		updateImage();

		return base;
	}

	private void resetImage() {
		image.fill(RGBColour.WHITE);
	}

	private void updateImage() {
		ic.setImage(bimg = ImageUtilities.createBufferedImageForDisplay(image, bimg));
	}

	private void initMDS() {
		if (this.distCombo.getSelectedItem().equals("Euclidean"))
			this.distanceMeasure = FloatFVComparison.EUCLIDEAN;
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
					if (Double.isNaN(FloatFVComparison.CORRELATION.compare(h1, h2)))
						System.out.println("here");
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

		drawPoints(false);
		updateImage();
	}

	private void drawPoints(boolean labels) {
		this.resetImage();

		final PointList pl = new PointList(points);
		final Rectangle bounds = pl.calculateRegularBoundingBox();
		final Rectangle imageBounds = image.getBounds();
		// imageBounds.x += 100;
		// imageBounds.y += 100;
		// imageBounds.width -= 200;
		// imageBounds.height -= 200;
		System.out.println(bounds + " " + imageBounds);

		final Matrix tf = TransformUtilities.makeTransform(bounds, imageBounds);

		for (int i = 0; i < points.size(); i++) {
			final Point2dImpl p = points.get(i);
			final Point2dImpl tfp = p.transform(tf);

			final Circle c = new Circle(tfp, 2);
			renderer.drawShapeFilled(c, RGBColour.RED);

			if (labels) {
				final GeneralFontStyle<Float[]> style =
						new GeneralFontStyle<Float[]>(new GeneralFont("Arial", Font.PLAIN), renderer, false);

						style.setColour(RGBColour.BLACK);
				style.setFontSize(14);
				renderer.drawText(data.getItemNames().get(i), tfp, style);
			}
		}
	}

	double performStep(double lasterror) {
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

		for (int k = 0; k < distances.length; k++) {
			points.get(k).x -= rate * grad[k].x;
			points.get(k).y -= rate * grad[k].y;
		}

		return totalError;
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
					double lasterror = Double.MAX_VALUE;
					while (isRunning && iter++ < 1000) {
						final double thiserror = performStep(lasterror);
						System.out.println(thiserror);
						drawPoints(false);
						updateImage();

						if (thiserror >= lasterror)
							break;

						lasterror = thiserror;
					}
					drawPoints(true);
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
		new SlideshowApplication(new MDSDemo2(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
