package uebung9;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Utils {

	public static String formatTimestep(int currentTimestep, int timesteps) {
		int digits = (int) (Math.floor(Math.log10(timesteps)) + 1);
		String format = "%0" + digits + "d";
		return String.format(format, currentTimestep + 1);
	}

	public static void writeImage(GameOfLife gol, String suffix) {
		long start = System.currentTimeMillis();

		BufferedImage image = new BufferedImage(GameOfLife.WIDTH,
				GameOfLife.HEIGHT, BufferedImage.TYPE_3BYTE_BGR);

		for (int row = 0; row < GameOfLife.HEIGHT; row++) {
			for (int column = 0; column < GameOfLife.WIDTH; column++) {
				int rgb;
				if (gol.isActive(row, column)) {
					rgb = Color.BLACK.getRGB();
				} else {
					rgb = Color.WHITE.getRGB();
				}
				image.setRGB(column, row, rgb);
			}
		}

		String filename = "gol-" + suffix + ".bmp";
		try {
			ImageIO.write(image, "BMP", new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}

		long duration = System.currentTimeMillis() - start;
		System.out.println(" - Output of " + filename + " took " + duration
				+ "ms");
	}

}
