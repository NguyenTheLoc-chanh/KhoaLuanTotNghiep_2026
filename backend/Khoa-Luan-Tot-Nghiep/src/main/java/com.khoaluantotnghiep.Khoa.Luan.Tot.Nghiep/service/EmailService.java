package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    // Load file template từ resources/templates
    private String loadTemplate(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/" + filename);
            return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc template email: " + filename, e);
        }
    }

    public void sendResetPasswordEmail(String to, String resetLink, int expiryMinutes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail); // email cấu hình
            helper.setTo(to);
            helper.setSubject("Đặt lại mật khẩu của bạn");

            // Load template HTML
            String template = loadTemplate("reset-password-email-template.html");

            // Thay thế placeholder trong template
            String htmlContent = template
                    .replace("${email}", to)
                    .replace("${expiryMinutes}", String.valueOf(expiryMinutes))
                    .replace("${resetLink}", resetLink)
                    .replace("${year}", String.valueOf(Year.now().getValue()));

            helper.setText(htmlContent, true); // true = gửi HTML

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại", e);
        }
    }
    /**
     * Gửi email thông báo trạng thái tin tuyển dụng.
     * @param to Email người nhận (nhà tuyển dụng)
     * @param jobTitle Tiêu đề tin tuyển dụng
     * @param newStatus Trạng thái mới (ACTIVE, LOCKED)
     */
    public void sendJobPostingStatusUpdateEmail(String to, String jobTitle, String newStatus) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Thông báo cập nhật trạng thái tin tuyển dụng: " + jobTitle);

            String template = loadTemplate("job-posting-status-email-template.html");

            // Thay thế các placeholder
            String htmlContent = template
                    .replace("${jobTitle}", jobTitle)
                    .replace("${newStatus}", newStatus)
                    .replace("${year}", String.valueOf(Year.now().getValue()));

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thông báo trạng thái tin tuyển dụng thất bại", e);
        }
    }
    public void sendInterviewLetter(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            helper.setText(content, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Gửi thư mời phỏng vấn thất bại", e);
        }
    }
    public void sendFeedbackReplyEmail(String to, String replyContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Phản hồi của bạn đã được xử lý");

            // Bạn có thể dùng template hoặc gửi trực tiếp
            String template = loadTemplate("feedback-reply-email-template.html");

            String htmlContent = template
                    .replace("${replyContent}", replyContent)
                    .replace("${year}", String.valueOf(Year.now().getValue()));

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email phản hồi thất bại", e);
        }
    }
}
