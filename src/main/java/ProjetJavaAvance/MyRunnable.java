package ProjetJavaAvance;

import java.util.Arrays;
import java.util.List;

/**
 * Un Runnable d�di� au projet
 */
public class MyRunnable implements Runnable {
	
	/**
	 * Les lignes que ce Runnable aura � traiter
	 */
	private List<String[]> lignes;
	
	/**
	 * Le num�ro de la ligne dans le fichier lu au d�part quand ce Rennable a �t� cr��
	 */
	private int numLigne;
	
	/**
	 * Rends les lignes que ce Runnable aura � traiter
	 * @return les lignes que ce Runnable aura � traiter
	 */
	public List<String[]> getLignes() {
		return lignes;
	}
	
	/**
	 * Constructeur
	 * @param lignes les lignes que ce Runnable aura � traiter
	 * @param numLigne Le num�ro de la ligne dans le fichier lu au d�part quand ce Rennable a �t� cr��
	 */
	public MyRunnable(List<String[]> lignes, int numLigne) {
		this.lignes = lignes;	
		this.numLigne = numLigne;
	}
	
	/**
	 * M�thode d'impl�mentation Runnable (run)
	 */
	public void run() {
		// System.out.println(this.toString() + " dodo");
		dormir();
		// Le s�parateur de fin de traitement
		String[] SEPARATEUR = null;
		// System.out.println(this.toString() + " DEBUT");
		int colonne; // L'indice de la colonne � traiter, la derni�re de la ligne.
		
		// On parcours chaques lignes que le Runnable a � traiter
		for (String[] ligne : lignes) {
			// On calcule la position de la colonne � traiter
			colonne =  Arrays.asList(ligne).size()-1;
			// Calcule X 2 sur chaque ligne
			ligne[colonne] = String.valueOf(Integer.valueOf(ligne[colonne])*2);
		}
		
		// On alimente le s�parateur avec quelques infos
		SEPARATEUR = new String[] {"Je suis " + this + " et j'ai boss� de la ligne " +
				(numLigne-lignes.size()+1) + " � la ligne " + numLigne + ". Salut..."};
		
		// Au final on ajoute le s�parateur
		lignes.add(SEPARATEUR);
		dormir();
		// System.out.println(this.toString() + " FIN");
	}
	
	private void dormir() {
		try { // On peut d'attente pour avoir le temps de voir le parall�lisme
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
