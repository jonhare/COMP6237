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

@Lecture(
		title = "L14: Data Mining and Machine Learning with Big Data",
		handoutsURL = "https://github.com/jonhare/COMP6237/blob/master/notes/BigDataMining.pdf",
		slidesURL = "http://comp6237.ecs.soton.ac.uk/lectures/pdf/BigDataMining.pdf")
@JvmArgs(vmArguments = "-Xmx1G")
public class Lecture14 {
	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		for (int i = 1; i <= 5; i++)
			slides.add(new PictureSlide(Lecture14.class.getResource(String.format("%03d.jpeg", i))));

		slides.add(new KMeansDemo());

		for (int i = 6; i <= 30; i++)
			slides.add(new PictureSlide(Lecture14.class.getResource(String.format("%03d.jpeg", i))));

		slides.add(new BatchGradientDescentDemo());

		for (int i = 31; i <= 32; i++)
			slides.add(new PictureSlide(Lecture14.class.getResource(String.format("%03d.jpeg", i))));

		slides.add(new StochasticGradientDescentDemo());

		for (int i = 33; i <= 34; i++)
			slides.add(new PictureSlide(Lecture14.class.getResource(String.format("%03d.jpeg", i))));

		slides.add(new MBSGradientDescentDemo());

		for (int i = 35; i <= 39; i++)
			slides.add(new PictureSlide(Lecture14.class.getResource(String.format("%03d.jpeg", i))));

		slides.add(new DownpourGradientDescentDemo());

		for (int i = 40; i <= 42; i++)
			slides.add(new PictureSlide(Lecture14.class.getResource(String.format("%03d.jpeg", i))));

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
