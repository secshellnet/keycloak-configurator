import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class Main {

  public static void main(String[] args) throws IOException {

    if (args.length != 1 && args.length != 5) {
      System.out.println("<file> <db-host> <db> <db-user> <db-pass>");
      return;
    }

    if (String.join("", args).matches("[<>]")) {
      System.out.println("Illegal characters: \"<>\"");
      return;
    }

    Document document = Jsoup.parse(new File(args[0]), null, "", Parser.xmlParser());

    document.select("http-listener").attr("proxy-address-forwarding", "true");

    if (args.length > 1) {
      final Elements dataSource = document.select("datasource[pool-name=\"KeycloakDS\"]");

      dataSource.select("connection-url").html(MessageFormat.format("jdbc:postgresql://{0}/{1}", args[1], args[2]));
      dataSource.select("driver").html("postgresql");

      final Elements security = dataSource.select("security");
      security.select("user-name").html(args[3]);
      security.select("password").html(args[4]);

      document.select("drivers").html(
          document.select("drivers").html()
          +
          "<driver name=\"postgresql\" module=\"org.postgresql\">\n"
          + "<xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>\n"
          + "</driver>"
      );

//    document.select("default-bindings").attr("datasource", "java:jboss/datasources/KeycloakDS");
    }

    Files.copy(Path.of(args[0]), Path.of(args[0] + ".back." + new Random().nextInt(1000000000)));
    Files.writeString(Path.of(args[0]), document.html());
    System.out.println(Arrays.toString(Base64.getDecoder().decode("TmljbyBpc3QgZG9vZiA6KQ==")));
  }
}
