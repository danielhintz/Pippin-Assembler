package project;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
//import org.junit.Rule;
import org.junit.Test;
//import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class InstructionTester {

	MachineModel machine = new MachineModel();
	int[] dataCopy = new int[Memory.DATA_SIZE];
	int accInit;
	int ipInit;
	int offsetinit;

	@Before
	public void setup() {
		for (int i = 0; i < Memory.DATA_SIZE; i++) {
			dataCopy[i] = -5 * Memory.DATA_SIZE + 10 * i;
			machine.setData(i, -5 * Memory.DATA_SIZE + 10 * i);
			// Initially the machine will contain a known spread
			// of different numbers:
			// -10240, -10230, -10220, ..., 0, 10, 20, ..., 10230
			// This allows us to check that the instructions do
			// not corrupt machine unexpectedly.
			// 0 is at index 1024
		}
		accInit = 30;
		ipInit = 30;
		offsetinit = 200;
		machine.setAccumulator(accInit);
		machine.setInstructionPointer(ipInit);
		machine.setMemoryBase(offsetinit);
	}

	@Test
	public void testNOP() {
		Instruction instr = machine.get(0x0);
		instr.execute(0);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Test accumulator untouched
		assertEquals("Accumulator unchanged", accInit, machine.getAccumulator());
	}

	@Test
	// Test whether load is correct with immediate addressing
	public void testLODI() {
		Instruction instr = machine.get(0x1);
		machine.setAccumulator(27);
		int arg = 12;
		// should load 12 into the accumulator
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Test accumulator modified
		assertEquals("Accumulator changed", 12, machine.getAccumulator());
	}

	@Test
	// Test whether load is correct with direct addressing
	public void testLOD() {
		Instruction instr = machine.get(0x2);
		machine.setAccumulator(27);
		int arg = 12;
		// should load data[offsetinit+12] into the accumulator
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Test accumulator modified
		assertEquals("Accumulator changed", dataCopy[offsetinit + 12], machine.getAccumulator());
	}

	@Test
	// Test whether load is correct with direct addressing
	public void testLODN() {
		Instruction instr = machine.get(0x3);
		machine.setAccumulator(-1);
		int arg = 1000;
		// should load data[offsetinit+data[offsetinit+1000]]
		// into the accumulator
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Test accumulator modified
		assertEquals("Accumulator changed", dataCopy[offsetinit + dataCopy[offsetinit + 1000]], machine.getAccumulator());
	}

	@Test
	// Test whether store is correct with direct addressing
	public void testSTO() {
		Instruction instr = machine.get(0x4);
		int arg = 12;
		machine.setAccumulator(567);
		dataCopy[offsetinit + 12] = 567;
		instr.execute(arg);
		// Test machine is changed correctly
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Test accumulator unchanged
		assertEquals("Accumulator unchanged", 567, machine.getAccumulator());
	}

	@Test
	// Test whether store is correct with indirect addressing
	public void testSTON() {
		Instruction instr = machine.get(0x5);
		int arg = 1000; // we know that address is data[offsetinit + 1000]
		machine.setAccumulator(567);
		dataCopy[offsetinit + dataCopy[offsetinit + 1000]] = 567;
		instr.execute(arg);
		// Test machine is changed correctly
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Test accumulator unchanged
		assertEquals("Accumulator unchanged", 567, machine.getAccumulator());
	}

	@Test
	// this test checks whether the jump is done correctly, when
	// address is the argument
	public void testJMPI() {
		Instruction instr = machine.get(0x6);
		int arg = 260;
		machine.setAccumulator(200);
		machine.setInstructionPointer(300);
		instr.execute(arg);
		// should have set the program counter to 40
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was changed", 300 + arg, machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 200, machine.getAccumulator());
	}

	@Test
	// this test checks whether the jump is done correctly, when
	// address is in memory
	public void testJUMP() {
		Instruction instr = machine.get(0x7);
		int arg = 1028; // the machine value is data[offsetinit+1028]
		machine.setAccumulator(200);
		machine.setInstructionPointer(300);
		instr.execute(arg);
		// should have set the program counter to 40
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was changed", 300 + dataCopy[offsetinit + arg], machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 200, machine.getAccumulator());
	}

	@Test
	// this test checks whether the jump is done correctly, when
	// address is the argument
	public void testJMZI() {
		Instruction instr = machine.get(0x8);
		int arg = 260;
		machine.setAccumulator(0);
		machine.setInstructionPointer(200);
		instr.execute(arg);
		// should have set the program counter to 40
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was changed", 200 + arg, machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 0, machine.getAccumulator());
	}

	@Test
	// this test checks whether the jump is done correctly, when
	// address is in memory
	public void testJMPZ() {
		Instruction instr = machine.get(0x9);
		int arg = 1028; // the machine value is data[offsetinit+1028]
		machine.setAccumulator(0);
		machine.setInstructionPointer(300);
		instr.execute(arg);
		// should have set the program counter to 40
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was changed", 300 + dataCopy[offsetinit + arg], machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 0, machine.getAccumulator());
	}

	@Test
	// this test checks whether no jump is done if accumulator is zero,
	// address is the argument
	public void testJMZIaccumNonZero() {
		Instruction instr = machine.get(0x8);
		int arg = 260;
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have set the program counter incremented
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 200, machine.getAccumulator());
	}

	@Test
	// this test checks whether no jump is done if accumulator is zero,
	// address is in memory
	public void testJMPZdirectAccumNonZero() {
		Instruction instr = machine.get(0x9);
		int arg = 260; // the machine value is data[260] = -2560+2600 = 40
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have set the program counter incremented
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was not changed", 200, machine.getAccumulator());
	}

	@Test
	// this test checks whether the add is done correctly, when
	// addressing is immediate
	public void testADDI() {
		Instruction instr = machine.get(0xA);
		int arg = 12;
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have added 12 to accumulator
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200 + 12, machine.getAccumulator());
	}

	@Test
	// this test checks whether the add is done correctly, when
	// addressing is direct
	public void testADD() {
		Instruction instr = machine.get(0xB);
		int arg = 12; // we know that memory value is dataCopy[offsetinit+12]
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have added 200 + dataCopy[offsetinit+12] to accumulator
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200 + dataCopy[offsetinit + 12], machine.getAccumulator());
	}

	@Test
	// this test checks whether the add is done correctly, when
	// addressing is indirect
	public void testADDN() {
		Instruction instr = machine.get(0xC);
		int arg = 1000; // we know that address is data[offsetinit + 1000]
		// and the memory value is data[offsetinit + data[offsetinit + 1000]]
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have added -9840 to accumulator
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200 + dataCopy[offsetinit + dataCopy[offsetinit + 1000]], machine.getAccumulator());
	}

	@Test
	// this test checks whether the subtract is done correctly, when
	// addressing is immediate
	public void testSUBI() {
		Instruction instr = machine.get(0xD);
		int arg = 12;
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have subtracted 12 from accumulator
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200 - 12, machine.getAccumulator());
	}

	@Test
	// this test checks whether the subtract is done correctly, when
	// addressing is direct
	public void testSUB() {
		Instruction instr = machine.get(0xE);
		int arg = 12; // we know that machine value is dataCopy[offsetinit+12]
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have subtracted -10240+120 from accumulator
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200 - dataCopy[offsetinit + 12], machine.getAccumulator());
	}

	@Test
	// this test checks whether the subtract is done correctly, when
	// addressing is indirect
	public void testSUBN() {
		Instruction instr = machine.get(0xF);
		int arg = 1000; // we know that address is data[offsetinit+1000]
		// and the memory value is data[offsetinit+data[offsetinit+1000]]
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have subtracted data[offsetinit+data[offsetinit+1000]] from
		// accumulator
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200 - dataCopy[offsetinit + dataCopy[offsetinit + 1000]], machine.getAccumulator());
	}

	@Test
	// this test checks whether the multiplication is done correctly, when
	// addressing is immediate
	public void testMULI() {
		Instruction instr = machine.get(0x10);
		int arg = 12;
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have multiplied accumulator by 12
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200 * 12, machine.getAccumulator());
	}

	@Test
	// this test checks whether the multiplication is done correctly, when
	// addressing is direct
	public void testMUL() {
		Instruction instr = machine.get(0x11);
		int arg = 12; // we know that memory value is dataCopy[offsetinit+12]
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have multiplied accumulator by dataCopy[offsetinit+12]
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200 * dataCopy[offsetinit + 12], machine.getAccumulator());
	}

	@Test
	// this test checks whether the multiplication is done correctly, when
	// addressing is indirect
	public void testMULN() {
		Instruction instr = machine.get(0x12);
		int arg = 1000; // we know that address is data[offsetinit+1000]
		// and the memory value is data[offsetinit+data[offsetinit+1000]]
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have multiplied to accumulator 60
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200 * dataCopy[offsetinit + dataCopy[offsetinit + 1000]], machine.getAccumulator());
	}

	@Test
	// this test checks whether the division is done correctly, when
	// addressing is immediate
	public void testDIVI() {
		Instruction instr = machine.get(0x13);
		int arg = 12;
		machine.setAccumulator(200);
		instr.execute(arg);
		// should have divided accumulator by 12
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200 / 12, machine.getAccumulator());
	}

	@Test
	// this test checks whether the division is done correctly, when
	// addressing is direct
	public void testDIV() {
		Instruction instr = machine.get(0x14);
		int arg = 12; // we know that machine value is data[offsetinit + 12]
		machine.setAccumulator(200000);
		instr.execute(arg);
		// should have divided accumulator by data[offsetinit + 12]
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200000 / dataCopy[offsetinit + 12], machine.getAccumulator());
	}

	@Test
	// this test checks whether the division is done correctly, when
	// addressing is indirect
	public void testDIVN() {
		Instruction instr = machine.get(0x15);
		int arg = 1000; // we know that address is data[offsetinit+1000]
		// and the memory value is data[offsetinit+data[offsetinit+1000]]
		machine.setAccumulator(200000);
		instr.execute(arg);
		// should have divided to accumulator
		// data[offsetinit+data[offsetinit+1000]]
		assertArrayEquals(dataCopy, machine.getData());
		assertEquals("Program counter was incremented", ipInit + 1, machine.getInstructionPointer());
		assertEquals("Accumulator was changed", 200000 / dataCopy[offsetinit + dataCopy[offsetinit + 1000]], machine.getAccumulator());
	}

	@Test(expected = DivideByZeroException.class)
	// this test checks whether the DivideByZeroException is thrown
	// for immediate division by 0
	public void testDIVIzero() {
		Instruction instr = machine.get(0x13);
		int arg = 0;
		instr.execute(arg);
	}

	@Test(expected = DivideByZeroException.class)
	// this test checks whether the DivideByZeroException is thrown
	// for division by 0 from machine
	public void testDIVzero() {
		Instruction instr = machine.get(0x14);
		int arg = 1024 - offsetinit;
		instr.execute(arg);
	}

	@Test(expected = DivideByZeroException.class)
	// this test checks whether the DivideByZeroException is thrown
	// for division by 0 from machine
	public void testDIVNzero() {
		Instruction instr = machine.get(0x15);
		machine.setData(100 + offsetinit, 1024 - offsetinit);
		int arg = 100;
		instr.execute(arg);
	}

	@Test
	// Check ANDI when accum and arg equal to 0 gives false
	public void testANDIaccEQ0argEQ0() {
		Instruction instr = machine.get(0x16);
		int arg = 0;
		machine.setAccumulator(0);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check ANDI when accum and arg pos gives true
	public void testANDIaccGT0argGT0() {
		Instruction instr = machine.get(0x16);
		int arg = 300;
		machine.setAccumulator(10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 1", 1, machine.getAccumulator());
	}

	@Test
	// Check ANDI when accum and arg neg gives true
	public void testANDIaccLT0argLT0() {
		Instruction instr = machine.get(0x16);
		int arg = -200;
		machine.setAccumulator(-10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 1", 1, machine.getAccumulator());
	}

	@Test
	// Check ANDI when accum neg and arg pos gives true
	public void testANDIaccLT0argGT0() {
		Instruction instr = machine.get(0x16);
		int arg = 300;
		machine.setAccumulator(-10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 1", 1, machine.getAccumulator());
	}

	@Test
	// Check ANDI when accum pos and arg neg gives true
	public void testANDIaccGT0argLT0() {
		Instruction instr = machine.get(0x16);
		int arg = -200;
		machine.setAccumulator(10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 1", 1, machine.getAccumulator());
	}

	@Test
	// Check AND when accum pos mem equal to zero gives false
	public void testANDIaccGT0argEQ0() {
		Instruction instr = machine.get(0x16);
		int arg = 0;
		machine.setAccumulator(10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check ANDI when accum neg mem equal to zero gives false
	public void testANDIaccLT0argEQ0() {
		Instruction instr = machine.get(0x16);
		int arg = 0;
		machine.setAccumulator(-10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check ANDI when accum equal to zero and mem pos gives false
	public void testANDIaccEQ0argGT0() {
		Instruction instr = machine.get(0x16);
		int arg = 300;
		machine.setAccumulator(0);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check ANDI when accum equal to zero and mem neg gives false
	public void testANDIaccEQ0argLT0() {
		Instruction instr = machine.get(0x16);
		int arg = -200;
		machine.setAccumulator(0);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check AND when accum and mem equal to 0 gives false
	public void testANDaccEQ0memEQ0() {
		Instruction instr = machine.get(0x17);
		int arg = 256;
		machine.setAccumulator(0);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check AND when accum and mem pos gives true
	public void testANDaccGT0memGT0() {
		Instruction instr = machine.get(0x17);
		int arg = 300;
		machine.setAccumulator(10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 1", 1, machine.getAccumulator());
	}

	@Test
	// Check AND when accum and mem neg gives true
	public void testANDaccLT0memLT0() {
		Instruction instr = machine.get(0x17);
		int arg = 200;
		machine.setAccumulator(-10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 1", 1, machine.getAccumulator());
	}

	@Test
	// Check AND when accum neg and mem pos gives true
	public void testANDaccLT0memGT0() {
		Instruction instr = machine.get(0x17);
		int arg = 300;
		machine.setAccumulator(-10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 1", 1, machine.getAccumulator());
	}

	@Test
	// Check AND when accum pos and mem neg gives true
	public void testANDaccGT0memLT0() {
		Instruction instr = machine.get(0x17);
		int arg = 200;
		machine.setAccumulator(10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 1", 1, machine.getAccumulator());
	}

	@Test
	// Check AND when accum pos mem equal to zero gives false
	public void testANDaccGT0memEQ0() {
		Instruction instr = machine.get(0x17);
		int arg = 1024 - offsetinit;
		machine.setAccumulator(10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check AND when accum neg mem equal to zero gives false
	public void testANDaccLT0memEQ0() {
		Instruction instr = machine.get(0x17);
		int arg = 1024 - offsetinit;
		machine.setAccumulator(-10);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check AND when accum equal to zero and mem pos gives false
	public void testANDaccEQ0memGT0() {
		Instruction instr = machine.get(0x17);
		int arg = 300;
		machine.setAccumulator(0);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check AND when accum equal to zero and mem neg gives false
	public void testANDaccEQ0memLT0() {
		Instruction instr = machine.get(0x17);
		int arg = 200;
		machine.setAccumulator(0);
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check NOT greater than 0 gives false
	public void testNOTaccGT0() {
		Instruction instr = machine.get(0X18);
		machine.setAccumulator(10);
		instr.execute(0);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check NOT equal to 0 gives true
	public void testNOTaccEQ0() {
		Instruction instr = machine.get(0X18);
		machine.setAccumulator(0);
		instr.execute(0);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 1", 1, machine.getAccumulator());
	}

	@Test
	// Check NOT less than 0 gives false
	public void testNOTaccLT0() {
		Instruction instr = machine.get(0X18);
		machine.setAccumulator(-10);
		instr.execute(0);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check CMPL when comparing less than 0 gives true
	public void testCMPLmemLT0() {
		Instruction instr = machine.get(0x19);
		int arg = 100;
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 1", 1, machine.getAccumulator());
	}

	@Test
	// Check CMPL when comparing equal to 0 gives false
	public void testCMPLmemEQ0() {
		Instruction instr = machine.get(0x19);
		int arg = 1024 - offsetinit;
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check CMPL when comparing greater than 0 gives false
	public void testCMPLmemGT0() {
		Instruction instr = machine.get(0x19);
		int arg = 1030;
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check CMPZ when comparing less than 0 gives false
	public void testCMPZmemLT0() {
		Instruction instr = machine.get(0x1A);
		int arg = 100;
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	@Test
	// Check CMPZ when comparing equal to 0 gives true
	public void testCMPZmemEQ0() {
		Instruction instr = machine.get(0x1A);
		int arg = 1024 - offsetinit; // should be where data is 0
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 1", 1, machine.getAccumulator());
	}

	@Test
	// Check CMPZ when comparing greater than 0 gives false
	public void testCMPZmemGT0() {
		Instruction instr = machine.get(0x1A);
		int arg = 300;
		instr.execute(arg);
		// Test machine is not changed
		assertArrayEquals(dataCopy, machine.getData());
		// Test program counter incremented
		assertEquals("Program counter incremented", ipInit + 1, machine.getInstructionPointer());
		// Accumulator is 1
		assertEquals("Accumulator is 0", 0, machine.getAccumulator());
	}

	// FROM :
	// http://stackoverflow.com/questions/309396/java-how-to-test-methods-that-call-system-exit
	// @Rule
	// public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	// @Test
	// public void systemExitWithArbitraryStatusCode() {
	// exit.expectSystemExit();
	// //the code under test, which calls System.exit(...);
	// }

	// @Test
	// public void systemExitWithSelectedStatusCode0() {
	// exit.expectSystemExitWithStatus(0);
	// Instruction instr = machine.get(0x1F);
	// instr.execute(0);
	// }
}