package ru.netology.servlet;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.nio.file.Files;


public class MainServlet extends HttpServlet {
  public static void main(String[] args) throws LifecycleException, IOException {
    final var tomcat = new Tomcat();
    final var baseDir = Files.createTempDirectory("tomcat");
    baseDir.toFile().deleteOnExit();
    tomcat.setBaseDir(baseDir.toAbsolutePath().toString());

    final var connector = new Connector();
    connector.setPort(8080);
    tomcat.setConnector(connector);

    tomcat.getHost().setAppBase(".");
    tomcat.addWebapp("", ".");

    tomcat.start();
    tomcat.getServer().await();
  }
}
