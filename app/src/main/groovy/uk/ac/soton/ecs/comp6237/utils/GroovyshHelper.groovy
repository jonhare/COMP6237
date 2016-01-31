package uk.ac.soton.ecs.comp6237.utils;

import org.codehaus.groovy.tools.shell.Groovysh

public class GroovyshHelper {

	public static void executeScript(Groovysh shell, String script) {
		// Disable the result hook for profile scripts
		final def previousHook = shell.resultHook
		shell.resultHook = { result -> /* nothing */}

		try {
			script.eachLine { shell << it }
		}
		finally {
			// Restore the result hook
			shell.resultHook = previousHook
		}
	}

	public static void executeScript(Groovysh shell, List<String> scripts) {
		// Disable the result hook for profile scripts
		final def previousHook = shell.resultHook
		shell.resultHook = { result -> /* nothing */}

		try {
			scripts.each { script ->
				script.eachLine { shell << it }
			}
		}
		finally {
			// Restore the result hook
			shell.resultHook = previousHook
		}
	}
}
