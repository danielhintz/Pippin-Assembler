package project;

import java.util.Map;
import java.util.TreeMap;

public class MachineModel {
	public final Map<Integer, Instruction> INSTRUCTIONS = new TreeMap<>();

	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private HaltCallBack callback;

	private Code code = new Code();

	private Job[] jobs = new Job[2];
	private Job currentJob;

	public MachineModel() {
		this(() -> System.exit(0));
	}

	public MachineModel(HaltCallBack callback) {
		this.callback = callback;

		for (int i = 0; i < jobs.length; i++) {
			jobs[i] = new Job();
			jobs[i].setStartcodeIndex(i * Code.CODE_MAX / 4);
			jobs[i].setStartmemoryIndex(i * Memory.DATA_SIZE / 2);
		}

		currentJob = jobs[0];

		// NOP
		INSTRUCTIONS.put(0x0, arg -> {
			cpu.incrementIP();
		});

		// LODI
		INSTRUCTIONS.put(0x1, arg -> {
			cpu.setAccumulator(arg);
			cpu.incrementIP();
		});

		// LOD
		INSTRUCTIONS.put(0x2, arg -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			cpu.setAccumulator(mem);
			cpu.incrementIP();
		});

		// LODN
		INSTRUCTIONS.put(0x3, arg -> {
			int arg1 = memory.getData(cpu.getMemoryBase() + arg);
			int mem = memory.getData(cpu.getMemoryBase() + arg1);
			cpu.setAccumulator(mem);
			cpu.incrementIP();
		});

		// STO
		INSTRUCTIONS.put(0x4, arg -> {
			memory.setData(cpu.getMemoryBase() + arg, cpu.getAccumulator());
			cpu.incrementIP();
		});

