package edu.bu;

import edu.bu.server.QueueServer;
import java.io.IOException;
import java.net.URISyntaxException;

/** Entry point for the queue service. */
public class QueueService {
  public static void main(String[] args) throws IOException, URISyntaxException {
    // start queue server
    QueueServer queueServer = new QueueServer();
    queueServer.start();
  }
}
