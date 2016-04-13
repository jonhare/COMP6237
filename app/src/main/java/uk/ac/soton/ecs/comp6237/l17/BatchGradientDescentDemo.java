package uk.ac.soton.ecs.comp6237.l17;

import java.io.IOException;

import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

/**
 * Demo showing batch gradient descent to fit a line
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
@Demonstration(title = "Batch Gradient Descent Demo")
public class BatchGradientDescentDemo extends AbstractGradientDescentDemo {
	@Override
	protected void performIteration() {
		final double[] error = errorv(X, params);
		final double[] delta = new double[2];
		for (int j = 0; j < X[0].length; j++) {
			delta[0] += X[0][j] * error[j];
			delta[1] += error[j];
		}
		delta[0] /= X[0].length;
		delta[1] /= X[0].length;

		params[0] = params[0] - alpha * delta[0];
		params[1] = params[1] - alpha * delta[1];
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new BatchGradientDescentDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
