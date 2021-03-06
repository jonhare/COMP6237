package uk.ac.soton.ecs.comp6237.l3;

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
		title = "L3: Discovering Groups: Clustering Data",
		handoutsURL = "https://github.com/jonhare/COMP6237/blob/master/notes/Groups.pdf",
		slidesURL = "http://comp6237.ecs.soton.ac.uk/lectures/pdf/Groups.pdf")
@JvmArgs(vmArguments = "-Xmx1G")
public class Lecture3 {
	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		for (int i = 1; i <= 32; i++) {
			slides.add(new PictureSlide(Lecture3.class.getResource(String.format("%03d.jpeg", i))));
		}

		slides.set(15, new HClusterDemo());
		slides.set(16, new HClusterInteractiveDemo());
		slides.set(19, new KMeansDemo());
		slides.set(20, new KMeansInteractiveDemo());
		slides.set(30, new MeanShiftDemo());

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
