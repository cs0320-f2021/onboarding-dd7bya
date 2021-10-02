package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  // use port 4567 by default when running server
  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // set up parsing of command line flags
    OptionParser parser = new OptionParser();

    // "./run --gui" will start a web server
    parser.accepts("gui");

    // use "--port <n>" to specify what port on which the server runs
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    // TODO: Add your REPL here!
    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      String input;
      while ((input = br.readLine()) != null) {
        try {
          input = input.trim();
          String[] arguments = input.split(" "); //splits input string by space and creates array
          LinkedList<Star> allStars = new LinkedList<Star>();
          //System.out.println(arguments[0]);
          // TODO: complete your REPL by adding commands for addition "add" and subtraction
          //  "subtract"
          if (arguments[0].equals("add") || arguments[0].equals("subtract")) {
            MathBot mathBot = new MathBot();
            double num1 = Double.parseDouble(arguments[1]);
            double num2 = Double.parseDouble(arguments[2]);
            if (arguments[0].equals("add")) {
              System.out.println(mathBot.add(num1, num2));
            } else if (arguments[0].equals("subtract")) {
              System.out.println(mathBot.subtract(num1, num2));
            }
          }
          else if (arguments[0].equals("stars")) { //stores star data in Linked List of arrays
            allStars.clear();
            BufferedReader reader = new BufferedReader(new FileReader(arguments[1]));
            reader.readLine(); //goes past first line, which just lists column names
            String info = reader.readLine();
            while (info != null) { //add star info to list
              String[] splitInfo = info.trim().split(",");
              Star newStar = new Star(Integer.parseInt(splitInfo[0]), splitInfo[1],
                  Float.parseFloat(splitInfo[2]), Float.parseFloat(splitInfo[3]),
                  Float.parseFloat(splitInfo[4]);
              allStars.add(newStar);
              info = reader.readLine();
            }
            reader.close();
          } else if (arguments[0].equals("naive_neighbors")) {
              if (arguments.length == 5) { //first naive_neighbors implementation
                HashMap<Star,Double> distances = new HashMap<Star,Double>;
                //calculate distances of all stars and add to hashmap allStars
                for (Star star : allStars) {
                  distances.put(star, Math.sqrt((star.X - Float.parseFloat(arguments[2])) *
                      (star.Y - Float.parseFloat(arguments[3])) *
                      (star.Z - Float.parseFloat(arguments[4]))));
                }
                HashMap<Star, Double> outputStars = new HashMap<Star,Double>();
                //find top k distances and add to hashmap outputStars
                for (Map.Entry<Star,Double> entryAll : distances.entrySet()){
                  if (outputStars.size() == Integer.parseInt(arguments[1])) { //already has k entries
                    for (Map.Entry<Star, Double> entryOutput : outputStars.entrySet()) {
                      if (entryOutput.getValue() > entryAll.getValue()) {
                        outputStars.remove(entryOutput.getKey());
                        outputStars.put(entryAll.getKey(), entryAll.getValue());
                      }
                    }
                  }
                  else {
                    outputStars.put(entryAll.getKey(), entryAll.getValue());
                  }
                }
            } else if (arguments.length == 4) { //second naive_neighbors implementation
                int[] inputStarCoords = new int[];
                arguments[0]
              } else { //number of parameters is incorrect
                System.out.println("ERROR: We couldn't process your input");
                throw new Exception();
              }
          }
        } catch (Exception e) {
          // e.printStackTrace();
          System.out.println("ERROR: We couldn't process your input");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR: Invalid input for REPL");
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration(Configuration.VERSION_2_3_0);

    // this is the directory where FreeMarker templates are placed
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    // set port to run the server on
    Spark.port(port);

    // specify location of static resources (HTML, CSS, JS, images, etc.)
    Spark.externalStaticFileLocation("src/main/resources/static");

    // when there's a server error, use ExceptionPrinter to display error on GUI
    Spark.exception(Exception.class, new ExceptionPrinter());

    // initialize FreeMarker template engine (converts .ftl templates to HTML)
    FreeMarkerEngine freeMarker = createEngine();

    // setup Spark Routes
    Spark.get("/", new MainHandler(), freeMarker);
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Request req, Response res) {
      // status 500 generally means there was an internal server error
      res.status(500);

      // write stack trace to GUI
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * A handler to serve the site's main page.
   *
   * @return ModelAndView to render.
   * (main.ftl).
   */
  private static class MainHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // this is a map of variables that are used in the FreeMarker template
      Map<String, Object> variables = ImmutableMap.of("title",
          "Go go GUI");

      return new ModelAndView(variables, "main.ftl");
    }
  }
}
