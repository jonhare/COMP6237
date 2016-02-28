package uk.ac.soton.ecs.comp6237.utils;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

import jline.TerminalFactory;

import org.codehaus.groovy.tools.shell.AnsiDetector;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.python.util.InteractiveConsole;

public class JythonInterpreter implements Runnable {
	public InteractiveConsole shell;
	private PipedInputStream is;
	private PipedOutputStream os;
	private PipedInputStream pis;
	private PipedOutputStream pos;

	public JythonInterpreter() throws IOException {
		is = new PipedInputStream();
		os = new PipedOutputStream();
		pis = new PipedInputStream(os);
		pos = new PipedOutputStream(is);

		System.setProperty(TerminalFactory.JLINE_TERMINAL, "none");
		AnsiConsole.systemInstall();
		Ansi.setDetector(new AnsiDetector());

		shell = new InteractiveConsole();
		shell.setIn(pis);
		shell.setOut(pos);
		shell.setErr(pos);
	}

	public PipedInputStream getInputStream() {
		return is;
	}

	public PipedOutputStream getOutputStream() {
		return os;
	}

	@Override
	public void run() {
		// shell.r
		shell.interact();
	}

	public void execute(String script) {
		shell.exec(script);
	}

	public void execute(List<String> script) {
		for (final String s : script)
			shell.exec(s);
	}

}
