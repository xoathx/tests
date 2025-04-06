package com.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.UsePlaywright;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("UI")
@Epic("UI тесты")
@Feature("Playwright демо")
@UsePlaywright
@ExtendWith(io.qameta.allure.junit5.AllureJunit5.class)
public class SampleUITest {

    @Test
    @Story("Клик по кнопке")
    @Description("Проверяет, что клик по кнопке меняет переменную result")
    @Tag("Открываем простую HTML-страницу с кнопкой")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldClickButton(Page page) {
        Allure.step("Открываем простую HTML-страницу с кнопкой", () ->
                page.navigate("data:text/html,<script>var result;</script><button onclick='result=\"Clicked\"'>Go</button>")
        );
        Allure.step("Кликаем по кнопке", () ->
                page.locator("button").click()
        );
        Allure.step("Проверяем, что значение result стало 'Clicked'", () ->
                assertEquals("Clicked", page.evaluate("result"))
        );
    }

    @Test
    @Story("Отметка чекбокса")
    @Description("Проверяет, что checkbox можно отметить")
    @Severity(SeverityLevel.NORMAL)
    public void shouldCheckTheBox(Page page) {
        Allure.step("Устанавливаем HTML с чекбоксом", () ->
                page.setContent("<input id='checkbox' type='checkbox'></input>")
        );
        Allure.step("Ставим галочку", () ->
                page.locator("input").check()
        );
        Allure.step("Проверяем, что чекбокс отмечен", () ->
                assertEquals(true, page.evaluate("window['checkbox'].checked"))
        );
    }

    @Test
    @Story("Поиск на Википедии")
    @Description("Проверяет, что поиск по слову 'playwright' на wikipedia.org работает")
    @Severity(SeverityLevel.CRITICAL)
    public void shouldSearchWiki(Page page) {
        Allure.step("Открываем wikipedia.org", () ->
                page.navigate("https://www.wikipedia.org/")
        );
        Allure.step("Заполняем поле поиска", () -> {
            page.locator("input[name=\"search\"]").click();
            page.locator("input[name=\"search\"]").fill("playwright");
        });
        Allure.step("Нажимаем Enter для поиска", () ->
                page.locator("input[name=\"search\"]").press("Enter")
        );
        Allure.step("Проверяем, что URL ведёт на статью 'Playwright'", () ->
                assertThat(page).hasURL("https://en.wikipedia.org/wiki/Playwright")
        );
    }
}
