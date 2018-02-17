package uk.ac.soton.ecs.comp6237.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.codehaus.groovy.tools.shell.IO.Verbosity;
import org.openimaj.content.slideshow.Slide;
import org.openimaj.content.slideshow.SlideshowApplication;

import groovy.lang.Binding;

public class GroovyREPLSlide implements Slide {
	protected Binding binding;
	protected GroovyInterpreter interpreter;
	private String[] initialCommands;

	public GroovyREPLSlide(String... initialCommands) {
		this.initialCommands = initialCommands;
	}

	@Override
	public Component getComponent(int width, int height) throws IOException {
		final JPanel base = new JPanel();
		base.setOpaque(false);
		base.setPreferredSize(new Dimension(width, height));
		base.setLayout(new BorderLayout());

		// border all the way around to make it possible to get to the
		// next/previous slide
		base.add(new JPanel(), BorderLayout.NORTH);
		base.add(new JPanel(), BorderLayout.EAST);
		base.add(new JPanel(), BorderLayout.WEST);
		base.add(new JPanel(), BorderLayout.SOUTH);

		binding = new Binding();
		interpreter = new GroovyInterpreter(binding);

		final JConsole console = new JConsole(interpreter.getInputStream(), interpreter.getOutputStream());
		base.add(console, BorderLayout.CENTER);

		new Thread(interpreter).start();

		interpreter.shell.getIo().setVerbosity(Verbosity.INFO);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// bootstrap history
				for (int i = 0; i < initialCommands.length; i++)
					console.history.add(initialCommands[i]);
				if (initialCommands.length > 0)
					console.historyUp();
			}
		});

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
