package uk.ac.soton.ecs.comp6237.l17;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

/**
 * Demo showing batch gradient descent to fit a line
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
@Demonstration(title = "Downpour Gradient Descent Demo")
public class DownpourGradientDescentDemo extends AbstractGradientDescentDemo {
	class Worker implements Runnable {
		int max_iters = 50;
		double[][] Xb;

		@Override
		public void run()
		{
			for (int i = 0; i < max_iters; i++) {
				final double[] params = getParams();
				final double[] error = errorv(Xb, params);
				final double[] delta = new double[2];
				for (int j = 0; j < Xb[0].length; j++) {
					delta[0] += Xb[0][j] * error[j];
					delta[1] += error[j];
				}
				delta[0] /= Xb[0].length;
				delta[1] /= Xb[0].length;

				descent(delta);
			}
		}
	}

	int num_threads = 10;

	void descent(double[] delta) {
		synchronized (this) {
			iter++;
			params[0] = params[0] - alpha * delta[0];
			params[1] = params[1] - alpha * delta[1];

			this.updateDisplay();
		}
	}

	double[] getParams() {
		return params.clone();
	}

	@Override
	public void run() {
		final List<Thread> threads = new ArrayList<Thread>();
		final int batch_size = X[0].length / num_threads;

		for (int k = 0; k < num_threads; k++) {
			final Worker worker = new Worker();
			worker.Xb = new double[2][batch_size];
			for (int j = 0; j < batch_size; j++) {
				worker.Xb[0][j] = X[0][k * batch_size + j];
				worker.Xb[1][j] = X[1][k * batch_size + j];
			}

			threads.add(new Thread(worker));
		}

		for (int k = 0; k < num_threads; k++) {
			threads.get(k).start();
		}

		for (int k = 0; k < num_threads; k++) {
			try {
				threads.get(k).join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new DownpourGradientDescentDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
