Enter file contents herepackage com.yourcompany; 





import com.saucelabs.common.SauceOnDemandAuthentication; 
import com.saucelabs.common.SauceOnDemandSessionIdProvider; 
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider; 
import com.saucelabs.testng.SauceOnDemandTestListener; 
import org.openqa.selenium.By; 
import org.openqa.selenium.Keys; 
import org.openqa.selenium.WebDriver; 
import org.openqa.selenium.WebElement; 
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities; 
import org.openqa.selenium.remote.CapabilityType; 
import org.openqa.selenium.remote.RemoteWebDriver; 
import org.openqa.selenium.support.ui.ExpectedConditions; 
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait; 
import org.testng.Assert; 
import org.testng.annotations.AfterMethod; 
import org.testng.annotations.BeforeMethod; 
import org.testng.annotations.DataProvider; 
import org.testng.annotations.Listeners; 
import org.testng.annotations.Test; 

import java.lang.reflect.Method; 
import java.net.MalformedURLException; 
import java.net.URL; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals; 




/** 
 * Simple TestNG test which demonstrates being instantiated via a DataProvider in order to supply multiple browser combinations. 
 * 
 * @author Neil Manvar 
 */ 
@Listeners({SauceOnDemandTestListener.class}) 
public class SampleSauceTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider { 


    public String username = System.getenv("SAUCE_USER_NAME") != null ? System.getenv("SAUCE_USER_NAME") : System.getenv("SAUCE_USERNAME"); 
    public String accesskey = System.getenv("SAUCE_API_KEY") != null ? System.getenv("SAUCE_API_KEY") : System.getenv("SAUCE_ACCESS_KEY"); 
 
    static Wait<WebDriver> wait;

    /** 
     * Constructs a {@link SauceOnDemandAuthentication} instance using the supplied user name/access key.  To use the authentication 
     * supplied by environment variables or from an external file, use the no-arg {@link SauceOnDemandAuthentication} constructor. 
     */ 
public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(username, accesskey); 


    /** 
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with. 
     */ 
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>(); 


    /** 
     * ThreadLocal variable which contains the Sauce Job Id. 
     */ 
    private ThreadLocal<String> sessionId = new ThreadLocal<String>(); 


    /** 
     * DataProvider that explicitly sets the browser combinations to be used. 
     * 
     * @param testMethod 
     * @return 
     */ 
    @DataProvider(name = "hardCodedBrowsers", parallel = true) 
    public static Object[][] sauceBrowserDataProvider(Method testMethod) { 
        return new Object[][]{ 
                new Object[]{"internet explorer", "11", "Windows 8.1"}, 
                new Object[]{"chrome", "41", "Windows XP"} 
           
        }; 
    } 


    /** 
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the browser, 
     * version and os parameters, and which is configured to run against ondemand.saucelabs.com, using 
     * the username and access key populated by the {@link #authentication} instance. 
     * 
     * @param browser Represents the browser to be used as part of the test run. 
     * @param version Represents the version of the browser to be used as part of the test run. 
     * @param os Represents the operating system to be used as part of the test run. 
     * @return 
     * @throws MalformedURLException if an error occurs parsing the url 
     */ 
    private WebDriver createDriver(String browser, String version, String os, String methodName) throws MalformedURLException { 


        DesiredCapabilities capabilities = new DesiredCapabilities(); 
        capabilities.setCapability(CapabilityType.BROWSER_NAME, browser); 
        if (version != null) { 
            capabilities.setCapability(CapabilityType.VERSION, version); 
        } 
        capabilities.setCapability(CapabilityType.PLATFORM, os); 


        String jobName = methodName + '_' + os + '_' + browser + '_' + version; 
         capabilities.setCapability("name", jobName); 
         webDriver.set(new RemoteWebDriver(new URL("http://" + authentication.getUsername() + ":" + authentication.getAccessKey() + "@ondemand.saucelabs.com:80/wd/hub"),capabilities)); 
         String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString(); 
         sessionId.set(id); 
 

         String message = String.format("SauceOnDemandSessionID=%1$s job-name=%2$s", id, jobName); 
         System.out.println(message); 
 

         return webDriver.get(); 
     } 
 

