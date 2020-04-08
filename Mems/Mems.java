package Mems;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
 
public class Mems {
    private static ArrayList<String> logger = new ArrayList<>();
    private static LinkedHashMap<String,String> cards = new LinkedHashMap<>();
    private static LinkedHashMap<String, Integer> mistakes = new LinkedHashMap<>();
    private static Scanner scnr = new Scanner(System.in).useDelimiter(System.lineSeparator());
 
    public static void main(String[] args) {
        while(true) {
            CrazyPrinter("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String action = Log(scnr.next());
            if( action.toLowerCase().equals("add")) Add();
            if( action.toLowerCase().equals("remove")) Remove();
            if( action.toLowerCase().equals("import")) Import();
            if( action.toLowerCase().equals("export")) Export();
            if( action.toLowerCase().equals("ask")) Ask();
            if( action.toLowerCase().equals("exit")) {
                CrazyPrinter("Bye bye!");
                break;
            }
            if( action.toLowerCase().equals("log")) Log();
            if( action.toLowerCase().equals("hardest card")) HardestCard();
            if( action.toLowerCase().equals("mistakes")) Mistakes();
            if( action.toLowerCase().equals("reset stats")) ResetStats();
            CrazyPrinter("");
        }
    }
    private static void ResetStats() {
        mistakes.clear();
        CrazyPrinter("Card statistics has been reset.");
    }
    private static void Mistakes() {
        mistakes.entrySet().stream().forEach(System.out::println);
    }
    private static void HardestCard() {
        final Integer maxMistakes = !mistakes.isEmpty() ? mistakes.values().stream().reduce(0,(prev,next) -> prev <= next ? next : prev) : 0;
 
        if(!maxMistakes.equals(0)) {
            String hardestCard = mistakes.entrySet().stream().reduce("",
                    (str, entry) -> !entry.getValue().equals(maxMistakes) ? str : !str.equals("") ? str + ", " + entry.getKey() : entry.getKey(),
                    (str1, str2) -> str1 + ", " + str2);
            String toBe = hardestCard.split(",").length > 1 ? "are" : "is";
            CrazyPrinter("The hardest card " + toBe + " \"" + hardestCard + "\". You have \"" + maxMistakes + "\" errors answering it.");
 
        }else {
            CrazyPrinter("There are no cards with errors.");
        }
    }
    private static String Log(String str) {
        logger.add(str+"\n");
        return str;
    }
    private static int Log(int num) {
        logger.add(String.valueOf(num)+"\n");
        return num;
    }
    private static void Log() {
        CrazyPrinter("File name:");
        try(PrintWriter pw = new PrintWriter(new File(Log(scnr.next())))) {
            for(String str : logger) {
                pw.print(str);
            }
        } catch (FileNotFoundException fnfe) {
            CrazyPrinter("File not found.");
        }
        CrazyPrinter("The log has been saved.");
    }
    private static void CrazyPrinter(String str) {
        System.out.println(str);
        Log(str);
    }
    private static void Export() {
        int counter = 0;
        CrazyPrinter("File name:");
        try(PrintWriter pw = new PrintWriter(new File(Log(scnr.next())))) {
            for(Map.Entry<String, String> entry : cards.entrySet()) {
                pw.println(entry.getKey());
                pw.println(entry.getValue());
                pw.println(mistakes.getOrDefault(entry.getKey(),0));
                counter++;
            }
        } catch (FileNotFoundException fnfe) {
            CrazyPrinter("File not found.");
        }
        CrazyPrinter(counter+" cards have been saved.");
    }
    private static void Import() {
        String term = "";
        String definition = "";
        int counter = 0;
        CrazyPrinter("File name:");
        try(Scanner sc = new Scanner(new File(Log(scnr.next()))).useDelimiter(System.lineSeparator())) {
            while(true) {
                final Integer mistakeCntr;
                if(sc.hasNext()) term = sc.next();
                if(sc.hasNext()) definition = sc.next();
                if(sc.hasNextInt()) mistakeCntr = sc.nextInt();
                else break;
                cards.putIfAbsent(term, definition);
                cards.replace(term, definition);
/*              mistakes.computeIfPresent(term, (k,v) -> v+mistakeCntr);
                mistakes.putIfAbsent(term, mistakeCntr);*/
                mistakes.put(term,mistakeCntr);
                counter++;
            }
        } catch (FileNotFoundException fnfe) {
            CrazyPrinter("File not found.");
 
        }
        if(counter != 0) CrazyPrinter(counter+" cards have been loaded.");
 
    }
    private static void Remove() {
        CrazyPrinter("The card:");
        String card = Log(scnr.next());
        if(cards.remove(card) != null) {
            mistakes.remove(card);
            CrazyPrinter("The card has been removed.");
        }else {
            CrazyPrinter("Can't remove \"" + card + "\": there is no such card.");
        }
    }
 
    private static boolean Ask() {
        CrazyPrinter("How many times to ask?");
        int times = 0;
        if(!scnr.hasNextInt()) {
            times = 1;
            Log(scnr.next());
        }
        else
            times = Log(scnr.nextInt());
        times = cards.size() == 0 ? 0 : times;
        for(int i = 0; i < times; i++) {
            String card = cards.size() > 1 ? (String) cards.keySet().toArray()[new Random().nextInt(cards.size()-1)] : (String) cards.keySet().toArray()[0];
            String definition = cards.get(card);
            String answer;
            String wrongKey;
 
            CrazyPrinter("Print the definition of \""+card+"\":");
            answer = Log(scnr.next());
            if(definition.toLowerCase().equals(answer.toLowerCase())) {
                CrazyPrinter("Correct answer.");
            } else {
                if ((wrongKey = (String) getKeyByValue(cards, answer)) != null)
                    CrazyPrinter("Wrong answer. The correct one is \"" + definition + "\", you've just written the definition of \"" + wrongKey + "\".");
                else
                    CrazyPrinter("Wrong answer. The correct one is \"" + definition + "\".");
                mistakes.computeIfPresent(card, (k, v) -> v + 1);
                mistakes.putIfAbsent(card, 1);
            }
 
        }
        return true;
    }
    private static boolean Add() {
        CrazyPrinter("The card:");
        String term = Log(scnr.next());
        if(cards.containsKey(term)) {
            CrazyPrinter("The card \""+term+"\" already exists.");
            return false;
        } else {
            CrazyPrinter("The definition of the card:");
            String definition = Log(scnr.next());
            if(getKeyByValue(cards, definition) != null) {
                CrazyPrinter("The definition \""+definition+"\" already exists.");
                return false;
            }
            cards.put(term,definition);
            CrazyPrinter("The pair (\""+term+"\":\""+definition+"\") has been added.");
            return true;
        }
 
    }
    public static <M extends Map, V> Object getKeyByValue(Map<?, ?> map, V value) {
        Object result = null;
        if(map.containsValue(value)) {
            for(Map.Entry<?, ?> entry : map.entrySet()) {
                if(value.equals(entry.getValue()))
                    result = entry.getKey();
            }
        }
        return result;
    }
}