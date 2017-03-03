package project;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InstructionMap {
	public static Set<String> sourceCodes = new TreeSet<>();
	public static Map<String, Integer> opcode = new TreeMap<>();
	public static Map<Integer, String> mnemonics = new TreeMap<>();
	public static Set<String> noArgument = new TreeSet<>();
	public static Set<String> immediateOK = new TreeSet<>();
	public static Set<String> indirectOK = new TreeSet<>();

	static {
		sourceCodes.add("NOP");
		sourceCodes.add("LOD");
		sourceCodes.add("STO");
		sourceCodes.add("JUMP");
		sourceCodes.add("JMPZ");
		sourceCodes.add("ADD");
		sourceCodes.add("SUB");
		sourceCodes.add("MUL");
		sourceCodes.add("DIV");
		sourceCodes.add("AND");
		sourceCodes.add("NOT");
		sourceCodes.add("CMPL");
		sourceCodes.add("CMPZ");
		sourceCodes.add("JMPN");
		sourceCodes.add("HALT");
		// add the other source code names listed above (14 including NOP)

		immediateOK.add("LOD");
		immediateOK.add("ADD");
		immediateOK.add("SUB");
		immediateOK.add("MUL");
		immediateOK.add("DIV");
		immediateOK.add("STO");
		immediateOK.add("JUMP");
		immediateOK.add("JMPZ");
		immediateOK.add("AND");
		// add the other source code names that allow immediate forms
		// (8 including LOD)

		indirectOK.add("LOD");
		indirectOK.add("STO");
		indirectOK.add("ADD");
		indirectOK.add("SUB");
		indirectOK.add("DIV");
		indirectOK.add("MUL");
		indirectOK.add("JUMP");
		// add the other source code names that allow indirect forms
		// (6 including LOD)

		noArgument.add("HALT");
		noArgument.add("NOT");
		noArgument.add("NOP");
		// add the other source code names that do not take arguments
		// (3 including HALT)

		opcode.put("NOP", 0x0);
		opcode.put("LODI", 0x1);
		opcode.put("LOD", 0x2);
		opcode.put("LODN", 0x3);
		opcode.put("STO", 0x4);
		opcode.put("STON", 0x5);
		opcode.put("JMPI", 0x6);
		opcode.put("JUMP", 0x7);
		opcode.put("JMZI", 0x8);
		opcode.put("JMPZ", 0x9);
		opcode.put("ADDI", 0xA);
		opcode.put("ADD", 0xB);
		opcode.put("ADDN", 0xC);
		opcode.put("SUBI", 0xD);
		opcode.put("SUB", 0xE);
		opcode.put("SUBN", 0xF);
		opcode.put("MULI", 0x10);
		opcode.put("MUL", 0x11);
		opcode.put("MULN", 0x12);
		opcode.put("DIVI", 0x13);
		opcode.put("DIV", 0x14);
		opcode.put("DIVN", 0x15);
		opcode.put("ANDI", 0x16);
		opcode.put("AND", 0x17);
		opcode.put("NOT", 0x18);
		opcode.put("CMPL", 0x19);
		opcode.put("CMPZ", 0x1A);
		opcode.put("JMPN", 0x1B);
		opcode.put("HALT", 0x1F);
		// add all the other instructions given in Lab11 and the mapping to
		// their
		// opcode, which is the number of the instruction given in Lab

		for (String str : opcode.keySet()) {
			mnemonics.put(opcode.get(str), str);
		}

	}
}