package uk.ac.soton.ecs.comp6237.l3;

import gnu.trove.procedure.TIntObjectProcedure;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.DisplayUtilities.ImageComponent;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourMap;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.renderer.MBFImageRenderer;
import org.openimaj.image.renderer.RenderHints;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.statistics.distribution.MultivariateKernelDensityEstimate;
import org.openimaj.math.statistics.distribution.kernel.StandardUnivariateKernels;
import org.openimaj.util.pair.ObjectDoublePair;
import org.openimaj.util.set.DisjointSetForest;
import org.openimaj.util.tree.DoubleKDTree;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

/**
 * Demo showing Mean Shift clustering
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
@Demonstration(title = "Mean Shift Clustering Demo")
public class MeanShiftDemo extends MouseAdapter implements Slide, ActionListener {

	private MBFImage image;
	private ImageComponent ic;
	private BufferedImage bimg;
	private List<Point2d> points = new ArrayList<Point2d>();
	private JButton runBtn;
	private JButton clearBtn;
	private JButton cnclBtn;
	private volatile boolean isRunning;
	private MBFImageRenderer renderer;

	private int maxIter = 300;

	private MultivariateKernelDensityEstimate kde;
	private int[] assignments;

	private double[][] modes;
	private int[] counts;
	private Float[][] colours;
	private JSpinner hSpn;

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
		ic.addMouseListener(this);
		ic.addMouseMotionListener(this);
		base.add(ic);

		final JPanel controls = new JPanel();
		controls.setPreferredSize(new Dimension(width, 50));
		controls.setMaximumSize(new Dimension(width, 50));
		controls.setSize(new Dimension(width, 50));

		clearBtn = new JButton("Clear");
		clearBtn.setActionCommand("button.clear");
		clearBtn.addActionListener(this);
		controls.add(clearBtn);

		controls.add(new JSeparator(SwingConstants.VERTICAL));

		controls.add(new JLabel("H:"));
		hSpn = new JSpinner(new SpinnerNumberModel(30, 1, 100, 5));
		controls.add(hSpn);

		controls.add(new JSeparator(SwingConstants.VERTICAL));

		runBtn = new JButton("Run Mean Shift");
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

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!isRunning) {
			final Point pt = e.getPoint();
			final Point2dImpl pti = new Point2dImpl(pt.x, pt.y);
			image.drawPoint(pti, RGBColour.MAGENTA, 20);
			points.add(pti);
			updateImage();
		}
	}

	private void resetImage() {
		image.fill(RGBColour.WHITE);
		points.clear();
		assignments = null;
	}

	private void updateImage() {
		ic.setImage(bimg = ImageUtilities.createBufferedImageForDisplay(image, bimg));
	}

	private void initMeanShift() {
		this.assignments = new int[this.points.size()];

		final double[][] data = new double[points.size()][2];
		for (int i = 0; i < points.size(); i++) {
			data[i][0] = points.get(i).getX();
			data[i][1] = points.get(i).getY();
		}

		this.kde = new MultivariateKernelDensityEstimate(data, StandardUnivariateKernels.Gaussian,
				(Integer) hSpn.getValue());

		drawCentroidsImage(false);
		updateImage();
	}

	protected boolean computeMeanShift(double[] pt) {
		final List<ObjectDoublePair<double[]>> support = kde.getSupport(pt);

		if (support.size() == 1) {
			return true;
		}

		double sum = 0;
		final double[] out = new double[pt.length];
		for (final ObjectDoublePair<double[]> p : support) {
			sum += p.second;

			for (int j = 0; j < out.length; j++) {
				out[j] += p.second * p.first[j];
			}
		}

		double dist = 0;
		for (int j = 0; j < out.length; j++) {
			out[j] /= sum;
			dist += (pt[j] - out[j]) * (pt[j] - out[j]);
		}

		System.arraycopy(out, 0, pt, 0, out.length);

		return dist < 1e-3 * kde.getBandwidth();
	}

	@Override
	public void close() {
		isRunning = false;
	}

	private void drawCentroidsImage(boolean colorPoints) {
		image.fill(RGBColour.WHITE);

		for (int i = 0; i < points.size(); i++)
			image.drawPoint(points.get(i), colorPoints ? colours[assignments[i]] : RGBColour.MAGENTA, 10);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("button.clear")) {
			resetImage();
			updateImage();
		} else if (e.getActionCommand().equals("button.run")) {
			runBtn.setEnabled(false);
			clearBtn.setEnabled(false);
			cnclBtn.setEnabled(true);
			isRunning = true;

			new Thread(new Runnable() {
				@Override
				public void run() {
					if (isRunning)
						initMeanShift();

					final double[][] data = kde.getData();
					final double[][] modePerPoint = new double[data.length][];
					for (int i = 0; i < points.size(); i++) {
						final double[] point = data[i].clone();

						for (int iter = 0; iter < maxIter; iter++) {
							final double[] oldPoint = point.clone();
							final boolean conv = computeMeanShift(point);

							renderer.drawLine((int) oldPoint[0], (int) oldPoint[1], (int) point[0], (int) point[1], 3,
									RGBColour.BLUE);
							updateImage();

							if (conv)
								break;

							if (!isRunning)
								break;
						}
						modePerPoint[i] = point;

						if (!isRunning)
							break;
					}

					if (isRunning) {
						// now need to merge modes that are <bandwidth away
						mergeModes(modePerPoint);
						drawCentroidsImage(true);
						updateImage();
					}

					runBtn.setEnabled(true);
					clearBtn.setEnabled(true);
					cnclBtn.setEnabled(false);
					isRunning = false;
				}
			}).start();
		} else if (e.getActionCommand().equals("button.cancel")) {
			isRunning = false;
			cnclBtn.setEnabled(false);
		}
	}

	protected void mergeModes(double[][] modePerPoint) {
		final DisjointSetForest<double[]> forest = new DisjointSetForest<double[]>();

		for (int i = 0; i < modePerPoint.length; i++)
			forest.makeSet(modePerPoint[i]);

		final DoubleKDTree tree = new DoubleKDTree(modePerPoint);
		for (int i = 0; i < modePerPoint.length; i++) {
			final double[] point = modePerPoint[i];

			tree.radiusSearch(modePerPoint[i], kde.getScaledBandwidth(), new TIntObjectProcedure<double[]>() {
				@Override
				public boolean execute(int a, double[] b) {
					forest.union(point, b);
					return true;
				}
			});
		}

		final Set<Set<double[]>> subsets = forest.getSubsets();
		this.assignments = new int[modePerPoint.length];
		this.modes = new double[subsets.size()][];
		this.counts = new int[subsets.size()];
		int current = 0;
		for (final Set<double[]> s : subsets) {
			this.modes[current] = new double[modePerPoint[0].length];

			for (int i = 0; i < modePerPoint.length; i++) {
				if (s.contains(modePerPoint[i])) {
					assignments[i] = current;
					for (int j = 0; j < modes[current].length; j++) {
						modes[current][j] = modePerPoint[i][j];
					}
				}
			}
			this.counts[current] = s.size();
			for (int j = 0; j < modes[current].length; j++) {
				modes[current][j] /= counts[current];
			}
			current++;
		}

		this.colours = RGBColour.coloursFromMap(ColourMap.HSV, current);
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new MeanShiftDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
