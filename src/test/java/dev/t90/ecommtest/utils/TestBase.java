package dev.t90.ecommtest.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestBase {
    protected WebDriver driver;
    protected StringBuffer verificationErrors = new StringBuffer();

    @BeforeEach
    void SetUp(){
        // Setup driver
        WebDriverManager.chromedriver().setup();
        System.out.println("Web driver loaded.");
        // open chrome
        driver = new ChromeDriver();
    }

    @AfterEach
    void TearDown(){
        driver.quit();
        String errors = verificationErrors.toString();
        if(!errors.isEmpty()){
            //We have verification errors
            Assertions.fail("Verification errors found : " + errors);
        }

    }
}
