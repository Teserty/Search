import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class VectorSearch {
    static Lemmer lemmer = new Lemmer();
    static HashMap<Integer, String> index = new HashMap<>();
    public static void setIndex(){
        try {
            FileReader reader = new FileReader("./files/index.txt");
            Scanner scan = new Scanner(reader);
            while (scan.hasNext()){
                String line = scan.nextLine();
                index.put(Integer.parseInt(line.split(" ")[0]), line.split(" ")[1]);
            }
        }catch (FileNotFoundException e){
            System.out.print(e);
        }
    }
    public static void main(String[] args) {
        //Получается, при запросе мы считаем сумму idf слов запроса,
        // а потом для всех страниц, отобранных булевым поиском считаем сходство с запросом
        // Закидываем index total в карту
        Scanner sc = new Scanner(System.in);
        setIndex();
        String searchCommand = " ";
        while(!searchCommand.equals("exit")) {
            searchCommand = sc.nextLine().toLowerCase();
//            List<String> keyWordsList = new ArrayList<>();
            Map<String, Double> keyWordsTF = lemmer.getLemmitizedWord(getKeyWordsTFFromCommand(searchCommand));
            // расчитываем длину запроса
            Double commandLength = 0.0;
            for (String keyWord : keyWordsTF.keySet()) {
                double a = keyWordsTF.get(keyWord);
                commandLength += a * a;
            }
            commandLength = Math.sqrt(commandLength);

            Set<String> keyWordsSet = keyWordsTF.keySet();
            Map<String, List<Double>> keyWordsData = readKeyWordsData("Tasks/task4/TF_IDF_revised.txt", keyWordsSet);
            //System.out.println(keyWordsData.size() + " слов всего");
            Map<String, Double> keyWordsTFIDF = getKeyWordsTFIDFFromTF(keyWordsTF, keyWordsData);


            // булевым поиском получаем список документов
            List<Integer> correctPages = BooleanSearch.getCorrectPages(searchCommand);
            Map<Integer, Double> pagesSimilarity = new HashMap<>();
            for (Integer i : correctPages) {
                // считаем длину документа
                Double documentLength = 0.0;
                for (String keyWord : keyWordsData.keySet()) {
                    double a = keyWordsData.get(keyWord).get(i+1);
                    documentLength += a * a;
                }
                documentLength = Math.sqrt(documentLength);
                // считаем косинусное сходство по последней формуле
                double a = 0;
                for (String keyWord : keyWordsData.keySet()) {
                    a += keyWordsData.get(keyWord).get(i+1) * keyWordsTFIDF.get(keyWord);
                }
                Double similarity = a / (documentLength * commandLength);

                pagesSimilarity.put(i, similarity);
            }
            HashMap<Double, List<Integer>> simil = new HashMap<>();
            System.out.print(simil.size());
            for(Integer s: pagesSimilarity.keySet()){
                if (simil.get(pagesSimilarity.get(s))==null) {
                    simil.put(pagesSimilarity.get(s), Collections.singletonList(s));
                }else{
                    List<Integer> list = new LinkedList<>(simil.get(pagesSimilarity.get(s)));
                    list.add(s);
                    simil.put(pagesSimilarity.get(s), list);
                }
            }
            List<Double> keys = new ArrayList<>(List.copyOf(simil.keySet()));
            Collections.sort(keys);
            System.out.print("Найдено:");
            for (int i = keys.size()-1; i>=0; i--){
                for (Integer y: simil.get(keys.get(i)))
                    System.out.println(index.get(y));
                //System.out.println(keys.get(i) + " " + simil.get(keys.get(i)));
            }
        }
    }


    public static Map<String, Double> getKeyWordsTFFromCommand(String searchCommand) {
        Map<String, Double> keyWords = new HashMap<>();
        String[] words = searchCommand.split("\\s");
        int k = words.length;
//        пересчитать длину без команд
        for (int i = 0; i < words.length; i++) {
            if (!(words[i].equals("AND")|words[i].equals("OR"))) {
                Double a = 1.0;
                if (words[i].equals("NOT")) {
                    i++;
                    a = -a;
                    k--;
                }
                if (keyWords.containsKey(words[i])) {
                    keyWords.replace(words[i], keyWords.get(words[i]) + a);
                } else {
                    keyWords.put(words[i], a);
                }
            } else {
                k--;
            }
        }
        for (String i: keyWords.keySet()) {
            keyWords.replace(i, keyWords.get(i)/k);
        }
        return keyWords;
    }

    public static Map<String, Double> getKeyWordsTFIDFFromTF(Map<String, Double> keyWordsTF,
                                                             Map<String, List<Double>> keyWordsData) {
        Map<String, Double> result = new HashMap<>();
        for (String i: keyWordsTF.keySet()) {
            result.put(i, keyWordsTF.get(i)*keyWordsData.get(i).get(0));
        }
        return result;
    }

    public static Map<String, List<Double>> readKeyWordsData(String indexPath, Set<String> keyWords) {
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
