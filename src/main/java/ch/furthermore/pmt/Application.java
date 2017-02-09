package ch.furthermore.pmt;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Build Samples:
 * <pre>
 * mvn package docker:build
 * docker push dockerchtz/PoorManTasks:latest
 * </pre>
 * 
 * Run Samples:
 * <pre>
 * docker run -p 8080:8080 -d dockerchtz/PoorManTasks
 * </pre>
 */
@SpringBootApplication
@Controller
public class Application {
	@ModelAttribute("items")
	public List<String> populateItems() {
	    return Arrays.asList(new String[]{"version: 1.0","hash: " + System.identityHashCode(this)});
	}
	
	@RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "nothing to see here (" + System.identityHashCode(this) + ")");
        return "index";
    }
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
