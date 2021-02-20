import org.junit.*;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import static javatests.TestSupport.assertEquals;

public class VectorSearch {
    static Lemmer lemmer = new Lemmer();
    public static void main(String[] args) {
        //Получается, при запросе мы считаем сумму idf слов запроса,
        // а потом для всех страниц, отобранных булевым поиском считаем сходство с запросом
        // Закидываем index total в карту

        String searchCommand = "удобный OR навсегда";
        // ToDo - лемманизировать все слова запроса
        lemmer.getLemmitizedWord(searchCommand.split(" ")[0]);
        List<String> keyWordsList = new ArrayList<>();
        Map<String, Double> keyWordsTF = getKeyWordsTFFromCommand(searchCommand, keyWordsList);

        // расчитываем длину запроса
        Double commandLength = 0.0;
        for (String keyWord:keyWordsTF.keySet()) {
            double a = keyWordsTF.get(keyWord);
            commandLength += a*a;
        }
        commandLength = Math.sqrt(commandLength);

        Map<String, List<Double>> keyWordsData = readKeyWordsData("resources/sladost.txt", keyWordsList);
        //System.out.println(keyWordsData.size() + " слов всего");

        // булевым поиском получаем список документов
        // ToDo положи булевый поиск в тот же пакет
        List<Integer> correctPages = BooleanSearch.getCorrectPages(searchCommand);

        // для каждого документа считаем tf-idf, длину, схожесть с запросом. Сохраняем в курту слово-схожесть

        Map<Integer, Double> pagesSimilarity = new HashMap<>();
        for (Integer i : correctPages) {
            // считаем длину документа
            Double documentLength = 0.0;
            for (String keyWord:keyWordsData.keySet()) {
                double a = keyWordsData.get(keyWord).get(i);
                documentLength += a*a;
            }
            documentLength = Math.sqrt(documentLength);
            // считаем косинусное сходство по последней формуле
            double a = 0;
            for (String keyWord:keyWordsData.keySet()) {
                a += keyWordsData.get(keyWord).get(i)*keyWordsTF.get(keyWord);
            }
            Double similarity = a/(documentLength*commandLength);

            pagesSimilarity.put(i,similarity);
        }
        // ToDo отсортировать этот список страниц по степени схожести
        List<Integer> keys = new ArrayList<>(List.copyOf(pagesSimilarity.keySet()));
        Collections.sort(keys);
        System.out.println(pagesSimilarity.toString());
    }

    // это изначально был метод для keyWordsList'a, но больно уж захотелось и рыбку съесть и всего остального
    public static Map<String, Double> getKeyWordsTFFromCommand(String searchCommand, List<String> keyWordsList) {
        Map<String, Double> keyWords = new HashMap<>();
        String[] words = searchCommand.split("\\s");
//        for (String word: words) {
        for (int i = 0; i < words.length; i++) {
            if (!(words[i].equals("AND")|words[i].equals("OR"))) {
                Double a = 1.0 / words.length;
                if (words[i].equals("NOT")) {
                    i++;
                    a = -a;
                }
                if (keyWords.containsKey(words[i])) {
                    keyWords.replace(words[i], keyWords.get(words[i]) + a);
                } else {
                    keyWords.put(words[i], a);
                    keyWordsList.add(words[i]);
                }

            }
        }
        return keyWords;
    }

    public static Map<String, List<Double>> readKeyWordsData(String indexPath, List<String> keyWords) {
        Map<String, List<Double>> keyWordsData = new HashMap<>();

        try(BufferedReader br = new BufferedReader(new FileReader(indexPath))) {
            String s;
            while (!keyWords.isEmpty() & (s=br.readLine())!=null) {
                String[] words = s.split("\\s");
                String a = words[0];
                for (String keyWord: keyWords) {
                    if (keyWord.equals(a)) {
                        List<Double> b = new ArrayList<>();
                        for(int i = 1; i < words.length; i++) {
                            b.add(Double.valueOf(words[i]));
                        }
                        keyWordsData.put(a, b);
                        keyWords.remove(keyWord);
                    }
                }
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        return keyWordsData;
    }
}
