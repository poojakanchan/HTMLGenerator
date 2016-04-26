import java.io.IOException;

public class Application {

	public static void main(String[] args) throws IOException {

		HTMLGenerator generator = new HTMLGenerator("resource/html_spec.json", "resource/output.html");
		generator.generate();
	}
}
