package pl.olszewski.Blog.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pl.olszewski.Blog.dto.UserRegistrationDto;
import pl.olszewski.Blog.service.AuthorInMemoryService;
import pl.olszewski.Blog.service.Menagers.AuthorManager;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller("registrationcontroller")
@RequestMapping("/registration")
public class RegistrationController {

    private AuthorManager authorManager;
    private final JavaMailSender emailSender;
    private SpringTemplateEngine templateEngine;

    @Autowired
    public RegistrationController(AuthorManager authorManager,
                                  SpringTemplateEngine templateEngine,
                                  JavaMailSender emailSender){
        this.authorManager = authorManager;
        this.templateEngine = templateEngine;
        this.emailSender = emailSender;
    }

    @PostMapping
    public String registrationAuthor(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto, BindingResult bindingResult) throws MessagingException {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        authorManager.save(registrationDto);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();
        Map<String, Object> map  = new HashMap<String, Object>() {{
            put("user", registrationDto);
        }};
        context.setVariables(map);
        helper.setTo(registrationDto.getEmail());
        helper.setSubject("Create Account");
        String html = templateEngine.process("mailContent", context);
        helper.setText(html, true);
        emailSender.send(message);

        return "redirect:/registration?success";
    }

    @GetMapping
    public String showRegistrationForm() {
        return "registration";
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }
}