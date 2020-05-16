package ProjetJavaAvance;

import java.util.Arrays;
import java.util.List;

/**
 * Un Runnable dédié au projet
 */
public class MyRunnable implements Runnable {
	
	/**
	 * Les lignes que ce Runnable aura à traiter
	 */
	private List<String[]> lignes;
	
	/**
	 * Le numéro de la ligne dans le fichier lu au départ quand ce Rennable a été créé
	 */
	private int numLigne;
	
	/**
	 * Rends les lignes que ce Runnable aura à traiter
	 * @return les lignes que ce Runnable aura à traiter
	 */
	public List<String[]> getLignes() {
		return lignes;
	}
	
	/**
	 * Constructeur
	 * @param lignes les lignes que ce Runnable aura à traiter
	 * @param numLigne Le numéro de la ligne dans le fichier lu au départ quand ce Rennable a été créé
	 */
	public MyRunnable(List<String[]> lignes, int numLigne) {
		this.lignes = lignes;	
		this.numLigne = numLigne;
	}
	
	/**
	 * Méthode d'implémentation Runnable (run)
	 */
	public void run() {
		// System.out.println(this.toString() + " dodo");
		dormir();
		// Le séparateur de fin de traitement
		String[] SEPARATEUR = null;
		// System.out.println(this.toString() + " DEBUT");
		int colonne; // L'indice de la colonne à traiter, la dernière de la ligne.
		
		// On parcours chaques lignes que le Runnable a à traiter
		for (String[] ligne : lignes) {
			// On calcule la position de la colonne à traiter
			colonne =  Arrays.asList(ligne).size()-1;
			// Calcule X 2 sur chaque ligne
			ligne[colonne] = String.valueOf(Integer.valueOf(ligne[colonne])*2);
		}
		
		// On alimente le séparateur avec quelques infos
		SEPARATEUR = new String[] {"Je suis " + this + " et j'ai bossé de la ligne " +
				(numLigne-lignes.size()+1) + " à la ligne " + numLigne + ". Salut..."};
		
		// Au final on ajoute le séparateur
		lignes.add(SEPARATEUR);
		dormir();
		// System.out.println(this.toString() + " FIN");
	}
	
	private void dormir() {
		try { // On peut d'attente pour avoir le temps de voir le parallélisme
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
