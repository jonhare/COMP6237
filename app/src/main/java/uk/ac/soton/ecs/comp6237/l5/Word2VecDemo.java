package uk.ac.soton.ecs.comp6237.l5;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.runtime.MethodClosure;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.openimaj.content.slideshow.SlideshowApplication;
import org.openimaj.data.DataUtils;

import uk.ac.soton.ecs.comp6237.utils.GroovyREPLSlide;
import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

@Demonstration(title = "Word2Vec Demo")
public class Word2VecDemo extends GroovyREPLSlide {
	static {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final File dataset = DataUtils.getDataLocation("GoogleNews-vectors-negative300-SLIM.bin.gz");

					if (!(dataset.exists())) {
						dataset.getParentFile().mkdirs();
						FileUtils.copyURLToFile(
								new URL("https://artist-cloud.ecs.soton.ac.uk/s/pTHe9m5PxZubnWB/download"),
								dataset);
						System.err.println("downloaded data");
					}

					vec = WordVectorSerializer.readWord2VecModel(dataset);
					System.err.println("loaded model");
				} catch (final IOException e) {
					System.err.println(e);
					e.printStackTrace();
					vec = null;
				}
			}
		}).start();
	}
	volatile static Word2Vec vec;

	public Word2VecDemo() {
		super("v2w(w2v(\"brother\")-w2v(\"man\")+w2v(\"woman\"))", "v2w(w2v(\"king\")-w2v(\"queen\")+w2v(\"woman\"))",
				"v2w(w2v(\"Paris\")-w2v(\"France\")+w2v(\"Britain\"))", "");
	}

	public MyNDArray w2v(String word) {
		checkVec();
		return new MyNDArray(vec.getWordVectorMatrix(word));
	}

	private void checkVec() {
		if (vec == null) {
			this.interpreter.shell.getIo().out.println("Data is still loading. Please wait a moment.");
			while (vec == null) {
				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
				}
			}
			this.interpreter.shell.getIo().out.println("Loaded.");
		}
	}

	public String v2w(MyNDArray vector) {
		checkVec();
		return vec.wordsNearest(vector.inner, 1).iterator().next();
	}

	public Collection<String> v2w(MyNDArray vector, int n) {
		checkVec();
		return vec.wordsNearest(vector.inner, n);
	}

	public Collection<String> nn(String word, int n) {
		checkVec();
		return vec.wordsNearest(word, n);
	}

	public static String help() {
		return "w2v(string) -> convert word to vector\n"
				+ "v2w(vector) -> convert vector to nearest word\n"
				+ "v2w(vector, number) -> convert vector to given number of nearest words\n"
				+ "nn(word, number) -> convert word to given number of nearest words\n";
	}

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final Component comp = super.getComponent(width, height);
		binding.setVariable("w2v", new MethodClosure(this, "w2v"));
		binding.setVariable("v2w", new MethodClosure(this, "v2w"));
		binding.setVariable("nn", new MethodClosure(this, "nn"));
		binding.setVariable("help", new MethodClosure(this, "help"));

		return comp;
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new Word2VecDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
