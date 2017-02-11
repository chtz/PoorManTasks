package ch.furthermore.pmt;

import org.hibernate.validator.constraints.NotBlank;

public class Field {
	private String name;
	
	private String label;
	
	@NotBlank
	private String value;
	
	private FieldType type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}
}
