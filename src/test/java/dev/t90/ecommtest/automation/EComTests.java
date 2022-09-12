package dev.t90.ecommtest.automation;

import static org.hamcrest.CoreMatchers.is;

import dev.t90.ecommtest.utils.TestBase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EComTests extends TestBase {
    private WebDriver driver;

    @BeforeEach
    void setUp() {

        // Setup driver
        WebDriverManager.chromedriver().setup();
        System.out.println("Web driver loaded.");
        // open chrome
        driver = new ChromeDriver();

        var address = "https://www.edgewordstraining.co.uk/demo-site/my-account/";
        driver.get(address);

        var usernameSelector = "username";
        var passwordSelector = "password";
        var loginSelector = "button.woocommerce-form-login__submit";

        var username = "asfdasf@email.com";
        var password = "rJ2CB*#Hj66^cvyj^6";

        // find elements for login
        var usernameElement = driver.findElement(By.id(usernameSelector));
        var passwordElement = driver.findElement(By.id(passwordSelector));
        var submitElement = driver.findElement(By.cssSelector(loginSelector));

        System.out.println("Logging in.");

        // Clear username field
        usernameElement.sendKeys(Keys.CONTROL + "a");
        usernameElement.sendKeys(Keys.BACK_SPACE);

        // fill username and password
        usernameElement.sendKeys(username);

        // clear password field
        passwordElement.sendKeys(Keys.CONTROL + "a");
        passwordElement.sendKeys(Keys.BACK_SPACE);

        // enter password
        passwordElement.sendKeys(password);

        // Click submit
        submitElement.click();

        /* ############################# LOGGED IN ############################# */
        System.out.println("Logged in.");

        // dismiss the notice
        driver.findElement(By.linkText("Dismiss")).click();
    }

    @Test
    public void couponTest() {
        addItemToBasket();

        /* ############################# CHECKING COUPON ############################# */

        // find coupon input and enter the coupon
        System.out.println("Entering coupon code");
        driver.findElement(By.id("coupon_code")).sendKeys("edgewords");
        driver.findElement(By.cssSelector("[name=apply_coupon]")).click();

        // Get discount and make sure it's 15%
        System.out.println("Checking discount");
        var discountXPath = "//*[@id=\"post-5\"]/div/div/div[2]/div/table/tbody/tr[2]/td/span";
        WebDriverWait myWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        myWait.until((drv) -> drv.findElement(By.xpath(discountXPath)));
        var discount = getPrice(discountXPath);

        System.out.println("Checking total");
        var subTotalXPath = "//*[@id=\"post-5\"]/div/div/div[2]/div/table/tbody/tr[1]/td/span/bdi";
        var subTotal = getPrice(subTotalXPath);

        MatcherAssert.assertThat((int)Math.round(subTotal * 0.15), is(discount));

        var deliveryXPath = "//*[@id=\"shipping_method\"]/li/label/span/bdi";
        var delivery = getPrice(deliveryXPath);

        var totalXPath = "//*[@id=\"post-5\"]/div/div/div[2]/div/table/tbody/tr[4]/td/strong/span/bdi";
        var total = getPrice(totalXPath);

        MatcherAssert.assertThat(total, is(subTotal - discount + delivery));


    }

    @Test
    public void orderTest() {
        addItemToBasket();

        /* ############################# CHECKOUT ############################# */
        // Going to checkout
        System.out.println("Going to checkout");
        driver.findElement(By.partialLinkText("Proceed to checkout")).click();

        // Filling checkout
        var fistNameId = "billing_first_name";
        var lastNameId = "billing_last_name";

        var streetId = "billing_address_1";

        var name = driver.findElement(By.id(fistNameId));
        var lastName = driver.findElement(By.id(lastNameId));


        name.sendKeys(Keys.CONTROL + "a");
        name.sendKeys("Harry");

        lastName.sendKeys(Keys.CONTROL + "a");
        name.sendKeys("Potter");


        var address = driver.findElement(By.id(streetId));

        address.sendKeys(Keys.CONTROL + "a");
        address.sendKeys("4 Privet Drive" + Keys.TAB + Keys.TAB + "Surrey" + Keys.TAB + Keys.TAB + "RG12 9FG" + Keys.TAB + "7777 777 777");

        // Click on cheque payment
        try {
            driver.findElement(By.cssSelector("#payment > ul > li.wc_payment_method.payment_method_cheque")).click();
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
            driver.findElement(By.cssSelector("#payment > ul > li.wc_payment_method.payment_method_cheque")).click();
        }

        driver.findElement(By.id("place_order")).click();

        System.out.println("Order made.");

        // Get order id
        WebDriverWait myWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        myWait.until((drv) -> drv.findElement(By.cssSelector("li.woocommerce-order-overview__order.order > strong")));
        var orderId = driver.findElement(By.cssSelector("li.woocommerce-order-overview__order.order > strong")).getText();

        System.out.println("Order ID received\nGoing to My account");

        /* ############################# MY ACCOUNT ############################# */

        driver.findElement(By.linkText("My account")).click();
        driver.findElement(By.linkText("Orders")).click();

        // Find first order number in the table (last order made)
        var orderNoSelector = "tr:nth-child(1) > td.woocommerce-orders-table__cell.woocommerce-orders-table__cell-order-number > a";
        var orderNo = driver.findElement(By.cssSelector(orderNoSelector)).getText().substring(1);
        MatcherAssert.assertThat(orderId, is(orderNo));
    }

    @AfterEach
    void tearDown() {
        /* ############################# LOG OUT ############################# */

        System.out.println("Going to my account.");
        driver.findElement(By.linkText("My account")).click();
        System.out.println("Logging out.");
        driver.findElement(By.linkText("Log out")).click();

        driver.quit();
    }

    private void addItemToBasket() {
        /* ############################# SHOP ############################# */
        // got to shop
        System.out.println("Going to shop.");
        driver.findElement(By.linkText("Shop")).click();

        // get list of add to card links
        System.out.println("Adding item to basket.");
        var addToCartElements = driver.findElements(By.cssSelector(".add_to_cart_button"));

        // pick one ad random and click
        var element = (int)Math.round(Math.random() * addToCartElements.size());
        addToCartElements.get(element).click();

        /* ############################# CART ############################# */
        System.out.println("Going to cart.");

        // Wait for, find and click on cart element
        WebDriverWait myWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        myWait.until((drv) -> drv.findElement(By.linkText("View cart")));
        driver.findElement(By.linkText("View cart")).click();
    }

    private int getPrice(String xPath) {
        return (int)Math.round(Double.parseDouble(driver.findElement(By.xpath(xPath)).getText().substring(1)) * 100);
    }

}
