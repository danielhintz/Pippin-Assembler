package project;

public class DivideByZeroException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DivideByZeroException() {
		super();
	}

	public DivideByZeroException(String message) {
		super(message);
	}

}
