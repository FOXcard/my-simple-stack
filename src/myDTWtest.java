import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.enseeiht.danck.voice_analyzer.DTWHelper;
import fr.enseeiht.danck.voice_analyzer.Extractor;
import fr.enseeiht.danck.voice_analyzer.Field;
import fr.enseeiht.danck.voice_analyzer.MFCC;

public class myDTWtest {

	protected static int MFCCLength = 13;

	static DTWHelper myDTWHelper = new myDTW();

	static String curPathS;

	// Fonction permettant de calculer la taille des Fields
	// c'est-à-dire le nombre de MFCC du Field
	static int FieldLength(String fileName) throws IOException {
		int counter = 0;
		File file = new File(System.getProperty("user.dir") + fileName);
		for (String line : Files.readAllLines(file.toPath(), Charset.defaultCharset())) {
			counter++;
		}
		return 2 * Math.floorDiv(counter, 512);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		File curPath = new File("");
		curPathS = curPath.getAbsolutePath();

//		String baseD = curPathS + "/test_res/corpus/Corpus_Nathan/";
		String baseTest = curPathS + "/test_res/corpus/Corpus/Gas/";

		System.out.println(recognitionRate_usingMeanValue(baseTest, curPathS + "/test_res/corpus/Corpus/Tim/",
				curPathS + "/test_res/corpus/Corpus/Nathan/", curPathS + "/test_res/corpus/Corpus/Vero/",
				curPathS + "/test_res/corpus/Corpus/Lent/", curPathS + "/test_res/corpus/Corpus/Rapide/",
				curPathS + "/test_res/corpus/Corpus/Ber/") * 100 + "%");
	}

	static void printConfusionMatrix(int confusionMatrix[][]) {
		for (int i = 0; i < confusionMatrix.length; i++) {
			for (int j = 0; j < confusionMatrix.length; j++) {
				System.out.print(confusionMatrix[i][j] + " ");
			}
			System.out.println();
		}
	}

	static private float recognitionRate_usingMeanValue(String testFolder, String... recognitionBaseFolders)
			throws InterruptedException, IOException {

		List<String> commandsBaseReco = new ArrayList<>();
		List<String> commandsTests;

		for (String recoFolder : recognitionBaseFolders) {
			commandsBaseReco.addAll(getListCommandsFromFolder(recoFolder));
		}

		commandsTests = getListCommandsFromFolder(testFolder);

		Map<String, Integer> mapCommandsNameIndex = new HashMap<>();
		Map<String, List<String>> mapCommandsNameCommandsPath = new HashMap<>();
		initializeMaps(commandsBaseReco, mapCommandsNameIndex, mapCommandsNameCommandsPath);

		System.out.println("");

		int nbCommands = mapCommandsNameIndex.size();

		int[][] confusionMatrix = new int[nbCommands][nbCommands];

		for (String testPath : commandsTests) {
			System.out.println("\n\n" + testPath);
			float minDist = Float.POSITIVE_INFINITY;
			String minRecoCommand = "";
			for (String commandName : mapCommandsNameCommandsPath.keySet()) {
				System.out.println("\tTesting commandes : " + commandName);
				List<String> commandPaths = mapCommandsNameCommandsPath.get(commandName);
				float distsMean = 0;
				for (String commandPath : commandPaths) {
					float dist = computeDistance(commandPath, testPath);
					System.out.print(dist + " ");
					distsMean += dist;
				}

				System.out.println("");

				distsMean /= commandPaths.size();

				if (distsMean < minDist) {
					minDist = distsMean;
					minRecoCommand = commandName;
				}
			}

			int indexCommandRecognized = mapCommandsNameIndex.get(minRecoCommand);
			int indexPerdicted = mapCommandsNameIndex.get(getCommandNameFromPath(testPath));

			if (indexCommandRecognized != indexPerdicted) {
				System.out.println("ERREUR : " + indexCommandRecognized + " " + indexPerdicted + "\n\t\tRecognized : "
						+ keyOfValue(mapCommandsNameIndex, indexCommandRecognized) + "\n\t\tPredicted : "
						+ keyOfValue(mapCommandsNameIndex, indexPerdicted));
			}

			confusionMatrix[indexCommandRecognized][indexPerdicted] = 1;
		}

		printConfusionMatrix(confusionMatrix);

		return computeRecognitionRate(confusionMatrix, commandsTests.size());
	}

