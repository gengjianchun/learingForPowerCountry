import java.io.*;

public class ReadCookie {
    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(new File("d:\\cookie.txt")));
        String line = reader.readLine();
        String[] kvs = line.split(";");
        String json = "";
        for(String s : kvs){
            String[] kv = s.split("=");
            json+=kv[0]+":"+kv[1]+",";
        }
        System.out.println(json);

    }
}
