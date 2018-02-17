package uk.ac.soton.ecs.comp6237.l5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openimaj.content.slideshow.PictureSlide;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;

import uk.ac.soton.ecs.comp6237.l3.PCASlide;
import uk.ac.soton.ecs.comp6237.utils.Utils;
import uk.ac.soton.ecs.comp6237.utils.annotations.JvmArgs;
import uk.ac.soton.ecs.comp6237.utils.annotations.Lecture;

@Lecture(
		title = "L5: Discovering Groups: Visualising and Embedding",
		handoutsURL = "https://github.com/jonhare/COMP6237/blob/master/notes/Groups.pdf",
		slidesURL = "http://comp6237.ecs.soton.ac.uk/lectures/pdf/Groups.pdf")
@JvmArgs(vmArguments = "-Xmx1G")
public class Lecture5 {
	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		for (int i = 1; i <= 36; i++) {
			slides.add(new PictureSlide(Lecture5.class.getResource(String.format("%03d.jpeg", i))));
		}

		slides.set(5, new PCASlide());
		slides.set(13, new SOMDemo());
		slides.set(18, new MDSDemo());
		slides.set(26, new TSNEDemo());
		slides.set(34, new Word2VecDemo());

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
