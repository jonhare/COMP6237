package uk.ac.soton.ecs.comp6237.utils;

import groovy.lang.Binding;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;

public class GroovyREPLSlide implements Slide {
	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel base = new JPanel();
		base.setOpaque(false);
		base.setPreferredSize(new Dimension(width, height));
		base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));

		final Binding binding = new Binding();
		final GroovyInterpreter interpreter = new GroovyInterpreter(binding);

		final JConsole console = new JConsole(interpreter.getInputStream(), interpreter.getOutputStream());
		base.add(console);

		new Thread(interpreter).start();

		return base;
	}

	@Override
	public void close() {

	}

	public static void main(String[] args) throws IOException {
		final List<Slide> slides = new ArrayList<Slide>();

		slides.add(new GroovyREPLSlide());

		new SlideshowApplication(slides, 1024, 768, Utils.BACKGROUND_IMAGE);
	}
}
