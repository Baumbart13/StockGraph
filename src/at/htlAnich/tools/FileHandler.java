package at.htlAnich.tools;

import java.io.*;
import java.util.LinkedList;

public class FileHandler {
	/**
	 * Reads a file line for line.
	 * @param file The relative path to the file, including the filename.
	 * @return an array of Strings, where every element is one line.
	 * @throws IOException Thrown if something goes wrong while accessing the file.
	 */
	public static String[] readFile(String file)throws IOException {
		var inputs = new LinkedList<String>();

		try (var br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();

			while (line != null) {
				inputs.add(line);
				line = br.readLine();
			}
		}

		return inputs.toArray(new String[0]);
	}

	/**
	 * Writes context to a file.
	 * @param file The relative path to the file, including the filename.
	 * @param strings The content, that will be written to the file.
	 * @throws IOException Thrown if something goes wrong while accessing the file.
	 */
	public static void writeToFile(String file, String[] strings) throws IOException{

		try (var bw = new BufferedWriter(new FileWriter(file))) {
			for (var string : strings) {
				bw.write(string);
				bw.newLine();
			}
		}
	}

	/**
	 * Writes context to a file.
	 * @param file The relative path to the file, including the filename.
	 * @param string The content, that will be written to the file.
	 * @throws IOException Thrown if something goes wrong while accessing the file.
	 */
	public static void writeToFile(String file, String string) throws IOException{

		try (var bw = new BufferedWriter(new FileWriter(file))) {
			bw.write(string);
			bw.newLine();
		}
	}
}
