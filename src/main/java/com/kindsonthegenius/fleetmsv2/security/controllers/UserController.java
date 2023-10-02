package com.kindsonthegenius.fleetmsv2.security.controllers;

import com.kindsonthegenius.fleetmsv2.parameters.models.Country;
import com.kindsonthegenius.fleetmsv2.security.models.User;
import com.kindsonthegenius.fleetmsv2.security.services.RoleService;
import com.kindsonthegenius.fleetmsv2.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/security/users")
    public String getAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "/security/users";
    }

    @GetMapping("/security/user/{op}/{id}")
    public String editUser(@PathVariable Integer id, @PathVariable String op, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("userRoles", roleService.getUserRoles(user));
        model.addAttribute("userNotRoles", roleService.getUserNotRoles(user));
        return "/security/user" + op; //returns employeeEdit or employeeDetails
    }

    @PostMapping("/users/addNew")
    public RedirectView addNew(
            User user,
            @RequestParam("photo") MultipartFile photoFile,
            UriComponentsBuilder uriBuilder,
            RedirectAttributes redir
    ) throws IOException {
        // Save the photo with the username as the filename
        if (!photoFile.isEmpty()) {
            String photoFileName = user.getUsername() + ".jpg";
            File photo = new File(resourceLoader.getResource("classpath:/static/img/users/").getFile(), photoFileName);
            photoFile.transferTo(photo);
        }

        // Save the user
        userService.save(user);

        // Set a flash attribute to indicate successful registration
        redir.addFlashAttribute("registrationSuccess", true);

        RedirectView redirectView = new RedirectView(uriBuilder.path("/login").build().toUriString(), true);
        return redirectView;
    }



    @RequestMapping(value = "/security/user/delete/{id}", method = {RequestMethod.GET, RequestMethod.DELETE})
    public String delete(@PathVariable Integer id) {
        // Get the user to determine the username for the photo file
        User user = userService.findById(id);

        // Delete the user's photo file if it exists
        String photoFileName = user.getUsername() + ".jpg";
        String photoFilePath = "src/main/resources/static/img/users/" + photoFileName;
        File photoFile = new File(photoFilePath);
        if (photoFile.exists()) {
            photoFile.delete();
        }

        // Delete the user
        userService.delete(id);

        return "redirect:/security/users";
    }



}
