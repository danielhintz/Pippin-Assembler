package project;

public class Memory {
	public static final int DATA_SIZE = 2048;

	private int[] data = new int[2048];

	private int changedIndex = -1;

	public int getData(int i) {
		return data[i];
	}

	public void setData(int index, int val) {
		data[index] = val;
		changedIndex = index;
	}

	int[] getData() {
		return data;
	}

	public void clear(int start, int end) {
		for (int i = start; i < end; i++) {
			data[i] = 0;
		}
		changedIndex = -1;
	}

	public int getChangedIndex() {
		return changedIndex;
	}
}
