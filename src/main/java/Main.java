import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class Main {

  public static void main(String[] args) throws IOException {

    if (args.length != 1 && args.length != 5) {
      System.out.println("<file> <db-host> <db> <db-user> <db-pass>");
      return;
    }

    if (String.join("", args).contains("<") || String.join("", args).contains(">")) {
      System.out.println("Illegal characters: \"<>\"");
      return;
    }

    Document document = Jsoup.parse(new File(args[0]), null, "", Parser.xmlParser());

    document.select("http-listener").attr("proxy-address-forwarding", "true");

    if (args.length > 1) {

      final Element dataSource = document.select("datasource[pool-name=\"KeycloakDS\"]").first();
      dataSource.attr("use-ccm", "true");

      dataSource.select("connection-url").first()
          .text(MessageFormat.format("jdbc:postgresql://{0}/{1}", args[1], args[2]));
      dataSource.select("driver").first().text("postgresql");

      final Element pool = new Element("pool");
      pool.appendChild(new Element("flush-strategy").text("IdleConnections"));
      dataSource.appendChild(pool);

      final Element validation = new Element("validation");
      validation.appendChild(new Element("check-valid-connection-sql").text("SELECT 1"));
      validation.appendChild(new Element("background-validation").text("true"));
      validation.appendChild(new Element("background-validation-millis").text("60000"));
      dataSource.appendChild(validation);

      final Elements security = dataSource.select("security");
      security.select("user-name").first().text(args[3]);
      security.select("password").first().text(args[4]);

      final Element drivers = document.select("drivers").first();

      final Element driver = new Element("driver");
      driver.attr("name", "postgresql");
      driver.attr("module", "org.postgresql.jdbc");
      driver.appendChild(new Element("xa-datasource-class").text("org.postgresql.xa.PGXADataSource"));

      drivers.appendChild(driver);

//    document.select("default-bindings").attr("datasource", "java:jboss/datasources/KeycloakDS");
    }

    Files.copy(Path.of(args[0]), Path.of(args[0] + ".back." + System.currentTimeMillis()));
    Files.writeString(Path.of(args[0]), document.html());
    System.out.println("Success");
  }
}
