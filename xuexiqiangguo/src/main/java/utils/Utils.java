package utils;

public class Utils {
    public static void waits(long time){
        try {
            Thread.sleep(time+5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
    * @Description: 得到两个字符串种相同的字的个数
    * @param: 
    * @return: 
    * @Author: gengjianchun
    * @Date: 2021-01-06
    */ 
    public static int getSameNum(String a, String b){
        int num = 0;
        for(char c : a.toCharArray()){
            if(b.indexOf(c) >=0){
                num ++;
            }
        }
        return num;
    }
}
