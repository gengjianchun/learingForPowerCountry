import org.openqa.selenium.WebDriver;
import study.Learning;
import utils.Utils;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Starter {

    public static void main(String[] args) throws IOException, InterruptedException {

        CountDownLatch count = new CountDownLatch(1);
        CountDownLatch countForCookie = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Learning.getCookie(count, countForCookie);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        countForCookie.await();
        WebDriver driver = Learning.login(count);
        Utils.waits(5000);
        Learning.startStudy(driver);
    }
}
