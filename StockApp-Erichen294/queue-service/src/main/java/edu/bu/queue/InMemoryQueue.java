package edu.bu.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.tinylog.Logger;

/** Queue implementation that allows enqueue to tail and dequeue from head */
public class InMemoryQueue {
  private final ConcurrentLinkedQueue<String> queue;

  public InMemoryQueue() {
    this.queue = new ConcurrentLinkedQueue<>();
  }

  public void enqueue(String message) {
    queue.add(message);
    Logger.info("Message received: {}", message);
  }

  public String dequeue() {
    return queue.poll();
  }
}
