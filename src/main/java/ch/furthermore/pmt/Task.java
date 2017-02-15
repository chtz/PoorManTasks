package ch.furthermore.pmt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

public class Task {
	private String id;
	
	@Valid
	private List<Field> fields = new LinkedList<>();
	
	private String callbackUrl;
	
	private String email;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	
	public void replaceWith(Task other) {
		id = other.id;
		fields = other.fields;
		callbackUrl = other.callbackUrl;
		email = other.email;
	}
	
	public void enrichWith(Task other) {
		callbackUrl = other.callbackUrl;
		email = other.email;
		for (int i = 0; i < fields.size(); i++) {
			Field f = fields.get(i);
			Field of = other.fields.get(i);
			f.setLabel(of.getLabel());
			f.setName(of.getName());
			f.setType(of.getType());
		}
	}

	public Map<String,String> toMap() {
		Map<String,String> result = new HashMap<>();
		for (Field f : fields) {
			result.put(f.getName(), f.getValue());
		}
		
		return result;
	}
}
