package uk.ac.soton.ecs.comp6237.l17;

import java.io.IOException;

import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

/**
 * Demo showing mini-batch gradient descent to fit a line
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
@Demonstration(title = "Mini-Batch Stochastic Gradient Descent Demo")
public class MBSGradientDescentDemo extends AbstractGradientDescentDemo {
	int i = 0;
	int batch_size = 10;

	@Override
	protected void performIteration() {
		final double[][] Xb = new double[2][batch_size];
		for (int j = 0; j < batch_size; j++) {
			Xb[0][j] = X[0][i + j];
			Xb[1][j] = X[1][i + j];
		}

		final double[] error = errorv(Xb, params);
		final double[] delta = new double[2];
		for (int j = 0; j < Xb[0].length; j++) {
			delta[0] += Xb[0][j] * error[j];
			delta[1] += error[j];
		}
		delta[0] /= Xb[0].length;
		delta[1] /= Xb[0].length;

		params[0] = params[0] - alpha * delta[0];
		params[1] = params[1] - alpha * delta[1];

		i += batch_size;

		if (i >= X[0].length)
			i = 0;
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new MBSGradientDescentDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
