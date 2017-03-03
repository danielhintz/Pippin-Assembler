package myassembler;

import java.io.File;
import java.io.PrintStream;

public class CompilerException extends RuntimeException {
	private static final long serialVersionUID = 8157620765459819695L;

	private String error;
	private int lineNum;
	private File file;

	public CompilerException() {
		super();
	}

	public CompilerException(String string, File file, int lineNum) {
		super(string);
		this.error = string;
		this.file = file;
		this.lineNum = lineNum;
	}

	public void printStackTrace() {
		this.printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream s) {
		s.println("CompilerException at: (" + file.getName() + ":" + lineNum + ")");
		s.println(error);
	}

}
