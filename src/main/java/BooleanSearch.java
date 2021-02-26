import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BooleanSearch {
    static Lemmer lemmer = VectorSearch.lemmer;
    public static LinkedList<Integer> getCorrectPages(String searchCommand){
        // Закидываем index total в карту
        Map<String, LinkedList<Integer>> tokensUsages = readIndex("Tasks/task3/index total.txt");

        //результат запроса
        LinkedList<Integer> result = new LinkedList<>();

        // ToDo Скобки

        // разбиваем запрос на слова
        String[] words = searchCommand.split("\\s");

        // отдельно считываем первое слово, оно может либо токеном, либо NOT, выбираем соответствующее множество страниц
        int i = 0;
        if (words.length > 0) {
            if (tokensUsages.containsKey(lemmer.getLemmitizedWord(words[i]))) {
                result = tokensUsages.get(lemmer.getLemmitizedWord(words[i]));

            } else if(words[0].equals("not")) {
                i++;
                result = operationNOT(tokensUsages.get(lemmer.getLemmitizedWord(words[i])));
            }
            i++;
        }



        // после токена мы всегда ждем AND, OR или конец запроса. После AND и OR всегда ждем токен или NOT
        while (i < words.length) {
            if (words[i].equals("and")) {
                i++;
                if (words[i].equals("not")) {
                    i++;
                    operationNOT(tokensUsages.get(words[i]));
                }
                result = operationAND(result, tokensUsages.get(lemmer.getLemmitizedWord(words[i])));
            } else
            if (words[i].equals("or")) {
                i++;
                if (words[i].equals("not")) {
                    i++;
                    operationNOT(tokensUsages.get(words[i]));
                }
                result = operationOR(result, tokensUsages.get(lemmer.getLemmitizedWord(words[i])));
            }
            i++;
        }
        return result;
    }


    public static LinkedList<Integer> operationAND(LinkedList<Integer> list1, LinkedList<Integer> list2) {
        LinkedList<Integer> resultList = new LinkedList<>();
        while (!list1.isEmpty() & !list2.isEmpty()) {
            if (list1.getFirst() < list2.getFirst()) {
                list1.removeFirst();
            } else
            if (list1.getFirst() > list2.getFirst()) {
                list2.removeFirst();
            } else
            if (list1.getFirst().equals(list2.getFirst())) {
                resultList.add(list1.removeFirst());
                list2.removeFirst();
            }
        }
        return resultList;
    }

    public static LinkedList<Integer> operationOR(LinkedList<Integer> list1, LinkedList<Integer> list2) {
        LinkedList<Integer> resultList = new LinkedList<>();
        while (!list1.isEmpty() & !list2.isEmpty()) {
            if (list1.getFirst() < list2.getFirst()) {
                resultList.add(list1.removeFirst());
            } else
            if (list1.getFirst() > list2.getFirst()) {
                resultList.add(list2.removeFirst());
            } else
            if (list1.getFirst().equals(list2.getFirst())) {
                resultList.add(list1.removeFirst());
                list2.removeFirst();
            }
        }
        while (!list1.isEmpty()) {
            resultList.add(list1.removeFirst());
        }
        while (!list1.isEmpty()) {
            resultList.add(list2.removeFirst());
        }
        return resultList;
    }

    public static LinkedList<Integer> operationNOT(LinkedList<Integer> notList) {
        LinkedList<Integer> resultList = new LinkedList<>();
        for (int i = 0; i < 250; i++) {
            resultList.add(i);
        }
        while (!notList.isEmpty()) {
            resultList.remove(notList.removeFirst());
        }
        return resultList;
    }

    public static Map<String, LinkedList<Integer>> readIndex(String indexPath) {
        Map<String, LinkedList<Integer>> tokensUsages= new HashMap<>();

        try(BufferedReader br = new BufferedReader(new FileReader(indexPath))) {
            //чтение построчно
            String s;
            while((s=br.readLine())!=null)
            {
                String[] words = s.split("\\s");
                String a = words[0];
                LinkedList<Integer> b = new LinkedList<>();
                for(int i = 1; i < words.length; i++) {
                    b.add(Integer.valueOf(words[i]));
                }
                tokensUsages.put(a,b);
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        return tokensUsages;
    }
}
