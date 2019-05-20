package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		if (docFile == null){
			throw new FileNotFoundException();
		}
		
		HashMap<String,Occurrence> keywords = new HashMap<String,Occurrence>();
		Scanner scan = new Scanner(new File(docFile));
		
		while (scan.hasNext()) {
			String word = getKeyword(scan.next());
			if (word != null){
				if (keywords.get(word) == null){
					keywords.put(word, new Occurrence(docFile, 1));
				}else{
					keywords.get(word).frequency++;
				}
			}
		}
	return keywords;
	}
	
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> keys) {
		for (String key : keys.keySet()){
			if (!keywordsIndex.containsKey(key)){
				ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
				occs.add(keys.get(key));
				keywordsIndex.put(key, occs);
			}else{
				keywordsIndex.get(key).add(keys.get(key));
				insertLastOccurrence(keywordsIndex.get(key));
				keywordsIndex.put(key, keywordsIndex.get(key));
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		word = word.toLowerCase();
		if (word.length() == 1){
			return null;
		}
		if (noiseWords.contains(word)){
			return null;
		}
		
		while (!Character.isLetter(word.charAt(word.length() - 1))){
			char s = word.charAt(word.length() - 1);
			if (s == '.' || s == ',' || s == '?' || s == ':' || s == ';' || s == '!'){
				word = word.substring(0, word.length() - 1);
			}else{
				return null;
			}
		}
		
		for (int i = 0; i < word.length(); i++){
			if (Character.isLetter(word.charAt(i)) == false){
				return null;
			}
		}
		
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
ArrayList<Integer> result = new ArrayList<Integer>();
		
		int size = occs.size();
		int p = 0, s = size - 2, n;
		int fr = occs.get(occs.size() - 1).frequency;
		
		while (p < s){
			n = (p + s)/2;
			result.add(n);
			if (occs.get(n).frequency > occs.get(occs.size() - 1).frequency){
				p = n + 1;
			}else if (occs.get(n).frequency == occs.get(occs.size() - 1).frequency){
				p = n;
				s = n;
				break;
			}else{
				s = n;
			}
		}
				
		if (fr > occs.get(p).frequency){
			occs.add(p, occs.remove(occs.size() - 1));
		}else{
			occs.add(p + 1, occs.remove(occs.size() - 1));
		}
	return result;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner scan = new Scanner(new File(noiseWordsFile));
		while (scan.hasNext()) {
			String word = scan.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		scan = new Scanner(new File(docsFile));
		while (scan.hasNext()) {
			String docFile = scan.next();
			HashMap<String,Occurrence> keys = loadKeywordsFromDocument(docFile);
			mergeKeywords(keys);
		}
		scan.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> docs = new ArrayList<String>();
		ArrayList<Occurrence> first = null;
		ArrayList<Occurrence> sec = null;
		
		if (keywordsIndex.get(kw1) != null){
			first = keywordsIndex.get(kw1);
		}
		
		if (keywordsIndex.get(kw2) != null){
			sec = keywordsIndex.get(kw2);
		}
		
		if (first == null && sec == null){
			return docs;
		}else if (first != null && sec == null){
			int index = 0;
			while (docs.size() < 5 && index < first.size()){
				if (!docs.contains(first.get(index))){
					docs.add(first.get(index).document);
				}
			index++;
			}
		}else if (sec != null && first == null){
			int index = 0;
			while (docs.size() < 5 && index < sec.size()){
				if (!docs.contains(sec.get(index))){
					docs.add(sec.get(index).document);
				}
			index++;
			}
		}else{
			int index1 = 0, index2 = 0;
			while (index1 < first.size() && index2 < sec.size() && docs.size() < 5){
				if (first.get(index1).frequency > sec.get(index2).frequency){
					if (!docs.contains(first.get(index1).document)){
						docs.add(first.get(index1).document);
					}
				index1++;
				}else if (first.get(index1).frequency < sec.get(index2).frequency){
					if (!docs.contains(sec.get(index2).document)){
						docs.add(sec.get(index2).document);
					}
				index2++;
				}else{
					if (!docs.contains(first.get(index1).document)){
						docs.add(first.get(index1).document);
					}
					if (docs.size() < 5 && !docs.contains(sec.get(index2).document)){
						docs.add(sec.get(index2).document);
					}
				index1++;
				index2++;
				}
			}
			
			if (index1 == first.size()){
				while (index2 < sec.size() && docs.size() < 5){
					if (!docs.contains(sec.get(index2).document)){
						docs.add(sec.get(index2).document);
					}
				index2++;
				}
			}
			
			if (index2 == sec.size()){
				while (index1 < first.size() && docs.size() < 5){
					if (!docs.contains(first.get(index1).document)){
						docs.add(first.get(index1).document);
					}
				index1++;
				}
			}
		}
	return docs;
	}
	
	}

