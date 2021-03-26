package com.example;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import java.io.IOException;
import java.util.stream.IntStream;

public class Application {

   private final ClusterListener listener;
   private final EmbeddedCacheManager cacheManager;
   private Cache<Integer, Book> cache;

   public Application() throws IOException, InterruptedException {
      cacheManager = new DefaultCacheManager("infinispan-local.xml");
      listener = new ClusterListener(2);
      cacheManager.addListener(listener);
      cache = cacheManager.getCache("queryReplCache");

      System.out.println("---- Waiting for cluster to form ----");
      listener.clusterFormedLatch.await();
   }

   public void insertSomeEntries() {
      if(cacheManager.isCoordinator()) {
         IntStream.rangeClosed(0, 100).forEach(it -> cache.put(it, new Book("something: "+it)));
      }
   }

   public void getSomeEntries() {
      if(!cacheManager.isCoordinator()) {
         IntStream.rangeClosed(0, 1000).forEach(it -> System.out.println(cache.get(it).getTitle()));
      }
   }

   public void shutdown() throws InterruptedException {
      if (cacheManager.isCoordinator()) {
         listener.shutdownLatch.await();
      }
      cacheManager.stop();
   }

   public static void main(String[] args) throws IOException, InterruptedException {

      Application application = new Application();
      application.insertSomeEntries();
      System.out.println(application.cache.size());
      application.getSomeEntries();

      //COMMENT THREAD.SLEEP TO SEE THE EXCEPTION DISSAPEAR
      Thread.sleep(10000);
      application.shutdown();

   }
}
