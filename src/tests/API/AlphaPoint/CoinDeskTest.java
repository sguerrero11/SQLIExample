package API.AlphaPoint;


import helpers.api.PetStore.PetStoreApiHelper;
import listeners.ProjectListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners({ProjectListener.class})
public class CoinDeskTest {

    private PetStoreApiHelper coinDesk;

    @BeforeClass
    public void beforeTests() {
        coinDesk = new PetStoreApiHelper();
        coinDesk.setBaseURI("https://api.coindesk.com/v1/bpi/currentprice.json"); // sets the endpoint
    }

    @Test(description = "Get usd rate by Coin and status is 200", priority = 10)
    public void getUSDRateForBitcoin() {
        String usdRateBitcoin = coinDesk.getUSDRate("Bitcoin");
        // print response

        System.out.println("Getting the usd rate for Bitcoin: " + usdRateBitcoin);

        // Convert the rate to a numeric value for comparison
        double usdRateValue = Double.parseDouble(usdRateBitcoin.replace(",", ""));

        // Assert that the USD rate is greater than 100k
//        Assert.assertTrue(usdRateValue > 100000,
//                "USD rate is not greater than 100k. Current rate: " + usdRateValue);
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(usdRateValue > 100000,
                "USD rate is not greater than 100k. Current rate: " + usdRateValue);

        // softAssert.assertAll(); // to fail the test

        // assertGreaterThan("100,000",usdRateBitcoin,"Verify usd rate for Bitcoin is greater than 100k");
    }
}



/*

Backend Exercise:
Base URL: https://api.coindesk.com/v1/bpi/currentprice.json

The Above url returns the Bitcoin Price Index in real-time


1. Write a test that will get the USD Rate for Bitcoin
2. Write an Assertion that validates that the USD Rate is greater than 100k

---

{
  "time": {
    "updated": "Dec 10, 2024 22:51:44 UTC",
    "updatedISO": "2024-12-10T22:51:44+00:00",
    "updateduk": "Dec 10, 2024 at 22:51 GMT"
  },
  "disclaimer": "This data was produced from the CoinDesk Bitcoin Price Index (USD). Non-USD currency data converted using hourly conversion rate from openexchangerates.org",
  "chartName": "Bitcoin",
  "bpi": {
    "USD": {
      "code": "USD",
      "symbol": "&#36;",
      "rate": "97,196.878",
      "description": "United States Dollar",
      "rate_float": 97196.8781
    },
    "GBP": {
      "code": "GBP",
      "symbol": "&pound;",
      "rate": "76,103.698",
      "description": "British Pound Sterling",
      "rate_float": 76103.6976
    },
    "EUR": {
      "code": "EUR",
      "symbol": "&euro;",
      "rate": "92,325.759",
      "description": "Euro",
      "rate_float": 92325.7594
    }
  }
}


 */