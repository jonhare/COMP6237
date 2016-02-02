package uk.ac.soton.ecs.comp6237.l6;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openimaj.content.slideshow.PictureSlide;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.JvmArgs;
import uk.ac.soton.ecs.comp6237.utils.annotations.Lecture;

@Lecture(title = "L6: Making Recommendations",
		handoutsURL = "https://github.com/jonhare/COMP6237/blob/master/notes/L6-Recommendation.pdf",
		slidesURL = "http://comp6237.ecs.soton.ac.uk/lectures/pdf/L6-Recommendation.pdf")
@JvmArgs(vmArguments = "-Xmx1G")
public class Lecture6 {
	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		for (int i = 1; i <= 56; i++) {
			slides.add(new PictureSlide(Lecture6.class.getResource(String.format("l6.%03d.jpeg", i))));
		}

		slides.set(30, new ItemSpaceSlide());
		slides.set(33, new UserSpaceSlide());

		slides.set(36, new UserUserSimDemo()); // user-user sim
		slides.set(39, new UserBasedRecDemo()); // user based rec
		slides.set(42, new ItemItemSimDemo()); // item-item sim

		slides.set(45, new ItemItemPrecompDemo()); // item-item precomp
		slides.set(47, new ItemBasedRecDemo()); // item-based rec

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
