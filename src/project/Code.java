package project;

public class Code {
	public static final int CODE_MAX = 2048;
	public int[] code = new int[CODE_MAX];

	public int getOpCode(int i) {
		return code[2 * i];
	}

	public int getArg(int i) {
		return code[2 * i + 1];
	}

	public String getHex(int i) {
		return Integer.toHexString(code[2 * i]).toUpperCase() + " " + Integer.toHexString(code[2 * i + 1]).toUpperCase();
	}

	public String getDecimal(int i) {
		return InstructionMap.mnemonics.get(code[2 * i]) + " " + code[2 * i + 1];
	}

	public void clear(int start, int end) {
		for (int i = 2 * start; i < 2 * end; i++) {
			code[i] = 0;
		}
	}

	public String getText(int i) {
		String s1 = Integer.toHexString(getOpCode(i)).toUpperCase();
		String s2 = "";
		int x = (getArg(i));
		if (x < 0) s2 += "-";
		s2 += Integer.toHexString(Math.abs(x));
		return s1 + " " + s2;
	}

	public void setCode(int i, int op, int arg) {
		code[i * 2] = op;
		code[i * 2 + 1] = arg;
	}
}
