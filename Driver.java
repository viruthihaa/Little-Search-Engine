package lse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.IOException;


public class Driver {


    static Scanner sc = new Scanner(System.in);
    static String options = "rgiftlqm";

    static char getOption() {
        System.out.print("\n Choose action: ");
        System.out.print("(r)un it all, ");
        System.out.print("(g)et Keyword, ");
        System.out.print("(i)nsert last occurrence, "); //not used, debugger was easier then building an Occs list for testing
        System.out.print("(f)ind keyword, ");
        System.out.print("(t)op 5 search, ");
        System.out.print("(l)oad keywords from document, ");
        //System.out.print("(m)loadfromdoc, ");
        System.out.print("(q)uit? => ");
        char response = sc.nextLine().toLowerCase().charAt(0);
        while (!options.contains(response+"")) {
            System.out.print("\tYou must enter one of r, g, i, f, t, l, or q => ");
            response = sc.nextLine().toLowerCase().charAt(0);
        }
        return response;
    }

    public static void main(String[] args)
    throws IOException {

        //Create the empty LSE
        ToySearchEngine TSE = new ToySearchEngine();

        char option;
        while ((option = getOption()) != 'q') {
            System.out.println();

            //run it all
            if (option == 'r') {

                System.out.print("Enter docsFile name => ");
                String docsFile = sc.nextLine();
                System.out.print("Enter noisewordsFile name => ");
                String noisewordsFile = sc.nextLine();

                TSE.makeIndex(docsFile, noisewordsFile);

                // for each key
                for (String s : TSE.keywordsIndex.keySet()) {
                    System.out.println("key: "+ s + " value: " + TSE.keywordsIndex.get(s));
                }
            }
            
            else if (option == 'm') {
            	
            	System.out.print("Enter docsFile name => ");
            	String docsFil = sc.nextLine();
            	
            	TSE.mergeKeywords(TSE.loadKeywordsFromDocument(docsFil));
            }
            	

            //getKeyword
            else if (option == 'g') {
                System.out.print("Enter the word to getKeyword() for => ");
                String text = sc.nextLine();
                System.out.println(TSE.getKeyword(text));



            //insert last occurrence
            }else if(option == 'i'){	

                //used debugger to test this method



            //load keywords from document
            }else if (option == 'l'){
                System.out.print("Enter the document to load from => ");
                String doc = sc.nextLine();

                //load up the noisewords
                Scanner sc = new Scanner(new File("noisewords.txt"));
                while (sc.hasNext()) {
                    String word = sc.next();
                    TSE.noiseWords.add(word);
                    //System.out.println(LSE.noiseWords.
                }

                HashMap<String,Occurrence> tester = TSE.loadKeywordsFromDocument(doc);
                // for each key
                if(tester != null) {
                    for (String s : tester.keySet()) {
                        System.out.println("key: " + s + " value: " + tester.get(s));
                    }
                }



            //find keyword in the master hashtable after its been built
            }else if (option == 'f'){
                System.out.print("Enter the key to find => ");
                String text = sc.nextLine();
                System.out.println("key: " + (text) + " value: " + TSE.keywordsIndex.get(text));



            //top5search
            }else if (option == 't'){
                System.out.print("Enter text for kw1 => ");
                String s1 = sc.nextLine();
                System.out.print("Enter text for kw2 => ");
                String s2 = sc.nextLine();

                System.out.println();
                System.out.println("-Top 5 docs for the keywords-");
                //print out the resulting array list
                for (String s : TSE.top5search(s1,s2)) {
                    System.out.println(s);
                }
            }
        }
    }
}

