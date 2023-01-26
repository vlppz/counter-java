package clc.vlppz.counterjava;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@SpringBootApplication
@RestController
public class CounterjavaApplication {

	public Map<String, String> users = new HashMap<>();

	public static Configuration fmconfig = new Configuration(Configuration.VERSION_2_3_32);
	public static void main(String[] args) {
		SpringApplication.run(CounterjavaApplication.class, args);
		ClassPathResource resource = new ClassPathResource("/templates");

		try {
			fmconfig.setDirectoryForTemplateLoading(new File(resource.getURI()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/")
	public String index() {
		Template t = null;
		try {
			t = fmconfig.getTemplate("index.html");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> root = new HashMap<>();
		root.put("ans", "");
		root.put("sh", "false");
		root.put("reglog", "");

		Writer wr = new StringWriter();
		try {
			t.process(root, wr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return wr.toString();

	}

	@PostMapping("/regusr")
	public String regusr(@RequestParam(name = "login") String login, @RequestParam(name = "pass") String pass) {
		Template t = null;
		String ans = "";

		try {
			t = fmconfig.getTemplate("index.html");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (users.get(login) == null) {
			users.put(login, pass);
			ans = "Registered as: "+login;
		} else {
			ans = "This user is already registered!";
		}

		Map<String, Object> root = new HashMap<>();
		root.put("ans", ans);
		root.put("sh", "true");
		root.put("reglog", "Registration info");

		Writer wr = new StringWriter();
		try {
			t.process(root, wr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return wr.toString();

	}

	@PostMapping("/logusr")
	public String logsr(@RequestParam(name = "login") String login, @RequestParam(name = "pass") String pass) {
		Template t = null;
		String ans = "";

		try {
			if (users.get(login).equals(pass)) {
				ans = login;
			} else {
				ans = "Incorrect username or password!";
			}
		} catch (Exception e) {
			ans = "Incorrect username or password!";
		}

		try {
			if (ans.equals(login)) {
				t = fmconfig.getTemplate("cnt.html");
			} else {
				t = fmconfig.getTemplate("index.html");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> root = new HashMap<>();
		
		if (ans.equals(login)) {
			root.put("usr", ans);
		} else {
			root.put("ans", ans);
			root.put("sh", "true");
			root.put("reglog", "Log in info");
		}

		Writer wr = new StringWriter();
		try {
			t.process(root, wr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return wr.toString();

	}

	@GetMapping("/css")
	public String css() {
		ClassPathResource cssf = new ClassPathResource("/static/style.css");

		try {
			return Files.readString(Path.of(cssf.getURI()));
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}

}
