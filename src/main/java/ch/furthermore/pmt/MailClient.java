package ch.furthermore.pmt;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailClient {
	private final static Logger logger = LoggerFactory.getLogger(MailClient.class);

	@Value("${mail.drymode}")
	private boolean drymode;

	@Value("${mail.host}")
	private String host;

	@Value("${mail.socketFactory.port}")
	private String socketFactoryPort;

	@Value("${mail.socketFactory.class}")
	private String socketFactoryClass;

	@Value("${mail.auth}")
	private String auth;

	@Value("${mail.port}")
	private String port;

	@Value("${mail.user}")
	private String user;

	@Value("${mail.password}")
	private String password;

	@Value("${mail.from}")
	private String from;

	public boolean send(final String to, final String subject, final String text) {
		final Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.socketFactory.port", socketFactoryPort);
		props.put("mail.smtp.socketFactory.class", socketFactoryClass);
		props.put("mail.smtp.auth", auth);
		props.put("mail.smtp.port", port);

		// some props in appendix A: http://www.oracle.com/technetwork/java/javamail-1-149769.pdf
		// more props here: https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
		props.put("mail.smtp.connectiontimeout", "5000");
		props.put("mail.smtp.timeout", "5000");
		props.put("mail.smtp.writetimeout", "5000");

		try {

			final Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, password);
				}
			});

			final Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(text);

			if (!drymode) {
				Transport.send(message);

				logger.debug("Mail sent to {}", to);
			} else {
				logger.warn("Dry-mail {}={} NOT sent to {}", subject, text, to);
			}

			return true;
		} catch (Exception e) {
			logger.error("Could not send email ({})", props, e);

			return false;
		}
	}
}
