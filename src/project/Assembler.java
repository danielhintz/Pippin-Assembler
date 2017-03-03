package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Assembler {

	public static String assemble(File input, File output) {
		String retVal = "success";
		try {
			List<String> code = new ArrayList<>();
			List<String> data = new ArrayList<>();
			List<String> outText = new ArrayList<>();
			List<String> inText = new ArrayList<>();

			Scanner in = new Scanner(new FileInputStream(input));

			String line;
			int errorLine = Integer.MAX_VALUE;
			boolean errors = false;
			boolean datab = false;
			while (in.hasNext()) {
				line = in.nextLine();
				inText.add(line);
			}
			in.close();

			int i;
			int j;
			int k;
			for (i = 0; i < inText.size(); i++) {
				line = inText.get(i);
				if (line.trim().length() == 0) {
					if (i + 1 < inText.size() && inText.get(i + 1).length() > 0) {
						errorLine = i + 1;
						retVal = "Error: line " + errorLine + " is a blank line";
						errors = true;
						break;
					}
				}
			}
			for (j = 0; j < i; j++) {
				line = inText.get(j);
				if (line.charAt(0) == ' ' || line.charAt(0) == '\t') {
					errorLine = j + 1;
					retVal = "Error: line " + errorLine + " starts with a white space";
					errors = true;
					break;
				}
			}
			for (k = 0; k < j; k++) {
				line = inText.get(k);
				if (line.trim().toUpperCase().equals("DATA")) {
					if (!line.trim().equals("DATA")) {
						errorLine = k + 1;
						retVal = "Error: line " + errorLine + " does not have DATA in upper case";
						errors = true;
						break;
					}
				}
			}

			for (i = 0; i < inText.size(); i++) {
				line = inText.get(i);
				if (line.equals("DATA")) {
					datab = true;
				} else if (!datab) {
					code.add(line);
				} else {
					data.add(line);
				}
			}

			for (i = 0; i < code.size() && i < errorLine; i++) {
				String[] parts = code.get(i).split("\\s+");
				if (InstructionMap.sourceCodes.contains(parts[0].toUpperCase())) {
					if (!InstructionMap.sourceCodes.contains(parts[0])) {
						errorLine = i + 1;
						retVal = "Error: line " + errorLine + " does not have the instruction mnemonic in upper case";
						errors = true;
						break;
					}
				} else {
					errorLine = i + 1;
					retVal = "Error: line " + errorLine + " " + parts[0] + " is not a valid instruction";
					errors = true;
					break;
				}
				if (InstructionMap.noArgument.contains(parts[0])) {
					if (parts.length != 1) {
						errorLine = i + 1;
						retVal = "Error: line " + errorLine + " has an illegal argument";
						errors = true;
						break;
					}
				} else if (parts.length == 1) {
					errorLine = i + 1;
					retVal = "Error: line " + errorLine + " is missing an argument";
					errors = true;
					break;
				} else if (parts.length > 2) {
					errorLine = i + 1;
					retVal = "Error: line " + errorLine + " has more than one argument";
					errors = true;
					break;
				}
				if (parts.length == 2) {
					if (parts[1].startsWith("#")) {
						if (!InstructionMap.immediateOK.contains(parts[0])) {
							errorLine = i + 1;
							retVal = "Error: line " + errorLine + " " + parts[0] + " does not support immediate mode";
							errors = true;
							break;
						} else {
							parts[1] = parts[1].substring(1);
							if (parts[0].equals("JUMP")) {
								parts[0] = "JMPI";
							} else if (parts[0].equals("JMPZ")) {
								parts[0] = "JMZI";
							} else {
								parts[0] += "I";
							}
						}
					} else if (parts[1].startsWith("&")) {
						if (!InstructionMap.indirectOK.contains(parts[0])) {
							errorLine = i + 1;
							retVal = "Error: line " + errorLine + " " + parts[0] + " does not support indirect mode";
							errors = true;
							break;
						} else {
							parts[1] = parts[1].substring(1);
							if (parts[0].equals("JUMP")) {
								parts[0] = "JMPN";
							} else {
								parts[0] += "N";
							}
						}
					}
					try {
						Integer.parseInt(parts[1], 16);
					} catch (NumberFormatException e) {
						errorLine = i + 1;
						retVal = "Error: line " + errorLine + " does not have a numeric argument";
						errors = true;
						break;
					}
				}

				if (!errors) {
					int opcode = InstructionMap.opcode.get(parts[0]);
					if (parts.length == 1) {
						outText.add(Integer.toHexString(opcode).toUpperCase() + " 0");
					} else {
						outText.add(Integer.toHexString(opcode).toUpperCase() + " " + parts[1]);
					}
				}
			}

			outText.add("-1");
			if (i + 2 < errorLine) {
				for (j = 0; j < data.size(); j++) {
					String[] split = data.get(j).split("\\s+");
					if (split.length != 2) {
						errorLine = j + i + 2;
						retVal = "Error: line " + errorLine + " illegal memory format";
						errors = true;
						break;
					} else {
						try {
							Integer.parseInt(split[0], 16);
							Integer.parseInt(split[1], 16);
							outText.add(data.get(j));
						} catch (Exception e) {
							errorLine = j + i + 2;
							retVal = "Error: line " + errorLine + " illegal number format";
							errors = true;
							break;
						}
					}
				}
			}
			if (!errors) {
				PrintWriter pw = new PrintWriter(output);
				for (String s : outText) {
					pw.println(s);
				}
				pw.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return retVal;
	}

	public static void main(String[] args) {
		int start = 3;
		int end = 26;
		for (int i = start; i <= end; i++) {
			String s = "testing/";
			if (i < 10) {
				s += "0";
			}
			s += i + "e";
			String l = getLastLine(s + ".pasm");
			System.out.println(s + "  " + l + "    " + assemble(new File(s + ".pasm"), new File(s + ".pexe")));
		}
	}

	private static String getLastLine(String string) {
		try {
			Scanner s = new Scanner(new FileInputStream(new File(string)));
			String last = "";
			while (s.hasNextLine()) {
				String line = s.nextLine();
				if (line.trim().length() > 0) last = line.trim();
			}
			s.close();
			return last;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