		// STON
		INSTRUCTIONS.put(0x5, arg -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			memory.setData(cpu.getMemoryBase() + mem, cpu.getAccumulator());
			cpu.incrementIP();
		});

		// JMPI
		INSTRUCTIONS.put(0x6, arg -> {
			cpu.setInstructionPointer(cpu.getInstructionPointer() + arg);
		});

		// JUMP
		INSTRUCTIONS.put(0x7, arg -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			cpu.setInstructionPointer(cpu.getInstructionPointer() + mem);
		});

		// JMZI
		INSTRUCTIONS.put(0x8, arg -> {
			if (cpu.getAccumulator() == 0) {
				INSTRUCTIONS.get(0x06).execute(arg);
			} else {
				cpu.incrementIP();
			}
		});

		// JMPZ
		INSTRUCTIONS.put(0x9, arg -> {
			if (cpu.getAccumulator() == 0) {
				INSTRUCTIONS.get(0x7).execute(arg);
			} else {
				cpu.incrementIP();
			}
		});

		// INSTRUCTION_MAP entry for "ADDI"
		INSTRUCTIONS.put(0xA, arg -> {
			cpu.setAccumulator(cpu.getAccumulator() + arg);
			cpu.incrementIP();
		});

		// INSTRUCTION_MAP entry for "ADD"
		INSTRUCTIONS.put(0xB, arg -> {
			int arg1 = memory.getData(cpu.getMemoryBase() + arg);
			cpu.setAccumulator(cpu.getAccumulator() + arg1);
			cpu.incrementIP();
		});

		// INSTRUCTION_MAP entry for "ADDN"
		INSTRUCTIONS.put(0xC, arg -> {
			int arg1 = memory.getData(cpu.getMemoryBase() + arg);
			int arg2 = memory.getData(cpu.getMemoryBase() + arg1);
			cpu.setAccumulator(cpu.getAccumulator() + arg2);
			cpu.incrementIP();
		});

		// SUBI
		INSTRUCTIONS.put(0xD, (arg) -> {
			cpu.setAccumulator(cpu.getAccumulator() - arg);
			cpu.incrementIP();
		});

		// SUB
		INSTRUCTIONS.put(0xE, (arg) -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			cpu.setAccumulator(cpu.getAccumulator() - mem);
			cpu.incrementIP();
		});

		// SUBN
		INSTRUCTIONS.put(0xF, (arg) -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			int arg2 = memory.getData(cpu.getMemoryBase() + mem);
			cpu.setAccumulator(cpu.getAccumulator() - arg2);
			cpu.incrementIP();
		});

		// MULI
		INSTRUCTIONS.put(0x10, (arg) -> {
			cpu.setAccumulator(cpu.getAccumulator() * arg);
			cpu.incrementIP();
		});

		// MUL
		INSTRUCTIONS.put(0x11, (arg) -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			cpu.setAccumulator(cpu.getAccumulator() * mem);
			cpu.incrementIP();
		});

		// MULN
		INSTRUCTIONS.put(0x12, (arg) -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			int arg2 = memory.getData(cpu.getMemoryBase() + mem);
			cpu.setAccumulator(cpu.getAccumulator() * arg2);
			cpu.incrementIP();
		});

		// DIVI
		INSTRUCTIONS.put(0x13, (arg) -> {
			if (arg == 0) throw new DivideByZeroException();
			cpu.setAccumulator(cpu.getAccumulator() / arg);
			cpu.incrementIP();
		});

		// DIV
		INSTRUCTIONS.put(0x14, (arg) -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			if (mem == 0) throw new DivideByZeroException();
			cpu.setAccumulator(cpu.getAccumulator() / mem);
			cpu.incrementIP();
		});

		// DIVN
		INSTRUCTIONS.put(0x15, (arg) -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			int arg2 = memory.getData(cpu.getMemoryBase() + mem);
			if (arg2 == 0) throw new DivideByZeroException();
			cpu.setAccumulator(cpu.getAccumulator() / arg2);
			cpu.incrementIP();
		});

		// ANDI
		INSTRUCTIONS.put(0x16, (arg) -> {
			int set = 0;
			if (arg != 0 && cpu.getAccumulator() != 0) {
				set = 1;
			}
			cpu.setAccumulator(set);
			cpu.incrementIP();
		});

		// AND
		INSTRUCTIONS.put(0x17, (arg) -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			int set = 0;
			if (mem != 0 && cpu.getAccumulator() != 0) {
				set = 1;
			}
			cpu.setAccumulator(set);
			cpu.incrementIP();
		});

		// NOT
		INSTRUCTIONS.put(0x18, (arg) -> {
			int set = 1;
			if (cpu.getAccumulator() != 0) {
				set = 0;
			}
			cpu.setAccumulator(set);
			cpu.incrementIP();
		});

		// CMPL
		INSTRUCTIONS.put(0x19, (arg) -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			int set = 0;
			if (mem < 0) {
				set = 1;
			}
			cpu.setAccumulator(set);
			cpu.incrementIP();
		});

		// CMPZ
		INSTRUCTIONS.put(0x1A, (arg) -> {
			int mem = memory.getData(cpu.getMemoryBase() + arg);
			int set = 0;
			if (mem == 0) {
				set = 1;
			}
			cpu.setAccumulator(set);
			cpu.incrementIP();
		});

		// JMPN
		INSTRUCTIONS.put(0x1B, arg -> {
			int target = memory.getData(cpu.getMemoryBase() + arg);
			cpu.setInstructionPointer(currentJob.getStartcodeIndex() + target);
		});

		// HALT
		INSTRUCTIONS.put(0x1F, (arg) -> {
			this.callback.halt();
		});
	}

	public void setMemoryBase(int i) {
		cpu.setMemoryBase(i);
	}

	public void setData(int i, int j) {
		memory.setData(i, j);
	}

	public Instruction get(int i) {
		return INSTRUCTIONS.get(i);
	}

	public int[] getData() {
		return memory.getData();
	}

	public int getData(int i) {
		return getData()[i];
	}

	public void setAccumulator(int i) {
		cpu.setAccumulator(i);
	}

	public int getAccumulator() {
		return cpu.getAccumulator();
	}

	public int getInstructionPointer() {
		return cpu.getInstructionPointer();
	}

	public void setInstructionPointer(int i) {
		cpu.setInstructionPointer(i);
	}

	public Code getCode() {
		return code;
	}

	public void setCode(int i, int op, int arg) {
		code.setCode(i, op, arg);
	}

	public Job getCurrentJob() {
		return currentJob;
	}

	public void setJob(int i) {
		if (i != 0 && i != 1) throw new IllegalArgumentException();
		currentJob.setCurrentAcc(getAccumulator());
		currentJob.setCurrentIP(getInstructionPointer());
		currentJob = jobs[i];
		setAccumulator(currentJob.getCurrentAcc());
		setInstructionPointer(currentJob.getCurrentIP());
		cpu.setMemoryBase(currentJob.getStartmemoryIndex());
	}

	public void clearJob() {
		memory.clear(currentJob.getStartmemoryIndex(), currentJob.getStartmemoryIndex() + Memory.DATA_SIZE / 2);
		code.clear(currentJob.getStartcodeIndex(), currentJob.getStartcodeIndex() + currentJob.getCodeSize());
		cpu.setAccumulator(0);
		cpu.setInstructionPointer(currentJob.getStartcodeIndex());
	}

	public void step() {
		try {
			int ip = cpu.getInstructionPointer();
			if (ip < currentJob.getStartcodeIndex() || ip >= currentJob.getStartcodeIndex() + currentJob.getCodeSize()) {
				throw new CodeAccessException("CodeAccessException at line: " + ip);
			}
			int op = code.getOpCode(ip);
			int arg = code.getArg(ip);

			get(op).execute(arg);
		} catch (Exception e) {
			callback.halt();
			throw e;
		}
	}

	public States getCurrentState() {
		return currentJob.getCurrentState();
	}

	public void setCurrentState(States currentState) {
		currentJob.setCurrentState(currentState);
	}

	public int getChangedIndex() {
		return memory.getChangedIndex();
	}

	public int getMemoryBase() {
		return cpu.getMemoryBase();
	}
}
