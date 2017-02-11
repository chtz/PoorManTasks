package ch.furthermore.pmt;

import java.io.IOException;

import javax.script.ScriptException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Sample
 * <pre>
 * curl -s -d '{"task":{"callbackUrl":"http://foo", "fields":[{"name":"foo","label":"Foo","value":"default","type":"INPUT"}]}}' -H"Content-Type:application/json" http://localhost:8080/tasks
 * curl http://localhost:8080/tasks/dd35af46-0935-4316-9ef7-232ae6dbe705
 * </pre>
 */
@Controller
public class TaskController {
	@Autowired
	private TaskDAO taskDAO;
	
	@RequestMapping("/tasks/{taskId}")
    public String showTask(final Task task, @PathVariable("taskId") String taskId) throws JsonParseException, JsonMappingException, IOException {
		task.replaceWith(taskDAO.load(taskId));
		
        return "task";
    }
	
	@RequestMapping(value="/completion", method=RequestMethod.POST)
	public String saveTask(@Valid Task task, final BindingResult bindingResult, final ModelMap model) throws JsonParseException, JsonMappingException, IOException {
		task.enrichWith(taskDAO.load(task.getId()));
		
	    if (bindingResult.hasErrors()) {
	        return "task";
	    }
	    
	    return "redirect:/tasks/" + task.getId();
	}
	
	@RequestMapping(path="/tasks",method=RequestMethod.POST,consumes="application/json",produces="application/json")
	@ResponseBody
	CreateTaskResponse createTask(@RequestBody CreateTaskRequest request) throws NoSuchMethodException, IOException, ScriptException {
		CreateTaskResponse response = new CreateTaskResponse();
		try {
			taskDAO.insert(request.getTask());
			
			response.setSuccess(true);
			response.setTaskId(request.getTask().getId());
		}
		catch (Exception e) {
			response.setSuccess(false);
			response.setError(e.getMessage());
		}
		return response;
	}
}
