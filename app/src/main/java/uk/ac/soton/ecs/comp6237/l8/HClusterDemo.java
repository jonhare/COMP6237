package uk.ac.soton.ecs.comp6237.l8;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import org.openimaj.image.typography.FontStyle.HorizontalAlignment;
import org.openimaj.image.typography.FontStyle.VerticalAlignment;
import org.openimaj.image.typography.general.GeneralFont;
import org.openimaj.image.typography.general.GeneralFontStyle;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Circle;
import org.openimaj.math.geometry.shape.Rectangle;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

/**
 * Demo showing Hierarchical Agglomerative Clustering with WPGMC
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
@Demonstration(title = "Hierarchical Agglomerative Clustering Demo")
public class HClusterDemo extends MouseAdapter implements Slide, ActionListener {
	enum Linkage {
		WPGMC {
			@Override
			public double computeDistance(BiCluster a, BiCluster b, FloatFVComparator distanceMeasure) {
				return distanceMeasure.compare(a.vec, b.vec);
			}

			@Override
			public float[] computeVec(BiCluster left, BiCluster right) {
				final float[] mergevec = new float[2];
				for (int i = 0; i < mergevec.length; i++)
					mergevec[i] = (left.vec[i] + right.vec[i]) / 2;
				return mergevec;
			}
		},
		UPGMC {
			@Override
			public double computeDistance(BiCluster a, BiCluster b, FloatFVComparator distanceMeasure) {
				return distanceMeasure.compare(a.vec, b.vec);
			}

			@Override
			public float[] computeVec(BiCluster left, BiCluster right) {
				final List<float[]> leaves = new ArrayList<>();
				leaves.addAll(left.getAllLeaves());
				leaves.addAll(right.getAllLeaves());

				final float[] mean = new float[2];
				for (final float[] v : leaves) {
					mean[0] += v[0];
					mean[1] += v[1];
				}
				mean[0] /= leaves.size();
				mean[1] /= leaves.size();

				return mean;
			}
		},
		Minimum {
			@Override
			public double computeDistance(BiCluster a, BiCluster b, FloatFVComparator distanceMeasure) {
				final List<float[]> leavesA = a.getAllLeaves();
				final List<float[]> leavesB = b.getAllLeaves();

				double min = Double.MAX_VALUE;
				for (final float[] la : leavesA) {
					for (final float[] lb : leavesB) {
						final double d = distanceMeasure.compare(la, lb);
						if (d < min)
							min = d;
					}
				}

				return min;
			}
		},
		Maximum {
			@Override
			public double computeDistance(BiCluster a, BiCluster b, FloatFVComparator distanceMeasure) {
				final List<float[]> leavesA = a.getAllLeaves();
				final List<float[]> leavesB = b.getAllLeaves();

				double max = -Double.MAX_VALUE;
				for (final float[] la : leavesA) {
					for (final float[] lb : leavesB) {
						final double d = distanceMeasure.compare(la, lb);
						if (d > max)
							max = d;
					}
				}

				return max;
			}
		},
		UPGMA {
			@Override
			public double computeDistance(BiCluster a, BiCluster b, FloatFVComparator distanceMeasure) {
				final List<float[]> leavesA = a.getAllLeaves();
				final List<float[]> leavesB = b.getAllLeaves();

				double sum = 0;
				for (final float[] la : leavesA) {
					for (final float[] lb : leavesB) {
						sum += distanceMeasure.compare(la, lb);
					}
				}

				return sum / (leavesA.size() * leavesB.size());
			}
		},
		;

		public float[] computeVec(BiCluster left, BiCluster right) {
			return null;
		}

		public abstract double computeDistance(BiCluster a, BiCluster b, FloatFVComparator distanceMeasure);
	}

	class BiCluster {
		BiCluster left;
		BiCluster right;
		float[] vec;
		double distance;
		char label;

		List<float[]> getAllLeaves() {
			final List<float[]> leaves = new ArrayList<>();
			getAllLeaves(leaves);
			return leaves;
		}

		private void getAllLeaves(List<float[]> leaves) {
			if (left != null) {
				left.getAllLeaves(leaves);
				right.getAllLeaves(leaves);
			} else {
				leaves.add(vec);
			}
		}
	}

	private MBFImage image;
	private ImageComponent ic;
	private BufferedImage bimg;
	private List<Point2d> points = new ArrayList<Point2d>();
	private JButton runBtn;
	private JButton clearBtn;
	private JButton cnclBtn;
	private volatile boolean isRunning;
	private MBFImageRenderer renderer;
	private FloatFVComparator distanceMeasure = FloatFVComparison.EUCLIDEAN;
	private JComboBox<String> distCombo;
	List<BiCluster> clusters = new ArrayList<BiCluster>();
	Linkage linkage = Linkage.WPGMC;
	private JComboBox<String> linkCombo;

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
		controls.add(new JLabel("Distance:"));

		distCombo = new JComboBox<String>();
		distCombo.addItem("Euclidean");
		distCombo.addItem("Manhatten");
		distCombo.addItem("Cosine Distance");
		controls.add(distCombo);

		controls.add(new JSeparator(SwingConstants.VERTICAL));
		controls.add(new JLabel("Linkage:"));

		linkCombo = new JComboBox<String>();
		for (final Linkage s : Linkage.values())
			linkCombo.addItem(s.name());
		controls.add(linkCombo);

		controls.add(new JSeparator(SwingConstants.VERTICAL));

		runBtn = new JButton("Run HAC");
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
		if (!isRunning && points.size() <= 25) {
			final Point pt = e.getPoint();
			final Point2dImpl pti = new Point2dImpl(pt.x, pt.y);

			drawPoint(pti, points.size());
			points.add(pti);
			updateImage();
		}
	}

	private void drawPoint(final Point2dImpl pti, int index) {
		renderer.drawShapeFilled(new Circle(pti, 20), RGBColour.MAGENTA);
		final char c = (char) (65 + index);
		final GeneralFontStyle<Float[]> style = new GeneralFontStyle<Float[]>(new GeneralFont("Arial", Font.BOLD),
				renderer, false);
		style.setHorizontalAlignment(HorizontalAlignment.HORIZONTAL_CENTER);
		renderer.drawText(c + "", (int) (pti.x), (int) (pti.y + 10), style);
	}

	private void resetImage() {
		image.fill(RGBColour.WHITE);
		points.clear();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// ignore
	}

	private void updateImage() {
		ic.setImage(bimg = ImageUtilities.createBufferedImageForDisplay(image, bimg));
	}

	private void initHAC() {
		if (this.distCombo.getSelectedItem().equals("Euclidean"))
			this.distanceMeasure = FloatFVComparison.EUCLIDEAN;
		else if (this.distCombo.getSelectedItem().equals("Manhatten"))
			this.distanceMeasure = FloatFVComparison.CITY_BLOCK;
		else if (this.distCombo.getSelectedItem().equals("Cosine Distance"))
			this.distanceMeasure = FloatFVComparison.COSINE_DIST;

		final String link = (String) this.linkCombo.getSelectedItem();
		linkage = Linkage.valueOf(link);

		clusters.clear();
		image.fill(RGBColour.WHITE);
		int i = 0;
		for (final Point2d p : points) {
			final BiCluster c = new BiCluster();
			c.vec = toFloatArray(p, new float[2]);
			c.label = (char) (65 + i);
			clusters.add(c);
			drawPoint((Point2dImpl) p, i++);
		}
		updateImage();
	}

	/**
	 * Merge the two closest items
	 */
	private void mergeStep() {
		final int[] lowestpair = { 0, 1 };

		double closest = Double.MAX_VALUE;

		for (int i = 0; i < clusters.size(); i++) {
			for (int j = i + 1; j < clusters.size(); j++) {
				final double d = linkage.computeDistance(clusters.get(i), clusters.get(j), distanceMeasure);

				if (d < closest) {
					closest = d;
					lowestpair[0] = i;
					lowestpair[1] = j;
				}
			}
		}

		final float[] mergevec = linkage.computeVec(clusters.get(lowestpair[0]), clusters.get(lowestpair[1]));

		final BiCluster newcluster = new BiCluster();
		newcluster.vec = mergevec;
		newcluster.left = clusters.get(lowestpair[0]);
		newcluster.right = clusters.get(lowestpair[1]);
		newcluster.distance = closest;

		final int x = Math.min(computeLeftBound(newcluster.left), computeLeftBound(newcluster.right)) - 30;
		final int y = Math.min(computeTopBound(newcluster.left), computeTopBound(newcluster.right)) - 30;
		final int x1 = Math.max(computeRightBound(newcluster.left), computeRightBound(newcluster.right)) + 30;
		final int y1 = Math.max(computeBottomBound(newcluster.left), computeBottomBound(newcluster.right)) + 30;
		final Rectangle r = new Rectangle(x, y, x1 - x, y1 - y);
		renderer.drawShape(r, 3, RGBColour.RED);

		if (linkage == Linkage.UPGMC || linkage == Linkage.WPGMC) {
			image.drawLine((int) newcluster.left.vec[0], (int)
					newcluster.left.vec[1], (int) newcluster.right.vec[0],
					(int) newcluster.right.vec[1], 1, RGBColour.BLUE);
			renderer.drawPoint(new Point2dImpl(newcluster.vec[0], newcluster.vec[1]), RGBColour.GREEN, 5);
		}
		updateImage();

		clusters.remove(lowestpair[1]);
		clusters.remove(lowestpair[0]);
		clusters.add(newcluster);
	}

	int computeLeftBound(BiCluster a) {
		if (a.left == null)
			return (int) a.vec[0];
		else
			return Math.min(computeLeftBound(a.left), computeLeftBound(a.right)) - 5;
	}

	int computeRightBound(BiCluster a) {
		if (a.left == null)
			return (int) a.vec[0];
		else
			return Math.max(computeRightBound(a.left), computeRightBound(a.right)) + 5;
	}

	int computeTopBound(BiCluster a) {
		if (a.left == null)
			return (int) a.vec[1];
		else
			return Math.min(computeTopBound(a.left), computeTopBound(a.right)) - 5;
	}

	int computeBottomBound(BiCluster a) {
		if (a.left == null)
			return (int) a.vec[1];
		else
			return Math.max(computeBottomBound(a.left), computeBottomBound(a.right)) + 5;
	}

	private float[] toFloatArray(Point2d pt, float[] arr) {
		arr[0] = pt.getX();
		arr[1] = pt.getY();
		return arr;
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
			clearBtn.setEnabled(false);
			cnclBtn.setEnabled(true);
			isRunning = true;

			new Thread(new Runnable() {
				@Override
				public void run() {
					if (isRunning) {
						initHAC();
						try {
							Thread.sleep(500);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}

					while (clusters.size() > 1) {
						if (isRunning) {
							mergeStep();
							try {
								Thread.sleep(500);
							} catch (final InterruptedException e) {
								e.printStackTrace();
							}
						} else
							break;
					}

					drawDendrogram();
					updateImage();

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

	private int getHeight(BiCluster clust) {
		// Is this an endpoint? Then the height is just 1
		if (clust.left == null && clust.right == null)
			return 1;

		// Otherwise the height is the same of the heights of each branch
		return getHeight(clust.left) + getHeight(clust.right);
	}

	private float getDepth(BiCluster clust) {
		// The distance of an endpoint is 0.0
		if (clust.left == null && clust.right == null)
			return 0;

		// The distance of a branch is the greater of its two sides plus its own
		// distance
		return (float) (Math.max(getDepth(clust.left), getDepth(clust.right)) + clust.distance);
	}

	void drawDendrogram() {
		final int w = 300;
		final BiCluster root = this.clusters.get(0);
		// height and width
		final int h = getHeight(root) * 20;
		final float depth = getDepth(root);

		// width is fixed, so scale distances accordingly
		final float scaling = (w - 150.0F) / depth;
		final int x0 = renderer.getImage().getWidth() - w;

		renderer.drawLine(x0, 50 + (h / 2), x0 + 10, 50 + (h / 2), 3, RGBColour.RED);
		drawnode(root, x0 + 10, 50 + (h / 2), scaling);
	}

	void drawnode(BiCluster clust, int x, int y, float scaling) {
		if (clust.left != null) {
			final float h1 = getHeight(clust.left) * 20;
			final float h2 = getHeight(clust.right) * 20;
			final float top = y - (h1 + h2) / 2;
			final float bottom = y + (h1 + h2) / 2;

			// Line length
			final int ll = (int) (clust.distance * scaling);

			// Vertical line from this cluster to children
			renderer.drawLine(x, (int) (top + h1 / 2), x, (int) (bottom - h2 / 2), 3, RGBColour.RED);

			// Horizontal line to left item
			renderer.drawLine(x, (int) (top + h1 / 2), x + ll, (int) (top + h1 / 2), 3, RGBColour.RED);

			// Horizontal line to right item
			renderer.drawLine(x, (int) (bottom - h2 / 2), x + ll, (int) (bottom - h2 / 2), 3, RGBColour.RED);

			// Call the function to draw the left and right nodes
			drawnode(clust.left, x + ll, (int) (top + h1 / 2), scaling);
			drawnode(clust.right, x + ll, (int) (bottom - h2 / 2), scaling);
		} else {
			// If this is an endpoint, draw the item label
			final GeneralFontStyle<Float[]> style = new GeneralFontStyle<Float[]>(new GeneralFont("Arial", Font.PLAIN),
					renderer, false);
			style.setColour(RGBColour.RED);
			style.setVerticalAlignment(VerticalAlignment.VERTICAL_HALF);
			renderer.drawText(clust.label + "", x + 5, y + 7, style);

		}
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new HClusterDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
