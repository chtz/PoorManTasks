package ch.furthermore.pmt;

import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

public class Task {
	private String id;
	
	@Valid
	private List<Field> fields = new LinkedList<>();
	
	private String callbackUrl;

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
	}
	
	public void enrichWith(Task other) {
		callbackUrl = other.callbackUrl;
		for (int i = 0; i < fields.size(); i++) {
			Field f = fields.get(i);
			Field of = other.fields.get(i);
			f.setLabel(of.getLabel());
			f.setName(of.getName());
			f.setType(of.getType());
		}
	}
}
