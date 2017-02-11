package ch.furthermore.pmt;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	private final Logger logger = LoggerFactory.getLogger(Application.class);
	
	@ModelAttribute("title")
	public String populateTitle() {
		return "poor-man-tasks";
	}
	
	@ModelAttribute("items")
	public List<String> populateItems() {
	    return Arrays.asList(new String[]{"version: 1.0"});
	}
	
	@RequestMapping("/")
    public String index(final Feedback feedback, Model model) {
//        model.addAttribute("title", "nothing to see here (" + System.identityHashCode(this) + ")");
        return "index";
    }
	
	@RequestMapping(value="/", method=RequestMethod.POST)
	public String saveFeedback(@Valid Feedback feedback, final BindingResult bindingResult, final ModelMap model) {
	    if (bindingResult.hasErrors()) {
//	    	model.addAttribute("title", "Comment must not be empty");
	    	
	        return "index";
	    }
	    
	    logger.info("Got comment: {}", feedback.getComment());
	    
//	    model.clear();
	    return "redirect:/";
	}
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
