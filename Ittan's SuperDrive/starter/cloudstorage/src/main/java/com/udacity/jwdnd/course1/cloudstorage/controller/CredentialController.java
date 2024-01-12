package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@RequestMapping("/home/credentials")
@Controller
public class CredentialController {
    private CredentialsService credentialsService;
    private UserMapper userMapper;

    public CredentialController(CredentialsService credentialsService, UserMapper userMapper) {
        this.credentialsService = credentialsService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public String handleAddUpdateCredentials(Authentication authentication, Credentials credentials){
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        if (credentials == null || !isValidUrl(credentials.getUrl()) || !isValidUsername(credentials.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials data. Please check your input.");
            return "redirect:/result?error";
        }

        String loggedInUserName = (String) authentication.getPrincipal();
        User user = userMapper.getUser(loggedInUserName);
        Integer userId = user.getUserId();

        if (credentials.getCredentialid() != null) {
            credentialsService.editCredentials(credentials);
        } else {
            credentialsService.addCredentials(credentials, userId);
        }

        return "redirect:/result?success";
    }
    private boolean isValidUrl(String url) {
        return url != null && !url.isEmpty() && url.length() <= 255;
    }

    private boolean isValidUsername(String username) {
        return username != null && !username.isEmpty() && username.length() <= 255;
    }


    @GetMapping("/delete")
    public String deleteCredentials(@RequestParam("id") int credentialid, Authentication authentication, RedirectAttributes redirectAttributes){
        String loggedInUserName = (String) authentication.getPrincipal();
        User user = userMapper.getUser(loggedInUserName);

        try{
            if(credentialid > 0){
                credentialsService.deleteCredentials(credentialid);
                return "redirect:/result?success";
            }
        }
        catch (Exception e) {


            redirectAttributes.addAttribute("error", "Unable to delete the credentials.");

        }
        return "redirect:/result?error";
    }
}