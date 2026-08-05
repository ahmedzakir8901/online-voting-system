package com.voting.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeControllers {


    // 1. Dashboard Page
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("username", "JohnDoe");
        model.addAttribute("hasVoted", false);

        // Candidate model data
        List<Map<String, String>> candidates = new ArrayList<>();
        Map<String, String> c1 = new HashMap<>();
        c1.put("id", "1");
        c1.put("name", "Ahmed Loru");
        c1.put("party", "Pakistan Peoples Party");
        c1.put("description", "Focusing on Dalla Giri.");
        candidates.add(c1);

        model.addAttribute("candidates", candidates);
        return "dashboard";
    }

    // 2. Login Page (GET)
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // loads login.html
    }

    // 3. Signup Page (GET)
    @GetMapping({"/signup", "/register"})
    public String showSignupPage() {
        return "signup"; // loads signup.html
    }

    // 4. Signup Action (POST)
    @PostMapping("/signup")
    public String registerUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("cnic") String cnic, // Fixed: lowercase 'cnic' to match signup.html
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("role") String role,
            Model model) {

        // Validate that passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "signup"; // Returns back to signup.html displaying error
        }

        // --- Perform your registration / database logic here ---
        System.out.println("Registered User CNIC: " + cnic);
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Role: " + role);

        // Redirect to login page on success
        return "redirect:/login?success";
    }

    // 5. Login Action (POST)
    @PostMapping("/login")
    public String loginUser(
            @RequestParam("cnic") String cnic, // Fixed: lowercase 'cnic' to match login.html
            @RequestParam("password") String password, // Fixed: lowercase 'password' to match login.html
            Model model) {

        // --- Perform your authentication check here ---
        System.out.println("Logging in user CNIC: " + cnic);

        return "redirect:/dashboard"; // Redirects to dashboard after login
    }
}