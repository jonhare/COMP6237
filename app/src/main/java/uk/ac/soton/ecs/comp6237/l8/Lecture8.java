package uk.ac.soton.ecs.comp6237.l8;

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

@Lecture(
		title = "L8: Modelling with Decision Trees",
		handoutsURL = "https://github.com/jonhare/COMP6237/blob/master/notes/DecisionTrees.pdf",
		slidesURL = "http://comp6237.ecs.soton.ac.uk/lectures/pdf/DecisionTrees.pdf")
@JvmArgs(vmArguments = "-Xmx1G")
public class Lecture8 {
	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		for (int i = 1; i <= 41; i++) {
			slides.add(new PictureSlide(Lecture8.class.getResource(String.format("%03d.jpeg", i))));
		}

		slides.set(25, new JythonREPLConsoleSlide(JSplitPane.VERTICAL_SPLIT,
				Lecture8.class.getResource("tree.py"), "printtree(buildtree(data))"));// building
		slides.set(27, new JythonREPLConsoleSlide(JSplitPane.VERTICAL_SPLIT,
				Lecture8.class.getResource("tree.py"), "classify(['(direct)', 'USA', 'yes', 5], tree)",
				"classify(['google', 'USA', 'yes', 50], tree)"));// classification

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
