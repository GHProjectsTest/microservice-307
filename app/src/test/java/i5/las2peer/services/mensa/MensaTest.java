package i5.las2peer.services.mensa;

import static i5.las2peer.services.mensa.TestUtil.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import com.github.viclovsky.swagger.coverage.core.generator.Generator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import javax.ws.rs.core.MediaType;

import i5.las2peer.p2p.LocalNode;
import i5.las2peer.p2p.LocalNodeManager;
import i5.las2peer.api.p2p.ServiceNameVersion;
import i5.las2peer.security.ServiceAgentImpl;
import i5.las2peer.security.UserAgentImpl;
import i5.las2peer.security.AnonymousAgentImpl;
import i5.las2peer.testing.MockAgentFactory;
import i5.las2peer.connectors.webConnector.WebConnector;
import i5.las2peer.connectors.webConnector.client.ClientResponse;
import i5.las2peer.connectors.webConnector.client.MiniClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;


/**
 * Mensa18 - Test Class
 *
 * This class provides a basic testing framework for the microservice Mensa18. It was
 * generated by the CAE (Community Application Framework).
 */
public class MensaTest {

  private static final String HTTP_ADDRESS = "http://127.0.0.1";
  private static final int HTTP_PORT = WebConnector.DEFAULT_HTTP_PORT;

  private static LocalNode node;
  private static WebConnector connector;
  private static ByteArrayOutputStream logStream;

  private static UserAgentImpl testAgentAdam;
  private static UserAgentImpl testAgentAbel;
  private static UserAgentImpl testAgentEve;

  private static final String testPassAdam = "adamspass";
  private static final String testPassAbel = "abelspass";
  private static final String testPassEve = "evespass";

  // version does not matter in tests
  private static final ServiceNameVersion testTemplateService = new ServiceNameVersion(Mensa.class.getCanonicalName(),"0.1");

  private static final String mainPath = "mensa";


  /**
   * Called before the tests start.
   * Sets up the node and initializes connector and users that can be used throughout the tests.
   * @throws Exception
   */
  @BeforeClass
  public static void startServer() throws Exception {

    // start node
    node = new LocalNodeManager().newNode();
    
    testAgentAdam = MockAgentFactory.getAdam();
    testAgentAdam.unlock(testPassAdam); // agent must be unlocked in order to be stored
    node.storeAgent(testAgentAdam);

    testAgentAbel = MockAgentFactory.getAbel();
    testAgentAbel.unlock(testPassAbel);
    node.storeAgent(testAgentAbel);

    testAgentEve = MockAgentFactory.getEve();
    testAgentEve.unlock(testPassEve);
    node.storeAgent(testAgentEve);

    node.launch();

    ServiceAgentImpl testService = ServiceAgentImpl.createServiceAgent(testTemplateService, "a pass");
    testService.unlock("a pass");

    node.registerReceiver(testService);

    // start connector
    logStream = new ByteArrayOutputStream();

    connector = new WebConnector(true, HTTP_PORT, false, 1000);
    connector.setLogStream(new PrintStream(logStream));
    connector.start(node);
    Thread.sleep(1000); // wait a second for the connector to become ready

    // download swagger.json
    InputStream in = new URL(connector.getHttpEndpoint() + "/" + mainPath + "/swagger.json").openStream();
    Files.copy(in, Paths.get("export/swagger.json"), StandardCopyOption.REPLACE_EXISTING);
    
     
  }


  /**
   * 
   * Test for the GETdishesidratingsnotfoundtest_ID197273 method.
   * 
   */
  @Test
  public void testGETdishesidratingsnotfoundtest_ID197273() {
    MiniClientCoverage c = new MiniClientCoverage(mainPath);
    c.setConnectorEndpoint(connector.getHttpEndpoint());
    
        
    try {
      c.setLogin(AnonymousAgentImpl.IDENTIFIER, "");
      ClientResponse result = c.sendRequest("GET", "/dishes/{id}/ratings", """
""", "text/plain", "*/*", new HashMap<>(), "99999");
      System.out.println("Result of request with id: 365574: " + result.getResponse().trim());
    
      Assert.assertEquals("[499547]", 404, result.getHttpCode());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception: " + e);
    }

    
  }
  /**
   * 
   * Test for the Listavailableratingsforadish_ID568080 method.
   * 
   */
  @Test
  public void testListavailableratingsforadish_ID568080() {
    MiniClientCoverage c = new MiniClientCoverage(mainPath);
    c.setConnectorEndpoint(connector.getHttpEndpoint());
    
        
    try {
      c.setLogin(AnonymousAgentImpl.IDENTIFIER, "");
      ClientResponse result = c.sendRequest("GET", "/dishes/{id}/ratings", """
""", "text/plain", "*/*", new HashMap<>(), "1");
      System.out.println("Result of request with id: 528004: " + result.getResponse().trim());
      Object response = JSONValue.parse(result.getResponse().trim());
      // Response body all list entries has field "stars" has type Number
      assertThat("[242745]", response, both(isA(JSONArray.class)).and(asJSONObjectList(everyItem(both(isA(JSONObject.class)).and(asJSONObject(hasField("stars", isA(Number.class))))))));
      
      // Response body has type JSON Array
      assertThat("[480669]", response, isA(JSONArray.class));
      

      Assert.assertEquals("[522940]", 200, result.getHttpCode());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception: " + e);
    }

    
  }




  /**
   * Called after the tests have finished. Shuts down the server and prints out the connector log
   * file for reference.
   * @throws Exception
   */
  @AfterClass
  public static void shutDownServer() throws Exception {
	
	 

    connector.stop();
    node.shutDown();

    connector = null;
    node = null;

    System.out.println("Connector-Log:");
    System.out.println("--------------");

    System.out.println(logStream.toString());

    new Generator().setInputPath(Paths.get("swagger-coverage-output/"))
            .setSpecPath(Paths.get("export/swagger.json").toUri())
            .run();
  }

}