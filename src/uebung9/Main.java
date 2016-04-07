package uebung9;
public class Main {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err
					.println("arguments: <Christmas?> <timesteps> [<timesteps per image>]");
			System.exit(1);
		}

		boolean christmas = (toInt(args[0]) != 0);
		if (christmas) {
			System.out.println("It's Christmas time!");
		}
		int timesteps = toInt(args[1]);

		int timestepsPerImage = -1;
		if (args.length > 2) {
			timestepsPerImage = toInt(args[2]);
		}

		System.out.println("Generating input...");
		Input input;
		if (christmas) {
			input = new ChristmasInput();
		} else {
			input = new RandomInput();
		}
		byte[] start = input.get();

		System.out.println("Initializing computation...");
		GameOfLife gol;
		// TODO: replace with your implementation!
		gol = new GameOfLifeCPU();
		// gol = new GameOfLifeJCuda();
		gol.setStart(start);

		// output initial image
		Utils.writeImage(gol, "start");

		System.out.println("Starting computation of " + timesteps
				+ " time steps...");
		long startMillis = System.currentTimeMillis();
		for (int i = 0; i < timesteps; i++) {
			gol.iterate();

			if (i > 0 && timestepsPerImage > 0 && ((i + 1) % timestepsPerImage) == 0) {
				String suffix = Utils.formatTimestep(i, timesteps);
				Utils.writeImage(gol, suffix);
			}
		}
		long duration = System.currentTimeMillis() - startMillis;
		System.out.println("Finished comuptation in " + duration + "ms");

		// ensure output of final image
		Utils.writeImage(gol, "final");
	}

	private static int toInt(String arg) {
		try {
			return Integer.parseInt(arg);
		} catch (NumberFormatException e) {
			System.err.println("malformed argument: " + arg);
			System.exit(2);
		}

		return -1;
	}

}
