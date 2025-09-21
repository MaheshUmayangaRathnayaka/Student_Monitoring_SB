package com.example.studentmonitor.controller;

import com.example.studentmonitor.model.User;
import com.example.studentmonitor.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    
    private final UserService userService;
    
    @Value("${spring.profiles.active:development}")
    private String activeProfile;
    
    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Show login page
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return "redirect:/";
        }
        
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        
        model.addAttribute("pageTitle", "Login - Student Monitor");
        return "auth/login";
    }
    
    /**
     * Show registration page
     */
    @GetMapping("/register")
    public String registrationPage(Model model) {
        
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return "redirect:/";
        }
        
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Register - Student Monitor");
        return "auth/register";
    }
    
    /**
     * Process registration form
     */
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") User user,
                                    BindingResult bindingResult,
                                    @RequestParam("confirmPassword") String confirmPassword,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        
        model.addAttribute("pageTitle", "Register - Student Monitor");
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        
        // Check password confirmation
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("confirmPasswordError", "Passwords do not match.");
            return "auth/register";
        }
        
        try {
            // Register user
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registration successful! You can now log in with your credentials.");
            return "redirect:/login";
            
        } catch (UserService.UserRegistrationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }
    
    /**
     * Check if username is available (AJAX endpoint)
     */
    @GetMapping("/api/check-username")
    @ResponseBody
    public boolean checkUsernameAvailability(@RequestParam String username) {
        return !userService.existsByUsername(username);
    }
    
    /**
     * Check if email is available (AJAX endpoint)
     */
    @GetMapping("/api/check-email")
    @ResponseBody
    public boolean checkEmailAvailability(@RequestParam String email) {
        return !userService.existsByEmail(email);
    }
    
    /**
     * Add common model attributes for all views
     */
    @ModelAttribute
    public void addCommonAttributes(Model model) {
        boolean isDevelopmentMode = "development".equals(activeProfile);
        model.addAttribute("isDevelopmentMode", isDevelopmentMode);
        model.addAttribute("h2ConsoleEnabled", h2ConsoleEnabled);
        
        // Add current user info if authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            model.addAttribute("currentUser", auth.getPrincipal());
            model.addAttribute("currentUsername", auth.getName());
        }
    }
}