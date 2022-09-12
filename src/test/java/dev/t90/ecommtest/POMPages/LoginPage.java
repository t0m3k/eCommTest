package dev.t90.ecommtest.POMPages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

// page_url = https://www.edgewordstraining.co.uk/demo-site/my-account/
public class LoginPage {
    @FindBy(id = "username")
    public WebElement username;

    @FindBy(id = "password")
    public WebElement password;

    @FindBy(name = "login")
    public WebElement loginLog;


    public LoginPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }
}