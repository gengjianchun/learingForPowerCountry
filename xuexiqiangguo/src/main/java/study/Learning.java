package study;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import utils.Utils;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Learning {
    public static String driverPath = "D:\\workspace\\java\\chromedriver.exe";

    //public  static String driverPath="chromedriver.exe";

    /**
     * @Description: 点击我的学习 或我的积分
     * @param: drive
     * @param: 我的学习 我的积分
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-06
     */
    public static void findMyStudyOrScore(WebDriver driver, String itemName) {
        WebElement rightBar = driver.findElement(By.className("_2sd_CKPqo9yoERxZnUP_PX"));
        List<WebElement> items = rightBar.findElements(By.className("linkItem"));
        for (WebElement item : items) {
            if (itemName.equals(item.getText())) {
                item.click();
            }
        }
    }

    /**
     * @Description: 切换到新的页面
     * @param:
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-06
     */
    public static void swichToNewTag(WebDriver driver) {
        String current = driver.getWindowHandle();
        Set<String> allHandler = driver.getWindowHandles();
        for (String handler : allHandler) {
            if (!handler.equals(current)) {
                driver.switchTo().window(handler);
            }
        }
    }

    /**
     * @Description: 跳转到新页面 关闭旧页面
     * @param:scoreHandler 要保留的页面句柄
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-06
     */
    private static void closeOtherTag(WebDriver driver, String scoreHandler) {
        Set<String> allHandler = driver.getWindowHandles();
        for (String handler : allHandler) {
            if (!handler.equals(scoreHandler)) {
                driver.switchTo().window(handler);
                driver.close();
            }
        }
        driver.switchTo().window(scoreHandler);
    }

    public static void getCookie(CountDownLatch count, CountDownLatch countFouCookie) throws IOException, InterruptedException {
        File cookieFile = new File("mycookie.txt");
        FileWriter fileWriter = new FileWriter(cookieFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);


        System.setProperty("webdriver.chrome.driver", driverPath);
        DesiredCapabilities capability = DesiredCapabilities.internetExplorer();
        WebDriver driver = new ChromeDriver();
        //driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        driver.get("https://pc.xuexi.cn/points/login.html");
        Utils.waits(20000);
//        findMyStudyOrScore(driver,"我的学习");
//        Utils.waits(5000);
//        swichToNewTag(driver);


        Set<Cookie> cookies = driver.manage().getCookies();
        for (Cookie cookie : cookies) {
            bufferedWriter.write((cookie.getName() + ";" +
                    cookie.getValue() + ";" +
                    cookie.getDomain() + ";" +
                    cookie.getPath() + ";" +
                    cookie.getExpiry() + ";" +
                    cookie.isSecure()));
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        fileWriter.close();
        countFouCookie.countDown();
        count.await();
        driver.quit();
    }


    public static WebDriver login(CountDownLatch count) {
        BufferedReader bufferedReader;
        System.setProperty("webdriver.chrome.driver", driverPath);
        DesiredCapabilities capability = DesiredCapabilities.internetExplorer();
        WebDriver webDriver = new ChromeDriver();
        //driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        webDriver.get("https://www.xuexi.cn/");


        try {
            File cookieFile = new File("mycookie.txt");
            FileReader fileReader = new FileReader(cookieFile);
            bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
                while (stringTokenizer.hasMoreTokens()) {

                    String name = stringTokenizer.nextToken();
                    String value = stringTokenizer.nextToken();
                    String domain = stringTokenizer.nextToken();
                    String path = stringTokenizer.nextToken();
                    Date expiry = null;
                    String dt;

                    if (!(dt = stringTokenizer.nextToken()).equals("null")) {
                        expiry = new Date(dt);
                    }

                    boolean isSecure = new Boolean(stringTokenizer.nextToken()).booleanValue();
                    Cookie cookie = new Cookie(name, value, domain, path, expiry, isSecure);
                    webDriver.manage().addCookie(cookie);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        webDriver.get("https://pc.xuexi.cn/points/my-points.html");
        webDriver.manage().window().maximize();
        count.countDown();
        Utils.waits(3000);
//        //进入我的积分页面
//        findMyStudyOrScore(webDriver,"我的积分");
//        //各个学习模块
//        swichToNewTag(webDriver);
        //我的积分页面句柄
        String scoreHandler = webDriver.getWindowHandle();
        closeOtherTag(webDriver, scoreHandler);
        webDriver.switchTo().window(scoreHandler);
        return webDriver;
    }

    public static void chooseAns(List<WebElement> ans, List<String> rightAnses) {

        boolean flag = false;//是否有答案被选中
        for (WebElement element : ans) {
            //单选
            String waitToChoose = element.getText().substring(2).trim();
            if (rightAnses.contains(waitToChoose)) {
                element.click();
                flag = true;
            }
        }
        if (!flag) {
            int maxnum = 0;
            int ansIndex = 0;
            for (int j = 0; j < ans.size(); j++) {
                String waitToChoose = ans.get(j).getText().replace(" ", "");
                for (int i = 0; i < rightAnses.size(); i++) {
                    int samenum = Utils.getSameNum(waitToChoose, rightAnses.get(i));
                    if (samenum > maxnum) {
                        maxnum = samenum;
                        ansIndex = j;
                    }
                }
            }

            ans.get(ansIndex).click();
        }
    }

    /**
     * @Description: 选择答案或者填空
     * @param:
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-06
     */
    private static void fillAnses(WebDriver driver) {
        //滚动滚动条 定位到提示
        String js3 = "arguments[0].scrollIntoView();";
        WebElement tipsScroll = driver.findElement(By.className("tips"));//定位目标元素
        ((JavascriptExecutor) driver).executeScript(js3, tipsScroll);

        //查看提示
        driver.findElement(By.className("tips")).click();
        WebElement tips = driver.findElement(By.className("line-feed"));
        //正确答案
        List<WebElement> righAnsElements = tips.findElements(By.tagName("font"));
        List<String> rightAnses = new ArrayList<>();
        for (WebElement element : righAnsElements) {
            rightAnses.add(element.getText());
        }
        //隐藏提示
        driver.findElement(By.className("tips")).click();

        //备选答案
        List<WebElement> ans = driver.findElements(By.className("q-answer"));


        String questionHeader = driver.findElement(By.className("q-header")).getText();
        System.out.println(questionHeader);
        if (questionHeader.contains("单选")) {
            //单选题
            chooseAns(ans, rightAnses);
        } else if (questionHeader.contains("多选")) {
            //多选题，全选
            for (WebElement a : ans) {
                a.click();
            }
        } else {
            //填空题
            List<WebElement> blanks = driver.findElements(By.className("blank"));
            for (int i = 0; i < blanks.size(); i++) {
                blanks.get(i).sendKeys(rightAnses.get(i));
            }
        }
        //确定
        driver.findElement(By.tagName("button")).click();
        //点击下一题
        WebElement nextBtn = driver.findElement(By.className("ant-btn"));
        nextBtn.click();
        //等待下一题刷新
        Utils.waits(1000);
    }

    /**
     * @Description: 调整页面滚动条到指定元素
     * @param: 元素的class
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-07
     */
    public static void scrollToElement(WebDriver driver, String className) {
        String js3 = "arguments[0].scrollIntoView();";
        WebElement tipsScroll = driver.findElement(By.className(className));//定位目标元素
        ((JavascriptExecutor) driver).executeScript(js3, tipsScroll);
    }

    /**
     * @Description: 我要读文章
     * @param:
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-06
     */
    private static void readArticle(WebDriver driver, WebElement btn) {

        btn.click();
        //切换到文章列表
        swichToNewTag(driver);
        //文章列表页签
        String articlesWindow = driver.getWindowHandle();
        closeOtherTag(driver, articlesWindow);
        scrollToElement(driver, "text-wrap");
        List<WebElement> articles = driver.findElements(By.className("text-wrap"));
        for (int i = 0; i < 12 && i < articles.size(); i++) {
            articles.get(i).click();
            //切换到文章页面
            swichToNewTag(driver);
            Utils.waits(2000);
            long now = System.currentTimeMillis();
            //随机上下移动
            while (System.currentTimeMillis() - now < 120 * 1000) {
                scollRandomly(driver);
                Utils.waits(1000);
            }
            driver.close();
            driver.switchTo().window(articlesWindow);
        }
    }

    /**
     * @Description: 随机滚动
     * @param:
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-07
     */
    private static void scollRandomly(WebDriver driver) {
        Random r = new Random();
        int i = r.nextInt(100);
        if (i % 2 == 0) {
            i = -300;
        } else {
            i = 300;
        }
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + i + ")");
    }

    /**
     * @Description: 视听学习
     * @param:
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-06
     */
    private static void ListenAndView(WebDriver driver, WebElement btn) {

        driver.get("https://www.xuexi.cn/a191dbc3067d516c3e2e17e2e08953d6/b87d700beee2c44826a9202c75d18c85.html");
        String videoList = driver.getWindowHandle();
        closeOtherTag(driver, videoList);
        Utils.waits(3000);
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + 300 + ")");
        List<WebElement> videos = driver.findElements(By.className("text-link-item-title"));
        for (int i = 0; i < 7 && i < videos.size(); i++) {
            videos.get(i).click();
            for (int j = 0; j < 14 * 2; j++) {
                scrollToElement(driver, "menu-list");
                Utils.waits(1000);
                scrollToElement(driver, "copyright");
            }
            driver.quit();
            driver = login(new CountDownLatch(1));
            driver.get("https://www.xuexi.cn/a191dbc3067d516c3e2e17e2e08953d6/b87d700beee2c44826a9202c75d18c85.html");
            Utils.waits(1000);
            videoList = driver.getWindowHandle();
            closeOtherTag(driver, videoList);
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + 300 + ")");
            videos = driver.findElements(By.className("text-link-item-title"));
        }
        driver.close();
    }

    /**
     * @Description: 视听学习时长
     * @param:
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-06
     */
    private static void ListenAndViewTime(WebDriver driver, WebElement btn) {
        ListenAndView(driver, btn);
    }

    /**
     * @Description: 每日答题
     * @param:
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-06
     */
    private static void AnswerQuestion(WebDriver driver, WebElement btn) {
        btn.click();
        Utils.waits(2000);


        for (int ii = 0; ii < 5; ii++) {
            fillAnses(driver);
        }
        //返回我的学习页面
        List<WebElement> aTag = driver.findElement(By.className("ant-breadcrumb")).findElements(By.tagName("a"));
        for (WebElement tag : aTag) {
            if (tag.getText().equals("我的学习")) {
                tag.click();
            }
        }

    }


    /**
     * @Description: 每周答题
     * @param:
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-06
     */
    private static void AnswerWeekly(WebDriver driver, WebElement btn) {
        btn.click();
        Utils.waits(2000);
        List<WebElement> ansBtns = driver.findElements(By.className("ant-btn"));
        for (WebElement ansbtn : ansBtns) {
            if ("开始答题".equals(ansbtn.getText()) || "重新答题".equals(ansbtn.getText())) {
                ansbtn.click();
                System.out.println("打开答题页面");
                break;
            }
        }
        //等待页面加载
        Utils.waits(5000);
        for (int i = 0; i < 5; i++) {
            fillAnses(driver);
        }
        //返回我的学习页面
        List<WebElement> aTag = driver.findElement(By.className("ant-breadcrumb")).findElements(By.tagName("a"));
        for (WebElement tag : aTag) {
            if (tag.getText().equals("我的学习")) {
                tag.click();
            }
        }

    }

    /**
     * @Description: 专项答题
     * @param:
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-06
     */
    private static void spacialAnswer(WebDriver driver, WebElement btn) {
        btn.click();
        Utils.waits(2000);
        List<WebElement> ansBtns = driver.findElements(By.className("ant-btn"));
        for (WebElement ansbtn : ansBtns) {
            if ("开始答题".equals(ansbtn.getText()) || "继续答题".equals(ansbtn.getText())) {
                ansbtn.click();
                System.out.println("打开答题页面");
                break;
            } else {
                return;
            }
        }
        //等待页面加载
        Utils.waits(5000);


        for (int i = 0; i < 50; i++) {
            fillAnses(driver);
            List<WebElement> buttons = driver.findElements(By.tagName("button"));
            if (buttons.size() > 1) {
                buttons.get(0).click();
                buttons.get(1).click();
            }
        }
        //返回我的学习页面
        List<WebElement> aTag = driver.findElement(By.className("ant-breadcrumb")).findElements(By.tagName("a"));
        for (WebElement tag : aTag) {
            if (tag.getText().equals("我的学习")) {
                tag.click();
            }
        }
    }


    /**
     * @Description: 学习内容
     * @param:
     * @return:
     * @Author: gengjianchun
     * @Date: 2021-01-06
     */
    public static void startStudy(WebDriver driver) {
        //我的学习页面
        String scoreHandler = driver.getWindowHandle();
        List<WebElement> cards = driver.findElements(By.className("my-points-card"));
        for (int i = 0; i < cards.size(); i++) {
            cards = driver.findElements(By.className("my-points-card"));
            String cardTitle = cards.get(i).findElement(By.className("my-points-card-title")).getText();
            WebElement btn = cards.get(i).findElement(By.className("big"));
            String btnText = btn.getText();
            if ("已完成".equals(btnText)) {
                continue;
            }
            System.out.println(cardTitle);
            switch (cardTitle) {
                case "我要选读文章":
                    System.out.println("我要选读文章 readarticle");
                    readArticle(driver, btn);
                    break;
                case "视听学习":
                    System.out.println("视听学习 listenandview");
                    ListenAndView(driver, btn);
                    break;
                case "视听学习时长":
                    System.out.println("视听学习时长 listenandview time");
                    ListenAndViewTime(driver, btn);
                    break;
                case "每日答题":
                    System.out.println("开始每日答题 ansdayly");
                    AnswerQuestion(driver, btn);
                    break;
                case "每周答题":
                    System.out.println("开始每周答题 answeekly");
                    AnswerWeekly(driver, btn);
                    break;
                case "专项答题":
                    System.out.println("开始专项答题 spacialAns");
                    spacialAnswer(driver, btn);
                    break;
                default:
                    System.out.println("没有学习模块 no study");
                    // System.exit(0);

            }
            driver.quit();
            driver = login(new CountDownLatch(1));
            driver.get("https://pc.xuexi.cn/points/my-points.html");
            Utils.waits(3000);


        }
        driver.quit();
    }



}
