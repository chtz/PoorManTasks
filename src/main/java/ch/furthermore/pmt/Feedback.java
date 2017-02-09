package ch.furthermore.pmt;

import org.hibernate.validator.constraints.NotBlank;

public class Feedback {
	@NotBlank
	private String comment;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
