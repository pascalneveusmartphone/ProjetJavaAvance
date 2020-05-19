package ProjetJavaAvance;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.FilenameUtils;

// Les imports pour traiter les fichiers .csv (voir dependency net.sf.opencsv du pom.xml)
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * La classe capable de traiter un fichier par lots et multi thread en parallèle
 */
public class TraiteurDeFichier {
	
	/**
	 * La liste des Thread
	 */
	private List<Thread> tousLesThreads = null;
	
	/**
	 * Rends le nombre de traitements actifs et compare au dernier nombre
	 * @param lastNumber le dernier nombre de traitements actifs
	 * @return le nombre de traitements actifs et compare au dernier nombre
	 */
	private int getNbTraitementsActif(int lastNumber) {
		// A priori, la réponse est Oui
		int reponse =0;
		// On parcours tous les threads
		for (Thread thread : tousLesThreads) {
			if ( thread.isAlive() ) { // Si un thread est actif
				reponse += 1; // La réponse sera Non
			//	break; // on sort de la boucle
			}
		}
		if ( reponse != lastNumber ) {
			System.out.println(Calendar.getInstance().getTime() + " : il reste " + reponse + " traitements actifs.");
		}
		return reponse;
	}
	/**
	 * Est-ce que tous les Threads sont terminés ?
	 * @return Vrai si tous les Threads sont terminés, false sinon.
	 */
	private boolean isTraitementsFini() {
		int nbTraitementsActis = getNbTraitementsActif(0);
		return nbTraitementsActis==0;
		/*
		  // A priori, la réponse est Oui
		 
		boolean reponse = true;
		// On parcours tous les threads
		for (Thread thread : tousLesThreads) {
			if ( thread.isAlive() ) { // Si un thread est actif
				reponse = false; // La réponse sera Non
				break; // on sort de la boucle
			}
		}
		// On rends la réponse
		return reponse;
		*/
	}
	
