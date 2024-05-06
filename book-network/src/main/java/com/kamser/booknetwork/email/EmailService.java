package com.kamser.booknetwork.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//In this class is where I going to use the thymeleaf dependency to the email.
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async //This is to performe the mail sending, without blocking the server. I have to activate this functionality in the BookNetworkApiApplication, the root file.
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplateName,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {
        String templateName;
        if(emailTemplateName == null){
            templateName = "confirm-email";
        } else {
            templateName = emailTemplateName.getName();
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper =  new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        //Now I going to create the map to set the properties for the email.
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

        Context context =  new Context();
        context.setVariables(properties);

        helper.setFrom("keylor.morataya@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        /*
        * In the next step is where spring and thymeleaf will looking for the teamplate in the "templates" folder below the resources folder,
        * in this folder it going to search the teamplete for this email, which, for this case, will be the "activate_account.html", which is related with
        * the EmailTemplateName, an enum that I created.
        * */
        String template = templateEngine.process(templateName, context);

        helper.setText(template, true);

        mailSender.send(mimeMessage);


    }

}
