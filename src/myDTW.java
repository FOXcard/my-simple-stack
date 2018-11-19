import fr.enseeiht.danck.voice_analyzer.DTWHelper;
import fr.enseeiht.danck.voice_analyzer.Field;

public class myDTW extends DTWHelper {

	@Override
	public float DTWDistance(Field unknown, Field known) {
		// Methode qui calcule le score de la DTW
		// entre 2 ensembles de MFCC

		myMFCCdistance distanceCalculator = new myMFCCdistance();
		float matriceDistances[][] = new float[unknown.getLength() + 1][known.getLength() + 1];

		final float INF = Float.POSITIVE_INFINITY;

		matriceDistances[0][0] = 0;

		for (int i = 1; i < known.getLength() + 1; i++) {
			matriceDistances[0][i] = INF;
		}

		final int poids0 = 1;
		final int poids1 = 2;
		final int poids2 = 1;

		for (int i = 1; i < unknown.getLength() + 1; i++) {
			matriceDistances[i][0] = INF;
			for (int j = 1; j < known.getLength() + 1; j++) {

				float distIJ = distanceCalculator.distance(unknown.getMFCC(i - 1), known.getMFCC(j - 1));
				float min1 = Float.min(matriceDistances[i - 1][j] + poids0 * distIJ,
						matriceDistances[i - 1][j - 1] + poids1 * distIJ);
				matriceDistances[i][j] = Float.min(min1, matriceDistances[i][j - 1] + poids2 * distIJ);
			}
		}

		return matriceDistances[unknown.getLength()][known.getLength()] / (unknown.getLength() + known.getLength());
	}

}
