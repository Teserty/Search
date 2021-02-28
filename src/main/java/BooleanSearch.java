import org.junit.Assert;
import org.junit.Test;

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
                System.out.print(words[i] + " ");
                i++;
                if (words[i].equals("not")) {
                    System.out.print(words[i] + " ");
                    i++;
                    result = operationAND(result, operationNOT(tokensUsages.get(lemmer.getLemmitizedWord(words[i]))));
                } else {
                    result = operationAND(result, tokensUsages.get(lemmer.getLemmitizedWord(words[i])));
                }
            } else
            if (words[i].equals("or")) {
                System.out.print(words[i] + " ");
                i++;
                if (words[i].equals("not")) {
                    System.out.print(words[i] + " ");
                    i++;
                    result = operationOR(result, operationNOT(tokensUsages.get(lemmer.getLemmitizedWord(words[i]))));
                } else {
                    result = operationOR(result, tokensUsages.get(lemmer.getLemmitizedWord(words[i])));
                }
            }
            System.out.print(words[i] + " ");
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
    public static boolean isCommandCorrect(String searchCommand) {
        String[] words = searchCommand.split("\\s");
        int i = 0;
        if (words.length > 0) {
            if(words[0].equals("not")) {
                i++;
            }
            i++;
        }
        while (i < words.length) {
            if (words[i].equals("and")) {
                i++;
                if (words[i].equals("not")) {
                    i++;
                }
            } else
            if (words[i].equals("or")) {
                i++;
                if (words[i].equals("not")) {
                    i++;
                }
            } else {
                return false;
            }
            i++;
        }
        if (i == words.length) {
            return true;
        }
        return false;
    }
    @Test
    public void testOperationAND(){
        LinkedList<Integer> resultList = new LinkedList<>();
        for (int i = 0; i < 250; i++) {
            resultList.add(i);
        }
        LinkedList<Integer> list1 = new LinkedList<>();
        LinkedList<Integer> list2 = new LinkedList<>();
        list1.add(5);
        list1.add(8);
        list2.add(9);
        list2.add(20);

        if (!(list1.containsAll(resultList))
                & list2.containsAll(resultList)) {
            System.out.println("operationAND works wrong");
        }
    }
    @Test
    public void testOperationOR(){
        LinkedList<Integer> resultList = new LinkedList<>();
        for (int i = 0; i < 250; i++) {
            resultList.add(i);
        }
        LinkedList<Integer> list1 = new LinkedList<>();
        LinkedList<Integer> list2 = new LinkedList<>();
        list1.add(5);
        list1.add(8);
        list2.add(9);
        list2.add(20);

        if (!(resultList.containsAll(list1))
                & resultList.containsAll(list2)) {
            System.out.println("operationOR works wrong");
        }
    }
    @Test
    public void testOperationNOT(){
        LinkedList<Integer> resultList = new LinkedList<>();
        for (int i = 0; i < 250; i++) {
            resultList.add(i);
        }
        LinkedList<Integer> list = new LinkedList<>();
        list.add(5);
        list.add(8);
        list.add(9);
        list.add(20);

        if (!(resultList.containsAll(operationOR(list, operationNOT(list)))
                & operationOR(list, operationNOT(list)).containsAll(resultList))) {
            System.out.println("operationNOT works wrong");
        }
    }
    @Test
    public void testIsCommandCorrect(){
        String indexPath = "files/GoodCommands.txt";
        try(BufferedReader br = new BufferedReader(new FileReader(indexPath))) {
            String s;
            while((s=br.readLine())!=null) {
                if (!isCommandCorrect(s)) {
                    System.out.println("False negative on line " +s);
                    Assert.assertTrue(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        indexPath = "files/GoodCommands.txt";
        try(BufferedReader br = new BufferedReader(new FileReader(indexPath))) {
            String s;
            while((s=br.readLine())!=null) {
                if (isCommandCorrect(s)) {
                    System.out.println("False positive on line " +s);
                    Assert.assertTrue(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testReadIndex(){
        String indexPath = "Tasks/task3/index total.txt";
        Map<String, LinkedList<Integer>> index = readIndex(indexPath);
        try(BufferedReader br = new BufferedReader(new FileReader(indexPath))) {
            //чтение построчно
            String s;
            while((s=br.readLine())!=null)
            {
                String[] words = s.split("\\s");
                if (index.containsKey(words[0])) {
                    LinkedList<Integer> a = index.get(words[0]);
                    for(int i = 1; i < words.length; i++) {
                        if (!a.contains(Integer.valueOf(words[i]))) {
                            System.out.println("Didn't find usage of word "+ words[0] + " in page "+words[i]+ " in the index");
                            Assert.assertTrue(false);
                        }
                    }
                } else {
                    System.out.println("Didn't find word "+ words[0] + " in the index");
                    Assert.assertTrue(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
