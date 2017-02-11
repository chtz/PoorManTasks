package ch.furthermore.pmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	@RequestMapping("/")
    public String index(Model model) {
        return "index";
    }
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
