package org.openqa.selenium.seleniumquery.functions;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.seleniumquery.SeleniumQueryObject;

public class TriggerFunction {

	public static SeleniumQueryObject trigger(SeleniumQueryObject caller, WebElement element, String event) {
		WebDriver driver = caller.getWebDriver();
		((JavascriptExecutor) driver).executeScript("return arguments[0]."+event+"();", element); 
		return caller;
	}

}