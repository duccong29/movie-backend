package movies.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import movies.constant.PredefinedToken;
import movies.exception.AppException;
import movies.exception.ErrorCodes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    JavaMailSender mailSender;

    @NonFinal
    @Value("${spring.mail.username}")
    protected String fromEmailId;

    public void sendEmail(String to, String subject, String text, boolean isHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmailId);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, isHtml);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
    public void sendTokenEmail(String to, String token, String tokenType) {
        String subject;
        String actionUrl;

        if (PredefinedToken.VERIFICATION_TOKEN.equals(tokenType)) {
            subject = "Xác nhận email của bạn";
            actionUrl = "http://localhost:3000/confirm-email?token=" + token;
        } else if (PredefinedToken.PASSWORD_RESET_TOKEN.equals(tokenType)) {
            subject = "Đặt lại mật khẩu";
            actionUrl = "http://localhost:3000/reset-password?token=" + token;
        } else {
            throw new AppException(ErrorCodes.INVALID_TOKEN_TYPE);
        }

        String emailContent = String.format(
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                        "<h2 style='color: #2E86C1;'>%s</h2>" +
                        "<p>Vui lòng nhấp vào nút bên dưới để thực hiện thao tác:</p>" +
                        "<a href='%s' style='display: inline-block; padding: 10px 20px; background-color: #2E86C1; color: white; text-decoration: none; border-radius: 5px;'>Thực hiện ngay</a>" +
                        "<p style='margin-top: 20px;'>Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.</p>" +
                        "</div>",
                subject,
                actionUrl
        );

        sendEmail(to, subject, emailContent, true);
    }

//    public void sendConfirmationEmail(String to, String token) {
//        String subject = "Xác nhận email của bạn";
//        String confirmationUrl = "http://localhost:3000/confirm?token=" + token;
//        String text = String.format(
//                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
//                        "<h2 style='color: #2E86C1;'>Xác nhận email</h2>" +
//                        "<p>Vui lòng nhấp vào nút bên dưới để xác nhận email của bạn:</p>" +
//                        "<a href='%s' style='display: inline-block; padding: 10px 20px; background-color: #2E86C1; color: white; text-decoration: none; border-radius: 5px;'>Xác nhận Email</a>" +
//                        "<p style='margin-top: 20px;'>Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.</p>" +
//                        "</div>",
//                confirmationUrl
//        );
//        sendEmail(to, subject, text, true);
//    }
}
