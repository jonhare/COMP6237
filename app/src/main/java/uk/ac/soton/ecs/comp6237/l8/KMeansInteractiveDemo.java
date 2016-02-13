package uk.ac.soton.ecs.comp6237.l8;

import java.io.IOException;

import javax.swing.JSplitPane;

import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.GroovyREPLConsoleSlide;
import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.Demonstration;

@Demonstration(title = "K-Means Clustering")
public class KMeansInteractiveDemo extends GroovyREPLConsoleSlide {

	public KMeansInteractiveDemo() throws IOException {
		super(JSplitPane.VERTICAL_SPLIT, Lecture8.class.getResource("kmeanscluster.groovy"),
				"result = kmeans(moduledata.transpose())", "result = kmeans(moduledata)");
	}

	public static void main(String[] args) throws IOException {
		new SlideshowApplication(new KMeansInteractiveDemo(), 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
