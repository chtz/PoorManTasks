package ch.furthermore.pmt;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskDAO {
	@Value(value="${storagePath:taskstor}")
	private String storagePath;
	private File storage;
	
	@PostConstruct
	public void init() {
		storage = new File(storagePath);
		storage.mkdirs();
	}

	public Task load(String id) throws JsonParseException, JsonMappingException, IOException {
		File taskFile = new File(storage, UUID.fromString(id).toString() + ".json");
		
		ObjectMapper om = new ObjectMapper();
		return om.readValue(taskFile, Task.class);
	}
	
	public void insert(Task task) throws JsonGenerationException, JsonMappingException, IOException {
		task.setId(task.getId() == null ? UUID.randomUUID().toString() : UUID.fromString(task.getId()).toString());
		
		File taskFile = new File(storage, task.getId() + ".json");
		
		ObjectMapper om = new ObjectMapper();
		om.writeValue(taskFile, task);
	}
}
