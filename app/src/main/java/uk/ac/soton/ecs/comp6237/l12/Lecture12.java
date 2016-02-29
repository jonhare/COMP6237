package uk.ac.soton.ecs.comp6237.l12;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JSplitPane;

import org.openimaj.content.slideshow.PictureSlide;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.JythonREPLConsoleSlide;
import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.JvmArgs;
import uk.ac.soton.ecs.comp6237.utils.annotations.Lecture;

@Lecture(title = "L12: Modelling Prices & Nearest Neighbours",
		handoutsURL = "https://github.com/jonhare/COMP6237/blob/master/notes/L12-KNN.pdf",
		slidesURL = "http://comp6237.ecs.soton.ac.uk/lectures/pdf/L12-KNN.pdf")
@JvmArgs(vmArguments = "-Xmx1G")
public class Lecture12 {
	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		for (int i = 1; i <= 46; i++) {
			slides.add(new PictureSlide(Lecture12.class.getResource(String.format("l12.%03d.jpeg", i))));
		}

		slides.set(6, new JythonREPLConsoleSlide(JSplitPane.VERTICAL_SPLIT,
				Lecture12.class.getResource("numpredict.py"), "knnestimate(data, (99.0,5.0), k=1)"));
		slides.set(15, new JythonREPLConsoleSlide(JSplitPane.VERTICAL_SPLIT,
				Lecture12.class.getResource("numpredict.py"), "weightedknn(data, (99.0,5.0), k=5, weightf=gaussian)"));

		slides.set(20, new JythonREPLConsoleSlide(JSplitPane.VERTICAL_SPLIT,
				Lecture12.class.getResource("optimization.py"), "annealingoptimize(weightdomain, costf, step=2)"));

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
