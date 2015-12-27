public class RandomInput implements Input {

	public static final double RATIO = 0.3125;

	@Override
	public byte[] get() {
		byte[] input = GameOfLife.createArray();

		for (int i = 0; i < input.length; i++) {
			if (Math.random() <= RATIO) {
				input[i] = 1;
			} else {
				input[i] = 0;
			}
		}

		return input;
	}

}
