import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MainPage {
    public WebDriver driver = new ChromeDriver();

    @BeforeSuite public void beforeSuite(){
        driver.get("https://takvim.firat.edu.tr/");
        driver.manage().window().maximize();
    }

    @BeforeMethod public void toMain(){
        driver.get("https://takvim.firat.edu.tr/");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @Test public void isTakvimlerVisible() throws InterruptedException {
        driver.findElement(By.xpath("//div[@class='calendars-icon']")).click();
        Assert.assertEquals(driver.findElement(By.id("calendars-modal")).getAttribute("class"),"active");
        Thread.sleep(1000);

        driver.findElement(By.xpath("//i[@class='fas far fa-times-circle']")).click();
        Assert.assertEquals(driver.findElement(By.id("calendars-modal")).getAttribute("class"),"");
        Thread.sleep(1000);
    }

    @Test public void Ay_Ajanda(){
        driver.findElement(By.xpath("//button[@title='Ay']")).click();
        Assert.assertEquals(driver.findElement(By.id("fc-dom-1")).getText(),"Eylül 2022");

        driver.findElement(By.xpath("//button[@title='Ajanda']")).click();
        Assert.assertEquals(driver.findElement(By.id("fc-dom-1")).getText(),"2022");

        driver.findElement(By.xpath("//button[@title='Ay']")).click();
        Assert.assertEquals(driver.findElement(By.id("fc-dom-1")).getText(),"Eylül 2022");
    }

    @Test public void leftPanel(){
        List<WebElement> filters = driver.findElements(By.xpath("//div[@class='filter']//input"));
        for (WebElement e: filters) {
            e.click();
            Assert.assertFalse(e.isSelected());
        }

        for (WebElement e:filters) {
            e.click();
            Assert.assertTrue(e.isSelected());
        }
    }

    @Test public void closeAll(){
        driver.findElement(By.xpath("//div[@class='all-close']")).click();

        List<WebElement> filters = driver.findElements(By.xpath("//div[@class='filter']//input"));
        for (WebElement e: filters) {
            Assert.assertFalse(e.isSelected());
        }
    }

    @Test public void openAll(){
        driver.findElement(By.xpath("//div[@class='all-open']")).click();

        List<WebElement> filters = driver.findElements(By.xpath("//div[@class='filter']//input"));
        for (WebElement e: filters) {
            Assert.assertTrue(e.isSelected());
        }
    }

    @Test public void exportToGoogle(){
        driver.findElement(By.id("open-load-modal")).click();
        Assert.assertEquals(driver.getCurrentUrl(),"https://accounts.google.com/o/oauth2/auth/identifier?response_type=code&access_type=online&client_id=219246089931-4iq7vi7hej4ia5ejk16irgc48ppghf6d.apps.googleusercontent.com&redirect_uri=https%3A%2F%2Ftakvim.firat.edu.tr%2Fcallback&state&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcalendar&approval_prompt=auto&flowName=GeneralOAuthFlow");
    }

    @Test (dataProvider = "getTakvimler") public void checkTakvimler(String takvim) throws IOException {
        SoftAssert soft = new SoftAssert();
        driver.get(takvim);

        List<WebElement> links = driver.findElements(By.cssSelector("table a"));
        for (WebElement a : links) {
            String url = a.getAttribute("href");
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("HEAD");
            conn.connect();
            soft.assertTrue(conn.getResponseCode() < 400,a.getText() + "          BROKEN LINK");
        }
        soft.assertAll();
    }

    @DataProvider public Object[] getTakvimler(){
        List<WebElement> list = driver.findElements(By.cssSelector(".calendars-inner-div a"));
        Object[] takvimler = new Object[list.size()];
        for (int i =0; i< list.size(); i++) {
            takvimler[i] = list.get(i).getAttribute("href");
        }
        return takvimler;
    }

    @Test (dataProvider = "mainPageLinks") public void checkLinks(ArrayList<WebElement> data) throws IOException {
        SoftAssert soft = new SoftAssert();

        for (WebElement a : data) {
            String url = a.getAttribute("href");
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("HEAD");
            conn.connect();
            soft.assertTrue(conn.getResponseCode() < 400,a.getText() + "          BROKEN LINK");
        }
        soft.assertAll();
    }

    @DataProvider public Object[] mainPageLinks(){
        Object[] data = new Object[3];
        data[0] = driver.findElements(By.cssSelector(".calendars-inner-div a"));
        data[1] = driver.findElements(By.cssSelector("footer a"));
        data[2] = driver.findElements(By.cssSelector(".fc-daygrid-day-events a"));
        return data;
    }

    @AfterSuite public void afterSuite() throws InterruptedException {
        Thread.sleep(2000);
        driver.quit();
    }
}