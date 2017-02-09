package ch.furthermore.pmt;

import java.io.IOException;

import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Sample
 * <pre>
 * curl -s -d '{"task":{"callbackUrl":"http://foo", "fields":[{"name":"foo","label":"Foo","value":"default","type":"INPUT"}]}}' -H"Content-Type:application/json" http://localhost:8080/tasks
 * </pre>
 */
@Controller
public class TaskController {
	@Autowired
	private TaskDAO taskDAO;
	
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
