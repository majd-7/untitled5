package utilities;

import io.cucumber.java.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.time.Duration;

import static io.restassured.RestAssured.baseURI;

public class Hooks {

  @BeforeAll
  public static void setUpDB(){
    DbUtils.createConnection();
  }

  @AfterAll
  public static void closeDB(){
    DbUtils.close();
  }

  @Before("@api or @api-end-to-end")
  public void APISetup(){
    baseURI = ConfigReader.getProperty("base_uri");
  }

  @Before("not @db_only and not @api")
  public void UISetup(){
    String browser = System.getProperty("browser");  //read the browser type from command line, if no browser is passed value returned will be null

    if(browser == null){ // if no browser is passed thru command line
      browser = ConfigReader.getProperty("browser"); // Read the browser type from .properties file
    }

    if(browser.contains("headless")){
      Driver.getDriver().manage().window().setSize(new Dimension(1920, 1080));
    }
    else{
      Driver.getDriver().manage().window().maximize();
    }

    Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
  }

  @After("not @db_only and not @api")
  public void UITearDown(Scenario scenario){
    if(scenario.isFailed()){
      byte[] screenshotAs = ((TakesScreenshot) Driver.getDriver()).getScreenshotAs(OutputType.BYTES);
      scenario.attach(screenshotAs,"image/png", "screenshotOfFailure");
    }

    Driver.quitDriver();
  }
}
