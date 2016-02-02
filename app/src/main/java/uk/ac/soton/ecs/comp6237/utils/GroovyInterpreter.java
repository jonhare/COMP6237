package uk.ac.soton.ecs.comp6237.utils;

import groovy.lang.Binding;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

import jline.TerminalFactory;

import org.codehaus.groovy.tools.shell.AnsiDetector;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.IO;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class GroovyInterpreter implements Runnable {
	Binding binding;
	private Groovysh shell;
	private PipedInputStream is;
	private PipedOutputStream os;
	private PipedInputStream pis;
	private PipedOutputStream pos;

	public GroovyInterpreter(Binding binding) throws IOException {
		this.binding = binding;
		if (this.binding == null)
			this.binding = new Binding();

		is = new PipedInputStream();
		os = new PipedOutputStream();
		pis = new PipedInputStream(os);
		pos = new PipedOutputStream(is);

		System.setProperty(TerminalFactory.JLINE_TERMINAL, "none");
		AnsiConsole.systemInstall();
		Ansi.setDetector(new AnsiDetector());

		binding.setProperty("out", new ImmediateFlushingPrintWriter(pos));

		shell = new Groovysh(binding, new IO(pis, pos, pos));
		// {
		// @Override
		// public void displayWelcomeBanner(InteractiveShellRunner runner) {
		// // do nothing
		// }
		// };
		// shell.getIo().setVerbosity(Verbosity.QUIET);
	}

	public PipedInputStream getInputStream() {
		return is;
	}

	public PipedOutputStream getOutputStream() {
		return os;
	}

	@Override
	public void run() {
		try {
			shell.run("");
		} catch (final Throwable t) {
			System.out.println(t);
		}
	}

	public void execute(String script) {
		GroovyshHelper.executeScript(shell, script);
	}

	public void execute(List<String> script) {
		GroovyshHelper.executeScript(shell, script);
	}

}
