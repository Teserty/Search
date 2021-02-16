import com.google.inject.internal.util.Sets;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class TokensAndLemmas {
    public static void main(String [] args){
        ReadAllDocks();
    }
    public static void ReadAllDocks(){
        List<String> list = new LinkedList<>();
        String [] total = new String[250];
        for(int i = 0; i< 250; i++){
            try {
                FileReader reader = new FileReader("./files/выкачка "+i+".txt");
                Scanner sc = new Scanner(reader);
                while (sc.hasNext()){
                    total[i] += sc.nextLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        for(int i = 0; i< 250; i++) {
            StringTokenizer st = new StringTokenizer(total[i], " \t\n\r,.");
            while (st.hasMoreTokens()) {
                list.add(st.nextToken());
            }
        }
        Set<String> set = new HashSet<>(list);
        System.out.println(set.size());
    }
}
