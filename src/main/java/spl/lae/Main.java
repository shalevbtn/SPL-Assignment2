package spl.lae;

import java.io.IOException;

import parser.*;

public class Main {
	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.err.println("Usage: java -jar lga.jar <threads> <input> <output>");
            return;
		}

		int numOfThreads = Integer.parseInt(args[0]);
		String inputPath = args[1];
		String outputPath = args[2];

		InputParser inputP = new InputParser();

		try {
			ComputationNode root = inputP.parse(inputPath);
			LinearAlgebraEngine LAE = new LinearAlgebraEngine(numOfThreads);
			root = LAE.run(root);
			OutputWriter.write(root.getMatrix(), outputPath);
       	 	System.out.println(LAE.getWorkerReport());

		} catch (Exception e) {
			OutputWriter.write(e.getMessage(), outputPath);
		}
	}
}