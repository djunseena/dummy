package com.fsm.repositories.controllers;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsm.models.Users;
import com.fsm.repositories.UsersRepository;
import com.fsm.utility.HashUtil.SHA_256;

import lombok.Data;
import lombok.NoArgsConstructor;

@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping("api")
public class ForgotPasswordRepositoryController {

	@Autowired
	UsersRepository usersRepository;

	// email properties
	@Value("${spring.mail.properties.mail.smtp.starttls.enable}")
	private String tls;

	@Value("${spring.mail.username}")
	private String userName;

	@Value("${spring.mail.password}")
	private String password;

	@Value("${spring.mail.host}")
	private String host;

	@Value("${spring.mail.port}")
	private String port;

	@PutMapping("/forgotPassword")
	public HashMap<String, Object> sendingEmail(
			@Valid @RequestBody Email email)
			throws MessagingException, IOException {

		HashMap<String, Object> mapResult = new HashMap<String, Object>();

//		Patter email
		String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email.getTo());

		boolean isValid = false;
		boolean isTechnician = false;
		String message = "";
		LocalDateTime localNow = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		Timestamp dateNow = Timestamp.valueOf(formatter.format(localNow));
		String newPassword = generateRandomPassword(8);

		for (Users users : usersRepository.findAll()) {
			if(email.isMobile()) {
				if (users.getUserEmail().equalsIgnoreCase(email.getTo())) {
					if(users.getRoleId().getRoleId() == 8) {
						users.setUserPassword(SHA_256.digestAsHex(newPassword));
						users.setLastModifiedOn(dateNow);
						users.setCreatedBy(1);
						users.setLastModifiedBy(1);
						usersRepository.save(users);
						sendEmail(email, newPassword);
						isTechnician = true;
					} else {
						isTechnician = false;
					}
					isValid = true;
				} 
			} else { 
				if (users.getUserEmail().equalsIgnoreCase(email.getTo())) {
					users.setUserPassword(SHA_256.digestAsHex(newPassword));
					users.setLastModifiedOn(dateNow);
					users.setCreatedBy(1);
					users.setLastModifiedBy(1);
					usersRepository.save(users);
					sendEmail(email, newPassword);
					isValid = true;
				}
			}
		}
		
		if (isValid && matcher.matches() && !email.isMobile() || isValid && matcher.matches() && email.isMobile() && isTechnician) {
			message = "Email telah terkirim! Silahkan check email Anda!";
		} else if (!matcher.matches()) {
			message = "Penulisan alamat email salah!";
		} else if (!isTechnician && email.isMobile() && isValid) {
			message = "Maaf, Anda bukan teknisi!"; 
		} else if (!isValid) {
			message = "Gagal, alamat email belum terdaftar!";
		} 	

