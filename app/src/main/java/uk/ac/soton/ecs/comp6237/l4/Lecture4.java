package uk.ac.soton.ecs.comp6237.l4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openimaj.content.slideshow.PictureSlide;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.JvmArgs;
import uk.ac.soton.ecs.comp6237.utils.annotations.Lecture;

@Lecture(
		title = "L4: Covariance, EVD, PCA & SVD",
		handoutsURL = "https://github.com/jonhare/COMP6237/blob/master/notes/Covariance-PCA.pdf",
		slidesURL = "http://comp6237.ecs.soton.ac.uk/lectures/pdf/Covariance-PCA.pdf")
@JvmArgs(vmArguments = "-Xmx1G")
public class Lecture4 {
	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		for (int i = 1; i <= 52; i++) {
			slides.add(new PictureSlide(Lecture4.class.getResource(String.format("%03d.jpeg", i))));
		}

		slides.set(9, new CovarianceDemo());
		slides.set(27, new EigenDecompositionDemo());
		slides.set(33, new PCADemo());

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
