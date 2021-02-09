import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static void main(String [] args) {
        try {
            FileReader reader = new FileReader("sites.txt");
            FileWriter writer = new FileWriter("./files/index.txt", false);
            Scanner scan = new Scanner(reader);
            int i = 0;
            while(scan.hasNext()){
                String line = scan.nextLine();
                getDataAndWriteFile(i, line);
                writer.write(i + " " + line);
                writer.append("\n");
                i++;
            }
            reader.close();
            writer.flush();
            writer.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    public static void getUrls(){
        try {
            Document doc = Jsoup.connect("https://ru.wikipedia.org/wiki/%D0%92%D0%B8%D0%BA%D0%B8%D0%BF%D0%B5%D0%B4%D0%B8%D1%8F:%D0%98%D0%B7%D0%B1%D1%80%D0%B0%D0%BD%D0%BD%D1%8B%D0%B5_%D1%81%D1%82%D0%B0%D1%82%D1%8C%D0%B8").get();
            String text = String.valueOf(Jsoup.parse(doc.html()));
            Matcher matcher = Pattern.compile("\\\"/wiki/[a-zA-Zа-яА-Я%0-9]*\\\" ").matcher(text);
            while (matcher.find()) {
                System.out.println("https://ru.wikipedia.org/"+text.substring(matcher.start()+1,matcher.end()-2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void getDataAndWriteFile(int index, String url){
        try {
            // получить текст страницы
            Document doc = Jsoup.connect(url).get();
            // записать в текст
            try(FileWriter writer = new FileWriter("./files/выкачка "+index+".txt", false)) {
                // запись всей строки
                writer.write(Jsoup.parse(doc.text()).text());
                // запись по символам
                writer.append('\n');
                writer.flush();
                writer.close();
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}