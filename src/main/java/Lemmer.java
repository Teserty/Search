import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Lemmer {
    private HashMap<String, String> words;
    public Lemmer(){
        words = new HashMap<>();
        try {
            FileReader reader = new FileReader("./Tasks/task2/lemmer total.txt");
            Scanner scan = new Scanner(reader);
            while (scan.hasNext()) {
                String line = scan.nextLine();
                String [] word_a = line.split(" ");
                for (int i = 1; i < word_a.length; i++){
                    words.put(word_a[i], word_a[0]);
                }
            }
        }catch (FileNotFoundException e){
            System.out.print(e);
        }
    }
    public String getLemmitizedWord(String word){
        return words.get(word);
    }
    public HashMap<String, Double> getLemmitizedWord(Map<String, Double> wordlist){
        HashMap<String, Double> rwords = new HashMap<>();
        for(String key: wordlist.keySet()){
            rwords.put(words.get(key), wordlist.get(key));
        }
        return rwords;
    }
    @Test
    public void testGetLemmitizedWord(){
        HashMap<String, Double> a = new HashMap<>();
        a.put("AND", 0.0);
        System.out.print(getLemmitizedWord(a).keySet().contains("AND"));
    }
}
