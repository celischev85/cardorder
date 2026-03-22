package ru.netology;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardOrderTest {
    private WebDriver driver;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        //options.addArguments("--headless");//
        driver = new ChromeDriver(options);

        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) { // driver.quit(); // } }
        }
    }

    @Test
    void shouldSubmitOrderSuccessfully() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иванов");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79991234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        String expected = "Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.";
        String actual = driver.findElement(By.cssSelector("[data-test-id='order-success']")).getText().trim();

        assertEquals(expected, actual);
    }

    @Test
    void shouldShowErrorWhenPhoneIsInvalid() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иванов");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("79001234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        By phoneErrorLocator = By.cssSelector("[data-test-id='phone'] .input__sub");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(phoneErrorLocator));
        String expected = "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.";
        String actual = driver.findElement(phoneErrorLocator).getText().trim();
        assertEquals(expected, actual);
    }

    @Test
    void shouldShowErrorWhenNameIsInvalid() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan Ivanov");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79001234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        String expected = "Имя и Фамилия указаны неверно. Допустимы только русские буквы, пробелы и дефисы.";
        String actual = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText().trim();
        assertEquals(expected, actual);
    }

    @Test
    void shouldShowErrorWhenNameIsEmpty() {
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79001234567");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        String expected = "Поле обязательно для заполнения";
        String actual = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText().trim();
        assertEquals(expected, actual);
    }

    @Test
    void shouldShowErrorWhenPhoneIsEmpty() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иванов");
        // Поле телефона оставляем пустым
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.className("button")).click();

        // Проверяем, что под полем телефона появилась ошибка о пустом поле
        String expected = "Поле обязательно для заполнения";
        String actual = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub")).getText().trim();

        assertEquals(expected, actual);
    }

    @Test
    void shouldShowErrorWhenAgreementNotChecked_TextShown() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иванов");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79991234567");
        // НЕ кликаем чекбокс
        driver.findElement(By.cssSelector("button.button")).click();

        String expected = "Я соглашаюсь с условиями обработки и использования моих персональных данных и разрешаю сделать запрос в бюро кредитных историй";
        String actual = driver.findElement(By.cssSelector("[data-test-id='agreement'].input_invalid .checkbox__text")).getText().trim();

        assertEquals(expected, actual);
    }
}