		mapResult.put("Message", message);
		return mapResult;
	}

	@Data
	@NoArgsConstructor
	private static class Email {
		private String to;
		private String messageSubject;
		private String messageBody;
		private boolean mobile = false;
	}
	
	// Method send Email
	private void sendEmail(Email email, String newPassword) throws AddressException, MessagingException, IOException {
		Properties props = new Properties();
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", tls);
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.debug", "true");
		String subject = "";
		String contentBody = "";
		subject = "Forgot Password!";
		contentBody = "<!doctype html>\n" + "<html>\n" + "  <head>\n"
				+ "    <meta name=\"viewport\" content=\"width=device-width\">\n"
				+ "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
				+ "    <title>Simple Transactional Email</title>\n" + "    <style>\n"
				+ "    @media only screen and (max-width: 620px) {\n" + "      table[class=body] h1 {\n"
				+ "        font-size: 28px !important;\n" + "        margin-bottom: 10px !important;\n"
				+ "      }\n" + "      table[class=body] p,\n" + "            table[class=body] ul,\n"
				+ "            table[class=body] ol,\n" + "            table[class=body] td,\n"
				+ "            table[class=body] span,\n" + "            table[class=body] a {\n"
				+ "        font-size: 16px !important;\n" + "      }\n" + "      table[class=body] .wrapper,\n"
				+ "            table[class=body] .article {\n" + "        padding: 10px !important;\n"
				+ "      }\n" + "      table[class=body] .content {\n" + "        padding: 0 !important;\n"
				+ "      }\n" + "      table[class=body] .container {\n" + "        padding: 0 !important;\n"
				+ "        width: 100% !important;\n" + "      }\n" + "      table[class=body] .main {\n"
				+ "        border-left-width: 0 !important;\n" + "        border-radius: 0 !important;\n"
				+ "        border-right-width: 0 !important;\n" + "      }\n"
				+ "      table[class=body] .btn table {\n" + "        width: 100% !important;\n" + "      }\n"
				+ "      table[class=body] .btn a {\n" + "        width: 100% !important;\n" + "      }\n"
				+ "      table[class=body] .img-responsive {\n" + "        height: auto !important;\n"
				+ "        max-width: 100% !important;\n" + "        width: auto !important;\n" + "      }\n"
				+ "    }\n" + "\n" + "    @media all {\n" + "      .ExternalClass {\n"
				+ "        width: 100%;\n" + "      }\n" + "      .ExternalClass,\n"
				+ "            .ExternalClass p,\n" + "            .ExternalClass span,\n"
				+ "            .ExternalClass font,\n" + "            .ExternalClass td,\n"
				+ "            .ExternalClass div {\n" + "        line-height: 100%;\n" + "      }\n"
				+ "      .apple-link a {\n" + "        color: inherit !important;\n"
				+ "        font-family: inherit !important;\n" + "        font-size: inherit !important;\n"
				+ "        font-weight: inherit !important;\n" + "        line-height: inherit !important;\n"
				+ "        text-decoration: none !important;\n" + "      }\n" + "      #MessageViewBody a {\n"
				+ "        color: inherit;\n" + "        text-decoration: none;\n"
				+ "        font-size: inherit;\n" + "        font-family: inherit;\n"
				+ "        font-weight: inherit;\n" + "        line-height: inherit;\n" + "      }\n"
				+ "      .btn-primary table td:hover {\n" + "        background-color: #34495e !important;\n"
				+ "      }\n" + "      .btn-primary a:hover {\n"
				+ "        background-color: #34495e !important;\n" + "        border-color: #34495e !important;\n"
				+ "      }\n" + "    }\n" + "    </style>\n" + "  </head>\n"
				+ "  <body class=\"\" style=\"background-color: #f6f6f6; font-family: sans-serif; -webkit-font-smoothing: antialiased; font-size: 14px; line-height: 1.4; margin: 0; padding: 0; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;\">\n"
				+ "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"body\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%; background-color: #f6f6f6;\">\n"
				+ "      <tr>\n"
				+ "        <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\">&nbsp;</td>\n"
				+ "        <td class=\"container\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; display: block; Margin: 0 auto; max-width: 580px; padding: 10px; width: 580px;\">\n"
				+ "          <div class=\"content\" style=\"box-sizing: border-box; display: block; Margin: 0 auto; max-width: 580px; padding: 10px;\">\n"
				+ "\n" + "            <!-- START CENTERED WHITE CONTAINER -->\n"
				+ "            <span class=\"preheader\" style=\"color: transparent; display: none; height: 0; max-height: 0; max-width: 0; opacity: 0; overflow: hidden; mso-hide: all; visibility: hidden; width: 0;\">This is preheader text. Some clients will show this text as a preview.</span>\n"
				+ "            <table class=\"main\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%; background: #ffffff; border-radius: 3px;\">\n"
				+ "\n" + "              <!-- START MAIN CONTENT AREA -->\n" + "              <tr>\n"
				+ "                <td class=\"wrapper\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; box-sizing: border-box; padding: 20px;\">\n"
				+ "                  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;\">\n"
				+ "                    <tr>\n"
				+ "                      <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\">\n"
				+ "                        <p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; Margin-bottom: 15px;\">Halo,</p>\n"
				+ "                        <p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; Margin-bottom: 15px;\">Sepertinya kamu lupa akan passwordmu sendiri. Kami telah memberikanmu password sementara agar kamu tetap bisa login. Untuk merubahnya, kamu bisa menggunakan ubah password yang ada di halaman detail akun.</p>\n"
				+ "                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"btn btn-primary\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%; box-sizing: border-box;\">\n"
				+ "                          <tbody>\n" + "                            <tr>\n"
				+ "                              <td align=\"left\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; padding-bottom: 15px;\">\n"
				+ "                                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: auto;\">\n"
				+ "                                  <tbody>\n"
				+ "                                    <p><b>Password : " + newPassword + " </b></p>\n"
				+ "                                  </tbody>\n" + "                                </table>\n"
				+ "                              </td>\n" + "                            </tr>\n"
				+ "                          </tbody>\n" + "                        </table>\n"
				+ "                        <p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; Margin-bottom: 15px;\">Bila Anda mengalami masalah saat melakukan login, Anda dapat menghubungi kami melalui email padepokan79.testing@gmail.com</p>\n"
				+ "                        <p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; Margin-bottom: 15px;\">Terima Kasih</p>\n"
				+ "                      </td>\n" + "                    </tr>\n" + "                  </table>\n"
				+ "                </td>\n" + "              </tr>\n" + "\n" + "            </table>\n" + "\n"
				+ "            <!-- START FOOTER -->\n"
				+ "            <div class=\"footer\" style=\"clear: both; Margin-top: 10px; text-align: center; width: 100%;\">\n"
				+ "              <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; width: 100%;\">\n"
				+ "                <tr>\n"
				+ "                  <td class=\"content-block\" style=\"font-family: sans-serif; vertical-align: top; padding-bottom: 10px; padding-top: 10px; font-size: 12px; color: #999999; text-align: center;\">\n"
				+ "                    <span class=\"apple-link\" style=\"color: #999999; font-size: 12px; text-align: center;\">Gg. Terasana No.6A, Pasir Kaliki, Kec. Cicendo, Kota Bandung, Jawa Barat 40171</span>\n"
				+ "                    <br> Don't like these emails? <a href=\"http://i.imgur.com/CScmqnj.gif\" style=\"text-decoration: underline; color: #999999; font-size: 12px; text-align: center;\">Unsubscribe</a>.\n"
				+ "                  </td>\n" + "                </tr>\n" + "                <tr>\n"
				+ "                  <td class=\"content-block powered-by\" style=\"font-family: sans-serif; vertical-align: top; padding-bottom: 10px; padding-top: 10px; font-size: 12px; color: #999999; text-align: center;\">\n"
				+ "                    Design by <a href=\"https://tujuhsembilan.com/\" style=\"color: #999999; font-size: 12px; text-align: center; text-decoration: none;\">Padepokan 79</a>.\n"
				+ "                  </td>\n" + "                </tr>\n" + "              </table>\n"
				+ "            </div>\n" + "\n" + "          </div>\n" + "        </td>\n"
				+ "        <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\">&nbsp;</td>\n"
				+ "      </tr>\n" + "    </table>\n" + "  </body>\n" + "</html>\n" + "";
		System.out.println(email.getTo());
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		});
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(userName, false));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getTo()));
		msg.setSubject(subject);
		msg.setContent(contentBody, "text/html");
		msg.setSentDate(new Date());
		Transport.send(msg);
	}

	// Method Random char
	public static String generateRandomPassword(int len) {
		
		final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {
			int randomIndex = random.nextInt(chars.length());
			sb.append(chars.charAt(randomIndex));
		}

		return sb.toString();
	}
}