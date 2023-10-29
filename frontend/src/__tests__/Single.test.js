import { Builder, By } from "selenium-webdriver";
import chrome from "selenium-webdriver/chrome";
import { afterAll, beforeAll, describe, expect, it } from "vitest";

const url = "http://localhost:9090";

describe("Selenium", () => {
    let driver;

    beforeAll(async () => {
        const chromeOptions = new chrome.Options();
        chromeOptions
            .addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");

        driver = new Builder()
            .forBrowser("chrome")
            .setChromeOptions(chromeOptions)
            .build();

        await driver.manage().window().maximize();
        await driver.manage().setTimeouts({ implicit: 10000 });
        await driver.get(url);
    });

    it("should redirect to the login page", async () => {
        const username = await driver.findElement(By.id("username"));
        const password = await driver.findElement(By.id("password"));

        await username.sendKeys("chaitanya");
        await password.sendKeys("chaitanya");

        const submit = await driver.findElement(By.id("submit"));
        await submit.click();

        const currentUrl = await driver.getCurrentUrl();
        expect(currentUrl).toBe(`${url}/login`);
    });

    it("should be on the home page", async () => {
        const homeTitle = await driver.findElement(By.id("title")).getText();
        const currentUrl = await driver.getCurrentUrl();

        expect(currentUrl).toBe(`${url}/`);
        expect(homeTitle).toBe("IPAM");
    });

    it("should perform reserve action on ip-address", async () => {
        //default tab is ip-addresses
        const ipAddressTitle = await driver.findElement(By.id("ipaddress-title")).getText();
        expect(ipAddressTitle).toBe("IP Addresses");

        //click on reserve button
        const reserveButtons = await driver.findElements(By.id("reserve-btn"));
        const unreservedButtons = [];
        await Promise.all(
            reserveButtons.map(async (button) => {
                const isClickable = await button.isEnabled();
                if (isClickable) {
                    unreservedButtons.push(button);
                }
            })
        );
        const button = await unreservedButtons[0];
        await button.click();
        driver.sleep(1000);
        const toast = await driver.findElement(By.className("Toastify"));
        const toastText = await toast.getText();
        expect(toastText).toBe("🦄 ip address reserved");  
    })

    afterAll(async () => {
        await driver.quit();
    });

})
