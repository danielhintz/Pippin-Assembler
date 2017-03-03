package project;

public class Job {
	private int startcodeIndex, codeSize, startmemoryIndex, currentIP, currentAcc;
	private States currentState;

	public int getStartcodeIndex() {
		return startcodeIndex;
	}

	public void setStartcodeIndex(int startcodeIndex) {
		this.startcodeIndex = startcodeIndex;
	}

	public int getCodeSize() {
		return codeSize;
	}

	public void setCodeSize(int codeSize) {
		this.codeSize = codeSize;
	}

	public int getStartmemoryIndex() {
		return startmemoryIndex;
	}

	public void setStartmemoryIndex(int startmemoryIndex) {
		this.startmemoryIndex = startmemoryIndex;
	}

	public int getCurrentIP() {
		return currentIP;
	}

	public void setCurrentIP(int currentIP) {
		this.currentIP = currentIP;
	}

	public int getCurrentAcc() {
		return currentAcc;
	}

	public void setCurrentAcc(int currentAcc) {
		this.currentAcc = currentAcc;
	}

	public States getCurrentState() {
		return currentState;
	}

	public void setCurrentState(States currentState) {
		this.currentState = currentState;
	}

	public void reset() {
		codeSize = currentAcc = 0;
		currentIP = startcodeIndex;
		currentState = States.NOTHING_LOADED;
	}
}