	/**
	 * Méthode qui permet de faire le traitement
	 * @param pas le pas de découpage en multi traitement du fichier (le nb de ligne traitées par chaque Thread)
	 */
	public void faireTraitement(String nomEtCheminFichierIn, int pas) {
		
		// Construction d'un CSV Reader sur le fichier en utilisant Paths
		File fichierIn = new File(Paths.get(nomEtCheminFichierIn).toUri());
		// Acquisition d'un FileReader
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(fichierIn);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Construction d'un CSVReader
		CSVReader csvReader = new CSVReader(fileReader, ';');
		
		FileWriter fileWriter = null;
		CSVWriter cvsWriter = null;
		
	    try {
			System.out.println(Calendar.getInstance().getTime() + " : Lecture et découpage du fichier...");
	    	int i = 1; // Indice pour gérer le pas / les lots
	    	int numLigne = 0; // Numéro de la ligne courante dans le fichier lu
	    	Thread thread = null; // Un Thread
	    	MyRunnable myRunnable = null; // Un Runnable
	    	List<String[]> lignes = new ArrayList<String[]>(); // Les lignes lues dans un lot
	    	// On initialise la liste des Threads
	    	tousLesThreads = new ArrayList<Thread>();
	    	// On construit la map des Runnable par Thread
	    	Map<Thread, MyRunnable> mapDesRunnableParThread = new HashMap<Thread, MyRunnable>();
	    	String[] nextLine = null; // La ligne courante lue dans le fichier lu
			// Tant qu'il y a une ligne dans le CSV Reader / dans le fichier en entrée
	    	while ((nextLine = csvReader.readNext()) != null) {
				numLigne ++;
				lignes.add(nextLine);
				if ( i == pas ) { // Si on a atteind la taille d'un lot
					// On construit un Runnable
					myRunnable = new MyRunnable(lignes,numLigne);
					// On construit un Thread à partir du Runnable
					thread = new Thread(myRunnable);
					// On ajoute le Thread à la liste des Threads
					tousLesThreads.add(thread);
					// On sauvegarde le lien Thread / Runnable (on va en avoir besoin plus loin)
					mapDesRunnableParThread.put(thread, myRunnable);
					// On ré initialise l'objet de stockage des lignes à traiter dans le prochain lot
					lignes = new ArrayList<String[]>(); 
					// On recommence pour un nouveau lot
					i = 1;
				} else { // Sinon, si on a pas atteint la taille d'un lot
					i++; // On note que la taille du lot a augmenté
				}
			}
	    	System.out.println(Calendar.getInstance().getTime() + " : " + numLigne + " lignes ont été lues");
	    	System.out.println(Calendar.getInstance().getTime() + " : " + tousLesThreads.size() + " threads ont été préparés");
	    	
	    	// La lecture du fichie in est faite, les Threads ont été créé
	    	// et leurs données leur ont été affectées, on les lance
			System.out.println(Calendar.getInstance().getTime() + " : On les lance");
			for (Thread leThread : tousLesThreads) {
				leThread.start();
			}
			System.out.println(Calendar.getInstance().getTime() + " : " + tousLesThreads.size() + " threads on été lancés");
			
			// On attends la fin de tous les threads car il va falloir construire le fichier out dans l'ordre des lots
			System.out.println(Calendar.getInstance().getTime() + " : On attends la fin des threads");
			int nbTraitementsActifs = getNbTraitementsActif(0);
			while(nbTraitementsActifs!=0) {
				nbTraitementsActifs = getNbTraitementsActif(nbTraitementsActifs);
			}
			System.out.println(Calendar.getInstance().getTime() + " : Tous les Threads sont terminés");

		    // On détermine le nom du fichier de sortie
			String nomDuFichier = fichierIn.getName();
			String nomDuFichierSansExtension =
					(nomDuFichier != null) ? nomDuFichier.substring(0,nomDuFichier.indexOf('.')) : "";
			String extension = FilenameUtils.getExtension(nomDuFichier);
			StringBuffer sb = new StringBuffer();
			sb.append(nomDuFichierSansExtension).append("_output.").append(extension);
			String nomFichierOut = sb.toString();
			System.out.println(Calendar.getInstance().getTime() + " : Nous allons générer le fichier " + nomFichierOut);
		    
			// Préparation du fichier de sortie
			File fichierOut = new File(fichierIn.getParentFile(),nomFichierOut);
			// Création d'un FileWriter
		    fileWriter = null;
			try {
				fileWriter = new FileWriter(fichierOut);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Création d'un CSV Writer
		    cvsWriter = new CSVWriter(fileWriter,';');
		    
			// On parcours la liste des Threads dans l'ordre où elle a été construite 
			// au départ
			for (Thread leThread : tousLesThreads) {
				// Pour chaque thread, on récupère les lignes modifiées
				// (portées par le runnable du Thread)
				myRunnable = mapDesRunnableParThread.get(leThread);
				List<String[]> mesLignesModifiee = myRunnable.getLignes();
				// et on inscrit les lignes dans le fichier de sortie
				for (String[] ligneModifiee : mesLignesModifiee) {
					cvsWriter.writeNext(ligneModifiee);
				}
			}
			System.out.println(Calendar.getInstance().getTime() + " : Le fichier de sortie est généré");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    // On ferme les outils d'I/O
	    try {
	    	fileReader.close();
	    	csvReader.close();
	    	fileWriter.close();
			cvsWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println(Calendar.getInstance().getTime() + " : Les fichiers et buffers sont fermés");
	}

	/**
	 * Méthode Main
	 * @param args
	 */
	public static void main(String[] args) {
		// On construit un traiteur de fichier
		TraiteurDeFichier tdf = new TraiteurDeFichier();
		// On le lance
		tdf.faireTraitement(
				"C:\\Users\\pasca\\Google Drive\\Formations\\Ionis stm\\Cours\\Java Avancé\\Projet\\dataf3.csv", // Fichier IN
				1000); // Lots de 1000 lignes
	}
	
}
