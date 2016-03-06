package uk.ac.soton.ecs.comp6237.l14;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openimaj.content.slideshow.PictureSlide;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.JvmArgs;
import uk.ac.soton.ecs.comp6237.utils.annotations.Lecture;

@Lecture(title = "L14: Semantic Spaces",
		handoutsURL = "https://github.com/jonhare/COMP6237/blob/master/notes/L14-SemanticSpaces.pdf",
		slidesURL = "http://comp6237.ecs.soton.ac.uk/lectures/pdf/L14-SemanticSpaces.pdf")
@JvmArgs(vmArguments = "-Xmx1G")
public class Lecture14 {
	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		for (int i = 1; i <= 36; i++) {
			slides.add(new PictureSlide(Lecture14.class.getResource(String.format("l14.%03d.jpeg", i))));
		}

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
