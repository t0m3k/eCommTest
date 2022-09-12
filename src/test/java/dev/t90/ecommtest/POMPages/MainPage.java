package dev.t90.ecommtest.POMPages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

// page_url = https://www.edgewordstraining.co.uk/demo-site
public class MainPage {
    public MainPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    // My account link
    @FindBy(css = "li[id=\"menu-item-46\"] a")
    public WebElement myAccount;

    public void goToMyAccount() {myAccount.click();}
}