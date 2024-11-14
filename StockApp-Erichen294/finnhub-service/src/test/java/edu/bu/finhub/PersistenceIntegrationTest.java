package edu.bu.finhub;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import edu.bu.FinnHubService;
import edu.bu.finhub.persistence.DatabaseSymbolPersistence;
import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.server.handlers.EnqueueingFinnhubResponseHandler;
import java.net.URISyntaxException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;

@Tag("IntegrationTest")
public class PersistenceIntegrationTest {

  EnqueueingFinnhubResponseHandler mockEnqueueingFinnhubResponseHandler;

  @BeforeEach
  public void setUp() {
    mockEnqueueingFinnhubResponseHandler = mock(EnqueueingFinnhubResponseHandler.class);
  }

  @Test
  public void createRestartConfirm() throws URISyntaxException, InterruptedException {
    SymbolsPersistence symbolsPersistence = new DatabaseSymbolPersistence();

    FinHubWebSocketClient firstFinHubWebSocketClient =
        new FinHubWebSocketClient(
            FinnHubService.WEBHOOK_URI + "?token=" + FinnHubService.API_TOKEN,
            mockEnqueueingFinnhubResponseHandler,
            symbolsPersistence);

    firstFinHubWebSocketClient.init();

    Logger.info("Removing ORCL to ensure clean slate");
    firstFinHubWebSocketClient.removeSubscribedSymbolFromSet("ORCL");
    symbolsPersistence.remove("ORCL");

    Set<String> subscribedSymbols = firstFinHubWebSocketClient.getSubscribedSymbols();

    assertFalse(subscribedSymbols.contains("ORCL"));

    firstFinHubWebSocketClient.addSubscribedSymbolToSet("ORCL");
    symbolsPersistence.add("ORCL");
    firstFinHubWebSocketClient.closeBlocking();

    FinHubWebSocketClient secondFinHubWebSocketClient =
        new FinHubWebSocketClient(
            FinnHubService.WEBHOOK_URI + "?token=" + FinnHubService.API_TOKEN,
            mockEnqueueingFinnhubResponseHandler,
            symbolsPersistence);

    secondFinHubWebSocketClient.init();
    secondFinHubWebSocketClient.initializeFromPersistence();

    Set<String> SecondSubscribedSymbols = secondFinHubWebSocketClient.getSubscribedSymbols();
    assertTrue(SecondSubscribedSymbols.contains("ORCL"));

    secondFinHubWebSocketClient.close();
  }
}
