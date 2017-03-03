package project;

public enum States {
	AUTO_STEPPING {
		public void enter() {
			AUTO_STEPPING.states[ASSEMBLE] = false;
			AUTO_STEPPING.states[CLEAR] = false;
			AUTO_STEPPING.states[LOAD] = false;
			AUTO_STEPPING.states[RELOAD] = false;
			AUTO_STEPPING.states[RUN] = true;
			AUTO_STEPPING.states[RUNNING] = true;
			AUTO_STEPPING.states[STEP] = false;
			AUTO_STEPPING.states[CHANGE_JOB] = false;
		}
	},
	NOTHING_LOADED {
		public void enter() {
			NOTHING_LOADED.states[ASSEMBLE] = true;
			NOTHING_LOADED.states[CLEAR] = false;
			NOTHING_LOADED.states[LOAD] = true;
			NOTHING_LOADED.states[RELOAD] = false;
			NOTHING_LOADED.states[RUN] = false;
			NOTHING_LOADED.states[RUNNING] = false;
			NOTHING_LOADED.states[STEP] = false;
			NOTHING_LOADED.states[CHANGE_JOB] = true;
		}
	},
	PROGRAM_HALTED {
		public void enter() {
			PROGRAM_HALTED.states[ASSEMBLE] = true;
			PROGRAM_HALTED.states[CLEAR] = true;
			PROGRAM_HALTED.states[LOAD] = true;
			PROGRAM_HALTED.states[RELOAD] = true;
			PROGRAM_HALTED.states[RUN] = false;
			PROGRAM_HALTED.states[RUNNING] = false;
			PROGRAM_HALTED.states[STEP] = false;
			PROGRAM_HALTED.states[CHANGE_JOB] = true;
		}
	},
	PROGRAM_LOADED_NOT_AUTOSTEPPING {
		public void enter() {
			PROGRAM_LOADED_NOT_AUTOSTEPPING.states[ASSEMBLE] = true;
			PROGRAM_LOADED_NOT_AUTOSTEPPING.states[CLEAR] = true;
			PROGRAM_LOADED_NOT_AUTOSTEPPING.states[LOAD] = true;
			PROGRAM_LOADED_NOT_AUTOSTEPPING.states[RELOAD] = true;
			PROGRAM_LOADED_NOT_AUTOSTEPPING.states[RUN] = true;
			PROGRAM_LOADED_NOT_AUTOSTEPPING.states[RUNNING] = false;
			PROGRAM_LOADED_NOT_AUTOSTEPPING.states[STEP] = true;
			PROGRAM_LOADED_NOT_AUTOSTEPPING.states[CHANGE_JOB] = true;
		}
	};

	private static final int ASSEMBLE = 0;
	private static final int CLEAR = 1;
	private static final int LOAD = 2;
	private static final int RELOAD = 3;
	private static final int RUN = 4;
	private static final int RUNNING = 5;
	private static final int STEP = 6;
	private static final int CHANGE_JOB = 7;

	private boolean[] states = new boolean[8];

	public abstract void enter();

	public boolean getAssembleFileActive() {
		return states[ASSEMBLE];
	}

	public boolean getClearActive() {
		return states[CLEAR];
	}

	public boolean getLoadFileActive() {
		return states[LOAD];
	}

	public boolean getReloadActive() {
		return states[RELOAD];
	}

	public boolean getRunningActive() {
		return states[RUNNING];
	}

	public boolean getRunPauseActive() {
		return states[RUN];
	}

	public boolean getStepActive() {
		return states[STEP];
	}

	public boolean getChangeJobActive() {
		return states[CHANGE_JOB];
	}
}
