import Jama.*;

public class Simulator {
  private Renderer rend;
	private World world;
	private Agent agent;
	private double timeStep;

	public void read(String filename) {
    In specFile = new In(filename);
		specFile.readLine();
		timeStep = specFile.readDouble();
		StdOut.println("Time step: " + timeStep);
		for (int i = 0; i < 3; i++) specFile.readLine();
		int stateDim = specFile.readInt();
		StdOut.println("State dimension: " + stateDim);
		for (int i = 0; i < 3; i++) specFile.readLine();
		int controlDim = specFile.readInt();
		StdOut.println("Control dimension: " + controlDim);
		for (int i = 0; i < 3; i++) specFile.readLine();
		Matrix initCond = new Matrix(stateDim, 1);
		for (int i = 0; i < stateDim; i++) {
			initCond.set(i, 0, specFile.readDouble());
		} 
		StdOut.println("Initial conditions: ");
		initCond.print(2, 1);
		for (int i = 0; i < 3; i++) specFile.readLine();
		Matrix initControl = new Matrix(controlDim, 1);
		for (int i = 0; i < controlDim; i++) {
			initControl.set(i, 0, specFile.readDouble());
		} 
		StdOut.println("Initial controls: ");
		initControl.print(2, 1);
		for (int i = 0; i < 3; i++) specFile.readLine();
		Matrix sys = new Matrix(stateDim, stateDim);
		for (int i = 0; i < stateDim; i++) {
			for (int j = 0; j < stateDim; j++) {
				sys.set(i, j, specFile.readDouble());
			} 
		} 
		StdOut.println("Linear system: ");
		sys.print(2, 1);
		for (int i = 0; i < 3; i++) specFile.readLine();
		String controllerName = specFile.readString();
		StdOut.println("Controller: ");
		StdOut.println(controllerName);
		for (int i = 0; i < 3; i++) specFile.readLine();
		Matrix controlSys = new Matrix(stateDim, controlDim);
		for (int i = 0; i < stateDim; i++) {
			for (int j = 0; j < controlDim; j++) {
				double item = specFile.readDouble();
				controlSys.set(i, j, item);
			} 
		} 
		StdOut.println("Control model: ");
		controlSys.print(2, 1);

		// Initialize instance variables
		rend = new GravityRenderer();
		world = new World(timeStep, initCond, initControl, sys, controlSys);
		agent = new Agent(controllerName);
	}

	public void advance() {
		Matrix newControls = agent.getControls();
		world.applyControl(newControls);
		Matrix curState = world.step();
		rend.update(curState);
	}

	public double getTimeStep() {
		return timeStep;
	}
  public static void main (String[] argv) {
		Simulator sim = new Simulator();
		sim.read(argv[0]);
		while(true) {
			sim.advance();
			try { Thread.sleep((int)(1000 * sim.getTimeStep())); } catch(Exception e) {}
		}
	}
}
