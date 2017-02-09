package ch.furthermore.pmt;

import java.util.LinkedList;
import java.util.List;

public class Task {
	private String id;
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
}
