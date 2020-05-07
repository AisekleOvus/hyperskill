import java.io.File;
import java.util.Locale;
import java.util.Scanner;

public class Main {
	private static StringBuilder word;
	private static String symbol = " ";
	private static String prevSymbol = " ";
	private static String vowels = "aeiouy";
	private static String[] grades = {"5","6","7","9","10","11","12","13","14","15","16","17","18","24+"};
    private static double wordCount = 0;
    private static double sentCount = 0;
	private static double charCount = 0;
	private static double score = 0;
	private static double inWordSyllCount = 0;
	private static double polySyllables = 0;
	private static double syllCounter = 0;
    
	public static void main(String[] args) throws Exception{
        if(args.length==0) System.exit(0);
		         word = new StringBuilder();
        Scanner scFile = new Scanner(new File(args[0])).useDelimiter("");
		System.out.println("The text is:");
		int c = 0;
        while(true) {
            if(scFile.hasNext()) {			
			    symbol = scFile.next();
				word.append(symbol.toLowerCase());
				System.out.print(symbol);
			    if(symbol.matches("[!.?]") & prevSymbol.matches("[^!.?]")) {
				    sentCount++;
				}
			    if(symbol.matches("\\s")){
					stats();
			    }if(symbol.matches("\\S")) {
			        charCount++;
					//System.out.print("|"+prevSymbol.toLowerCase()+ "|" + symbol.toLowerCase()+"|");
					inWordSyllCount += vowels.contains(symbol.toLowerCase()) & !vowels.contains(prevSymbol.toLowerCase()) ? 1 : 0;
			    }
			    prevSymbol = symbol;
			}else {
				if(prevSymbol.matches("[^!.?\\s]"))	
					sentCount++;
				if(prevSymbol.matches("\\S"))
					stats();			
				break;
			}
		}
		System.out.println("\n\nWords : "+(int)wordCount);
		System.out.println("Sentances : "+(int)sentCount);
		System.out.println("Characters : "+(int)charCount);
		System.out.println("Syllables : "+(int)syllCounter);
		System.out.println("Polysyllables : "+(int)polySyllables);
		System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
		switch(new Scanner(System.in).next()) {
			case("all") :
			    ariStat();
				fkStat();
				smogStat();
				clStat();
			    break;
		    case("ARI") :
			    ariStat();
				break;
		    case("FK") :
			    fkStat();
				break;
		    case("SMOG") :
			    smogStat();
				break;
		    case("CL") :
			    clStat();
				break;

		}
    }
	public static void ariStat() {
		score = 4.71*(charCount/wordCount) + 0.5*(wordCount/sentCount) - 21.43;
		System.out.printf(Locale.ENGLISH, "%nAutomated Readability Index: %1$.2f (about %2$s year olds).", score, score < grades.length ? grades[(int)Math.ceil(score)-1] : grades[grades.length-1]);
	}
	public static void fkStat() {
        score = 0.39*wordCount/sentCount + 11.8*syllCounter/wordCount - 15.59; 
		System.out.printf(Locale.ENGLISH, "%nFlesch–Kincaid readability tests: %1$.2f (about %2$s year olds).", score, score < grades.length ? grades[(int)Math.ceil(score)-1] : grades[grades.length-1]);
	}
	public static void smogStat() {
        score = 1.043*Math.sqrt(polySyllables*30/sentCount) + 3.1291;
		System.out.printf(Locale.ENGLISH, "%nSimple Measure of Gobbledygook: %1$.2f (about %2$s year olds).", score, score < grades.length ? grades[(int)Math.ceil(score)-1] : grades[grades.length-1]);
    }
	public static void clStat() {
        score = 5.88*(charCount/wordCount) - 29.6*(sentCount/wordCount) - 15.8;
		System.out.printf(Locale.ENGLISH, "%nColeman–Liau index: %1$.2f (about %2$s year olds).", score, score < grades.length ? grades[(int)Math.ceil(score)] : grades[grades.length-1]);
	}		
	public static void stats() {
        inWordSyllCount -= word.substring(word.lastIndexOf("e")+1).matches("[\\p{Punct}\\s]+") & inWordSyllCount > 1 ? 1 : 0;
		inWordSyllCount = inWordSyllCount == 0 ? 1 : inWordSyllCount;
       // System.out.println(" = "+inWordSyllCount);
		polySyllables += inWordSyllCount > 2 ? 1 : 0;
		syllCounter += inWordSyllCount;
		inWordSyllCount = 0;
		word.delete(0,word.capacity());
		prevSymbol = " ";
		//System.out.println(syllCounter);
		wordCount++;
	}
}