	static private float recognitionRate_usingMinValue(String testFolder, String... recognitionBaseFolders)
			throws InterruptedException, IOException {

		List<String> commandsBaseReco = new ArrayList<>();
		List<String> commandsTests;

		for (String recoFolder : recognitionBaseFolders) {
			commandsBaseReco.addAll(getListCommandsFromFolder(recoFolder));
		}

		commandsTests = getListCommandsFromFolder(testFolder);

		Map<String, Integer> mapCommandsNameIndex = new HashMap<>();
		Map<String, List<String>> mapCommandsNameCommandsPath = new HashMap<>();
		initializeMaps(commandsBaseReco, mapCommandsNameIndex, mapCommandsNameCommandsPath);

		System.out.println("");

		int nbCommands = mapCommandsNameIndex.size();

		int[][] confusionMatrix = new int[nbCommands][nbCommands];

		for (String testPath : commandsTests) {
			System.out.println("\n\n" + testPath);
			float minDist = Float.POSITIVE_INFINITY;
			String minRecoCommand = "";
			for (String commandName : mapCommandsNameCommandsPath.keySet()) {
				System.out.println("\tTesting commandes : " + commandName);
				List<String> commandPaths = mapCommandsNameCommandsPath.get(commandName);
				float distsMean = 0;
				for (String commandPath : commandPaths) {
					float dist = computeDistance(commandPath, testPath);
					System.out.print(dist + " ");
					distsMean += dist;
					if (dist < minDist) {
						minDist = dist;
						minRecoCommand = commandName;
					}
				}

				System.out.println("");
			}

			int indexCommandRecognized = mapCommandsNameIndex.get(minRecoCommand);
			int indexPerdicted = mapCommandsNameIndex.get(getCommandNameFromPath(testPath));

			if (indexCommandRecognized != indexPerdicted) {
				System.out.println("ERREUR : " + indexCommandRecognized + " " + indexPerdicted + "\n\t\tRecognized : "
						+ keyOfValue(mapCommandsNameIndex, indexCommandRecognized) + "\n\t\tPredicted : "
						+ keyOfValue(mapCommandsNameIndex, indexPerdicted));
			}

			confusionMatrix[indexCommandRecognized][indexPerdicted] = 1;
		}

		printConfusionMatrix(confusionMatrix);

		return computeRecognitionRate(confusionMatrix, commandsTests.size());
	}

	static private <A, B> A keyOfValue(Map<A, B> map, B value) {
		for (A key : map.keySet())
			if (map.get(key) == value) {
				return key;
			}

		return null;
	}

	static private float computeRecognitionRate(int confusionMatrix[][], int nbTests) {
		float ret = 0.f;

		for (int i = 0; i < confusionMatrix.length; i++) {
			if (confusionMatrix[i][i] > 0)
				ret++;
		}

		return ret / (float) nbTests;
	}

	static private final String filesExtension = ".csv";

	static private List<String> getListCommandsFromFolder(String folder) {
		List<String> retList = new ArrayList<>();

		File f = new File(folder);

		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(filesExtension);
			}
		};

		String[] files = f.list(filter);

		if (files == null) {
			return null;
		}

		for (String file : files)
			retList.add(folder + file);

		return retList;
	}

	static private void initializeMaps(List<String> commands, Map<String, Integer> mapCommandsNameIndex,
			Map<String, List<String>> mapCommandsNameCommandsPath) {
		int i = 0;
		for (String commandPath : commands) {
			String commandName = getCommandNameFromPath(commandPath);

			if (!mapCommandsNameCommandsPath.containsKey(commandName)) {
				mapCommandsNameIndex.put(commandName, i++);
				mapCommandsNameCommandsPath.put(commandName, new ArrayList<>());
			}

			List<String> l = mapCommandsNameCommandsPath.get(commandName);
			l.add(commandPath);
		}

	}

	static String getCommandNameFromPath(String path) {
		String[] splittedS = path.split("/");
		splittedS = splittedS[splittedS.length - 1].split("_");
		splittedS = splittedS[splittedS.length - 1].split("\\.");
		return splittedS[0];
	}

	static private float computeDistance(String s1, String s2) throws InterruptedException, IOException {
		Extractor extractor = Extractor.getExtractor();

		s1 = s1.replaceFirst(curPathS, "");
		s2 = s2.replaceFirst(curPathS, "");

		MultipleFileWindowMaker windowMaker;
		ArrayList<String> files;

		files = new ArrayList<>();
		files.add(s1);
		MFCCLength = FieldLength(s1);
		windowMaker = new MultipleFileWindowMaker(files);

		MFCC[] mfccs1 = new MFCC[MFCCLength];
		for (int i = 0; i < mfccs1.length; i++) {
			mfccs1[i] = extractor.nextMFCC(windowMaker);
		}
		Field field1 = new Field(mfccs1);

		files = new ArrayList<>();
		files.add(s2);
		MFCCLength = FieldLength(s2);
		windowMaker = new MultipleFileWindowMaker(files);

		MFCC[] mfccs2 = new MFCC[MFCCLength];
		for (int i = 0; i < mfccs2.length; i++) {
			mfccs2[i] = extractor.nextMFCC(windowMaker);
		}
		Field field2 = new Field(mfccs2);

		return myDTWHelper.DTWDistance(field1, field2);
	}

}
