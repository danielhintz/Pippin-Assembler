package myassembler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.InstructionMap;

public class MyAssembler {

	private static List<String> outText = new ArrayList<>();

	private static class SourceLine {
		private String line;
		private int lineNum;

		private SourceLine(String line, int n) {
			this.line = line;
			lineNum = n;
		}
	}

	private MyAssembler() {
	}

	private static int combine(int opCode, int arg) {
		return ((opCode << 24) | (arg & 0xffffff));
	}

	private static boolean isHexadecimal(String str) {
		if (str == null) return false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (i == 0 && (c == '-' || c == '+')) continue;
			if (!Character.isDigit(c)) {
				switch (Character.toLowerCase(c)) {
				case 'a':
				case 'b':
				case 'c':
				case 'd':
				case 'e':
				case 'f':
					continue;
				}
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		assemble(new File("testing/loop through array.s"), new File("testing/looper.pexe"));
	}

	public static String assemble(File input, File output) {
		assemble(input);
		try {
			PrintWriter pw = new PrintWriter(output);
			for (String s : outText) {
				if (s.contains(" ")) {
					String[] spl = s.split(" ");
					String res = spl[0] + " ";
					int i = Integer.parseInt(spl[1]);
					if (i < 0) {
						i *= -1;
						res += "-";
					}
					res += Integer.toHexString(i).toUpperCase();
					pw.println(res);
				} else {
					pw.println(s);
				}
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "success";
	}

	private static int[] assemble(File in) {
		outText.clear();
		int[] program = null;
		BufferedReader br = null;
		List<Object[]> dataEntries = new ArrayList<>();
		List<Integer> instructions = new ArrayList<>();
		List<SourceLine> lines = new ArrayList<>();
		Map<String, Integer> symbolTableLabels = new HashMap<>();
		Map<String, Integer> symbolTable = new HashMap<>();
		int varCount = 0;
		int sec = 1;
		int lineNum = 1;
		int sourceLineNum = 0;
		int mainLine = -1;

		try {
			br = new BufferedReader(new FileReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				sourceLineNum++;
				line = line.trim();
				if (line.contains("//")) {
					line = line.substring(0, line.indexOf("//")).trim();
				}
				if (line.length() == 0) {
					// continue;
				}
				if (line.startsWith(".data")) {
					sec = 0;
					continue;
				}
				if (line.startsWith(".text")) {
					sec = 1;
					continue;
				}
				if (sec == -1) throw new CompilerException("No .data or .text!!", in, sourceLineNum);
				if (sec == 0 && line.length() != 0) {
					String[] split = line.split(":");
					String name = split[0].trim();
					int data = Integer.parseInt(split[1].trim());
					int x = varCount++;

					dataEntries.add(new Object[] { data, x });
					symbolTable.put(name, x);
				} else if (sec == 1) {
					if (line.trim().matches(".+:")) {
						String[] split = line.split(":");
						String lName = split[0];
						if (isHexadecimal(lName))
							throw new CompilerException("Bad label name: " + lName, in, sourceLineNum);
						if (lName.equals("main")) {
							mainLine = lineNum;
						}
						symbolTableLabels.put(lName, lineNum);
					} else {
						lines.add(new SourceLine(line, sourceLineNum));
						if (line.trim().length() != 0 && !line.trim().startsWith("//")) {
							lineNum++;
						}
					}
				}
			}

			if (mainLine == -1) {
				throw new CompilerException("No entry point main", in, 1);
			}

			// Create instructions for the data to be stored in memory
			for (Object[] o : dataEntries) {
				int data = (int) o[0];
				int off = (int) o[1];
				/*
				 * lodi data sto off
				 */
				int opCode = getInstruction("LODI");
				instructions.add(combine(opCode, data));

				opCode = getInstruction("STO");
				instructions.add(combine(opCode, off));
			}

			// Add the jump to "main"
			// 2 instructions per data entry
			instructions.add(combine(getInstruction("JMPI"), mainLine));

			lineNum = 0;
			sourceLineNum = dataEntries.size() + 4;
			for (int i = 0; i < lines.size(); i++) {
				line = lines.get(i).line.trim();
				sourceLineNum = lines.get(i).lineNum;
				if (line.length() != 0 && !line.trim().startsWith("//")) {
					String[] split = line.split(" ");

					int opCode;
					int arg;
					int res;

					if (split.length == 1) {
						if (!InstructionMap.noArgument.contains(split[0].toUpperCase())) {
							throw new CompilerException(split[0].toUpperCase() + " must have an argument", in,
									sourceLineNum);
						} else {
							split = Arrays.copyOf(split, 2);
							split[1] = "0";
						}
					}

					if (split[1].startsWith("#")) {
						split[0] = split[0].toUpperCase();
						if (!InstructionMap.immediateOK.contains(split[0])) {
							throw new CompilerException("Immediate mode not supported by " + split[0], in,
									sourceLineNum);
						}
						split[1] = split[1].substring(1);
						if (split[0].equals("JUMP")) {
							split[0] = "JMPI";
						} else if (split[0].equals("JMPZ")) {
							split[0] = "JMZI";
						} else {
							split[0] += "I";
						}
					} else if (split[1].startsWith("&")) {
						split[0] = split[0].toUpperCase();
						if (!InstructionMap.indirectOK.contains(split[0])) {
							throw new CompilerException("Indirect mode not supported by " + split[0], in,
									sourceLineNum);
						}
						split[1] = split[1].substring(1);
						if (split[0].equals("JUMP")) {
							split[0] = "JMPN";
						} else {
							split[0] += "N";
						}
					}

					if (isHexadecimal(split[1])) {
						arg = Integer.parseInt(split[1], 16);
					} else if (symbolTableLabels.containsKey(split[1])) {
						int l = symbolTableLabels.get(split[1]);
						arg = l - lineNum - 1;
					} else if (symbolTable.containsKey(split[1])) {
						arg = symbolTable.get(split[1]);
					} else {
						throw new CompilerException("Unknown symbol: " + split[1], in, sourceLineNum);
					}

					opCode = (getInstruction(split[0]));
					if (opCode == -1)
						throw new CompilerException("Unknown instruction: " + split[0], in, sourceLineNum);
					res = combine(opCode, arg);
					instructions.add(res);
					lineNum++;
				}
			}
			program = new int[instructions.size()];
			for (int i = 0; i < instructions.size(); i++) {
				program[i] = instructions.get(i);
			}
			for (int i = dataEntries.size() * 2 + 1; i < program.length; i++) {
				outText.add((Integer.toHexString(getOpCode(program[i])) + " " + getArg(program[i])).toUpperCase());
			}
			outText.add("-1");
			for (int i = 0; i < dataEntries.size(); i++) {
				Object[] entry = dataEntries.get(i);
				outText.add((int) entry[1] + " " + entry[0]);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return program;
	}

	public static void save(File out, int[] program) {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(out));
			for (int i : program) {
				dos.writeInt(i);
			}
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int[] loadBin(File in) {
		List<Integer> res = new ArrayList<>();
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(in));
			while (dis.available() > 0) {
				res.add(dis.readInt());
			}
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int[] ret = new int[res.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = res.get(i);
		}
		return ret;
	}

	public static int getArg(int ins) {
		int arg = (int) (ins & 0xffffff);
		if (arg >> 23 == 1) {
			arg = arg | 0xff000000;
		}
		return arg;
	}

	public static int getOpCode(int ins) {
		return ((ins >> 24) & 0xff);
	}

	public static int getInstruction(String string) {
		Integer i = InstructionMap.opcode.get(string.toUpperCase());
		if (i == null) return -1;
		return i.intValue();
	}

}
