package org.churuata.digital.authentication.mail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.condast.commons.authentication.core.LoginData;
import org.condast.commons.authentication.user.ILoginUser;
import org.condast.commons.parser.AbstractResourceParser;
import org.condast.commons.preferences.config.Config;
import org.condast.commons.strings.StringStyler;


/**
 * @See: https://www.baeldung.com/java-email
 * @author info
 *
 */
public class MailUtils {

	public static final String S_CONDAST_EMAIL = "condast.auth@gmail.com";
	public static final String S_CONDAST_PASSWORD = "Kl00st3rtu1n@2012";
	public static final String S_RESOURCE_CONFIRM = "/resources/confirmation.txt";
	public static final String S_RESOURCE_CONFIRM_CODE = "/resources/confirmcode.txt";
	public static final String S_RESOURCE_FORGOT_PASSWORD_CODE = "/resources/forgot.txt";

	public static final String S_PLEASE_CONFIRM_TITLE = "Please Confirm your Registration:";
	public static final String S_DEFAULT_MAIL_RESOURCE = null;

	private enum Parameters{
		NAME,
		CONFIRMATION,
		FORGOTTEN,
		DOMAIN;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
		
		public static Parameters getParameter( String str ){
			return Parameters.valueOf( StringStyler.styleToEnum(str));
		}

	}
	
	public static void sendConfirmationdMail( LoginData login, String recipient, String domain, long confirmation ) throws Exception {
		String msg = createConfirmationMail( login, domain, confirmation);
		sendMail(S_CONDAST_EMAIL, recipient, S_PLEASE_CONFIRM_TITLE, msg);
	}

	public static void sendConfirmCodeMail( ILoginUser user, String domain ) throws Exception {
		String msg = createConfirmCodeMail( user, domain);
		sendMail(S_CONDAST_EMAIL, user.getEmail(), S_PLEASE_CONFIRM_TITLE, msg);
	}

	public static void sendForgotPasswordMail( ILoginUser user, String domain ) throws Exception {
		String msg = createForgotPasswordMail( user, domain);
		sendMail(S_CONDAST_EMAIL, user.getEmail(), S_PLEASE_CONFIRM_TITLE, msg);
	}

	public static void sendMail( String recipient, String subject, String msg ) throws AddressException, MessagingException {
		sendMail(S_CONDAST_EMAIL, recipient, subject, msg);
	}
	
	public static void sendMail( String host, String recipient, String subject, String msg ) throws AddressException, MessagingException {
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		
		Session session = Session.getInstance(prop, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(host, S_CONDAST_PASSWORD);
		    }
		});
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress( host ));
		message.setRecipients(
		  Message.RecipientType.TO, InternetAddress.parse( recipient ));
		message.setSubject( subject );

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		message.setContent(multipart);
		Transport.send(message);
	}
	
	public static String createConfirmationMail( LoginData login, String domain, long confirmation ) throws IOException {
		FileParser parser = new FileParser( login, domain, confirmation);
		return parser.parse( MailUtils.class.getResourceAsStream(S_RESOURCE_CONFIRM) );
	}

	public static String createConfirmCodeMail( ILoginUser user, String domain ) throws IOException {
		FileParser parser = new FileParser( new LoginData( user ), domain, user.getSecurity());
		return parser.parse( MailUtils.class.getResourceAsStream(S_RESOURCE_CONFIRM_CODE) );
	}

	public static String createForgotPasswordMail( ILoginUser user, String domain ) throws IOException {
		FileParser parser = new FileParser( new LoginData( user ), domain, user.getSecurity());
		return parser.parse( MailUtils.class.getResourceAsStream(S_RESOURCE_FORGOT_PASSWORD_CODE) );
	}

	private static class FileParser extends AbstractResourceParser{

		private long confirmation;
		private LoginData login;
		private String domain;
				
		public FileParser(LoginData login, String domain, long security) {
			super();
			this.confirmation = security;
			this.login = login;
			this.domain = domain;
		}

		@Override
		protected String onHandleTitle(String subject, Attributes attr) {
			String result = null;
			switch( attr ){
			case HTML:
				result = "Condast Mail";
				break;
			case PAGE:
				result = "Condast Mail";
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onAppendEOL() {
			return "<BR>";
		}

		@Override
		protected String onHandleLabel(String id, Attributes attr) {
			String result = null;
			Config config = Config.getInstance();
			switch( Parameters.getParameter(id)) {
			case NAME:
				result = login.getNickName();
				break;
			case CONFIRMATION:
				result = "<a href=" + config.getServerContext() + "condast/rest/auth/confirm-registration?confirm=" + this.confirmation + ">confirm</a>";
				break;
			case FORGOTTEN:
				result = "<a href=" + config.getServerContext() + "condast/rest/auth/restore-password?confirm=" + this.confirmation + ">confirm</a>";
				break;
			case DOMAIN:
				result = domain;
				break;
			default:
				break;
			}
			return result;
		}

		@Override
		protected String onCreateLink(String link, String url, String arguments) {
			return null;
		}

		@Override
		protected String getToken() {
			return String.valueOf(-1);
		}		
	}

}
