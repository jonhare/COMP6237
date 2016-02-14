package uk.ac.soton.ecs.comp6237.l8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openimaj.content.slideshow.PictureSlide;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.JvmArgs;
import uk.ac.soton.ecs.comp6237.utils.annotations.Lecture;

@Lecture(title = "L8: Discovering Groups",
handoutsURL = "https://github.com/jonhare/COMP6237/blob/master/notes/L8-Groups.pdf",
slidesURL = "http://comp6237.ecs.soton.ac.uk/lectures/pdf/L8-Groups.pdf")
@JvmArgs(vmArguments = "-Xmx1G")
public class Lecture8 {
	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		for (int i = 1; i <= 49; i++) {
			slides.add(new PictureSlide(Lecture8.class.getResource(String.format("l8.%03d.jpeg", i))));
		}

		slides.set(15, new HClusterDemo());
		slides.set(16, new HClusterInteractiveDemo());
		slides.set(19, new KMeansDemo());
		slides.set(20, new KMeansInteractiveDemo());
		slides.set(30, new MeanShiftDemo());

		slides.set(33, new PCASlide());
		slides.set(41, new SOMDemo());
		slides.set(47, new MDSDemo());

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