     @AfterMethod 
     public void tearDown() throws Exception { 
         webDriver.get().quit(); 
     } 
 

      
     @Test(dataProvider = "hardCodedBrowsers") 
     public void searchtxt(String browser, String version, String os, Method method) throws Exception  
     { 
         WebDriver driver = createDriver(browser, version, os, method.getName());         
         
         driver.get("https://travis-ci.org/"); 
         driver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);  
      	 driver.findElement(By.xpath("/html/body/div/div/div[2]/div[1]/div/div[1]/button")).click(); 
       	//driver.findElement(By.cssSelector("button.signed-out")).click(); 
          driver.findElement(By.id("login_field")).sendKeys("sheikp@gmail.com"); 
          driver.findElement(By.id("password")).sendKeys("rayhan2006"); 
          driver.findElement(By.xpath("/html/body/div[4]/div[1]/div/form/div[3]/input[4]")).click(); 
          boolean travis_succ=driver.findElement(By.cssSelector(".ember-view")).isDisplayed(); 
          if (driver.findElement(By.cssSelector(".ember-view")).getText()=="CI_Jmeter") 
          { 
          	Jmeter_Func(driver); 
          } 
          else 
          { 
          	String[] Testcases = {"Fosimple"}; 
          	for(int i=0;i<Testcases.length;i++) 
          	{ 
          		if (i>0) 
          		{ 
          			driver.close(); 
                      driver.quit(); 
                      driver = createDriver(browser, version, os, method.getName()); 
          			driver.get("https://travis-ci.org/"); 
          	    	driver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS); 
          	    	driver.findElement(By.xpath("/html/body/div/div/div[2]/div[1]/div/div[1]/button")).click(); 
             	        driver.findElement(By.id("login_field")).sendKeys("sheikp"); 
          	        driver.findElement(By.id("password")).sendKeys("rayhan2006"); 
          	        driver.findElement(By.xpath("/html/body/div[4]/div[1]/div/form/div[3]/input[4]")).click(); 
           		} 
          		Selenium_Func(Testcases[i], driver); 
          	} 
          }
      } 
 
     public static void Jmeter_Func(WebDriver driver) 
     { 
     	System.out.println("Jmeter Test"); 
     	String Build_Id=driver.findElement(By.cssSelector(".commit")).getText(); 
         String Jmeter_Summ = driver.findElement(By.xpath("/html/body/div/div/div/div/div/div/div/article/div/div[2]/div/div[2]/div/section/div/div/pre/p[40]/span")).getText(); 
         System.out.println(Jmeter_Summ); 
        //dbinsert("Jmeter",Jmeter_Summ,"Jmeter Test Case",Build_Id,"Passed",""); 
          
     }
     
     public static void Selenium_Func(String tc, WebDriver driver) 
     { 
     	String testcase=tc; 
     	String Build_Id=driver.findElement(By.cssSelector(".commit")).getText(); 
         System.out.println(Build_Id); 
     	System.out.println("Selenium Test"); 
     	driver.findElement(By.cssSelector(".icon--downloadLog")).click(); 
     	String Logs=driver.findElement(By.tagName("body")).getText(); 
     

     	String testpattern=testcase+": [31mTest"; 
     

     	int ind=Logs.indexOf(testpattern); 
    	int status_index=ind+testpattern.length()+1; 
    	 
    	String Status=Logs.substring(status_index,status_index+6); 
    	String Defect_Summ=testcase+" : "+Status; 
    	System.out.println(Defect_Summ); 
    		 
    	if (Status.equals("failed")) 
    	{ 
    		int Desc_Beg_in=status_index+13; 
    		String word=Logs.substring(status_index,status_index+6); 
    		status_index=status_index+1; 
    		while (!word.equals("Selenium")) 
    		{ 
    			word=Logs.substring(status_index,status_index+8); 
    			int Desc_End_in=status_index; 
    			status_index=status_index+1; 
    		} 
    		String Defect_Desc=Logs.substring(Desc_Beg_in,status_index-1); 
    		System.out.println(Defect_Desc); 
    		Jira_Defects(Defect_Desc,Defect_Summ,Build_Id,driver); 
    		 
    	} 
    	else 
    	{ 
    		String Defect_Desc="The Test Case Passed"; 
    		//dbinsert("Selenium",Defect_Summ,Defect_Desc,Build_Id,"Passed",""); 
    	}	 
    	//driver.navigate().back(); 
    } 
     
     public static void Jira_Defects(String defect_Desc,String Defect_Summ, String Build_Id, WebDriver driver) 
     { 


     	System.out.println("Logging In JIRA"); 
     	driver.get("https://qahacker.atlassian.net/"); 
     	//driver.findElement(By.cssSelector(".login-link")).click(); 
     	driver.findElement(By.id("username")).sendKeys("sujoysengupta05@gmail.com"); 
     	driver.findElement(By.id("password")).sendKeys("05yojus93"); 
     	driver.findElement(By.id("login")).click(); 
     	driver.findElement(By.id("quickSearchInput")).sendKeys(Defect_Summ); 
         driver.findElement(By.id("quickSearchInput")).sendKeys(Keys.ENTER); 
         Boolean isPresent = driver.findElements(By.cssSelector(".issue-link")).isEmpty(); 
         System.out.println(isPresent); 
         if (isPresent==false) 
         { 
         	Boolean status_Check=driver.findElement(By.cssSelector(".jira-issue-status-lozenge")).getText().contains("DONE"); 
         	System.out.println(driver.findElement(By.cssSelector(".jira-issue-status-lozenge")).getText()); 
         	System.out.println(status_Check); 
         	if (status_Check==true) 
         	{ 
         		defect_Creation(defect_Desc,Defect_Summ,Build_Id,driver); 
         	} 
         	else 
         	{ 
         		String defectId=driver.findElement(By.cssSelector(".issue-link")).getText(); 
         		System.out.println(defectId); 
         		dbinsert("Selenium",Defect_Summ,defect_Desc,Build_Id,"Failed",defectId,driver); 
         	}   	 
         } 
         else 
         { 
         	defect_Creation(defect_Desc,Defect_Summ,Build_Id,driver); 
         }    			    
     } 
     
     public static void defect_Creation(String defect_Desc,String Defect_Summ,String Build_Id,WebDriver driver) 
     {   
     	System.out.println("Creating JIRA Defect"); 
         driver.findElement(By.id("create_link")).click(); 
         driver.findElement(By.id("issuetype-field")).clear(); 
         driver.findElement(By.id("issuetype-field")).sendKeys("Bug"); 
         driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS); 
          
         //driver.findElement(By.id("assign-to-me-trigger")).click(); 
         driver.findElement(By.name("description")).sendKeys(defect_Desc); 
         driver.findElement(By.name("summary")).click(); 
         driver.findElement(By.name("summary")).sendKeys(Defect_Summ); 
              
         driver.findElement(By.id("create-issue-submit")).click(); 
         driver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS); 
          
        driver.findElement(By.id("quickSearchInput")).sendKeys(Defect_Summ); 
        driver.findElement(By.id("quickSearchInput")).sendKeys(Keys.ENTER); 
        String defectId=driver.findElement(By.cssSelector(".issue-link")).getText(); 
        System.out.println(defectId); 
         
        dbinsert("Selenium",Defect_Summ,defect_Desc,Build_Id,"Failed",defectId,driver); 
     } 
     
     public static void dbinsert(String Script,String Defect_Summ,String Defect_Desc,String Build_Id,String Status,String defectId , WebDriver driver) 
     {
     	
     Connection conn = null;
     Statement stmt = null;
     // JDBC driver name and database URL
     final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
     final String DB_URL = "jdbc:mysql://us-cdbr-azure-central-a.cloudapp.net:3306/ClearDBDispatch";

     //  Database credentials
     final String USER = "bdedbb1076214d";
     final String PASS = "9a4667ff";

     try{
        //STEP 2: Register JDBC driver
        Class.forName("com.mysql.jdbc.Driver");

        //STEP 3: Open a connection
        System.out.println("Connecting to a selected database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        System.out.println("Connected database successfully...");
        
        //STEP 4: Execute a query
        System.out.println("Inserting records into the table...");
        stmt = conn.createStatement();
        String sql = "insert into automation_results (buildscenario,TestName,Comments,RunID,Results,DefectID,executiontime) values ('" + Script + "','" + Defect_Summ + "','" + Defect_Desc + "','" + Build_Id + "','" + Status + "','" + defectId + "',now())";
        stmt.executeUpdate(sql);
        System.out.println("Inserted records into the table...");

     }catch(SQLException se){
        //Handle errors for JDBC
        se.printStackTrace();
     }catch(Exception e){
        //Handle errors for Class.forName
        e.printStackTrace();
     }finally{
        //finally block used to close resources
        try{
           if(stmt!=null)
              conn.close();
        }catch(SQLException se){
        }// do nothing
        try{
           if(conn!=null)
              conn.close();
        }catch(SQLException se){
           se.printStackTrace();
        }//end finally try
     }//end try
     System.out.println("Goodbye!");
     }
     
     
     /** 
      * @return the {@link WebDriver} for the current thread 
      */ 
     public WebDriver getWebDriver() { 
         System.out.println("WebDriver" + webDriver.get()); 
         return webDriver.get(); 
     } 
 

     /** 
      * 
      * @return the Sauce Job id for the current thread 
      */ 
     public String getSessionId() { 
         return sessionId.get(); 
     } 
 

     /** 
      * 
      * @return the {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key 
      */ 
     @Override 
     public SauceOnDemandAuthentication getAuthentication() { 
         return authentication; 
     } 
 } 
 



