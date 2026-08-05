package com.voting.Controllers;

import jakarta.servlet.http.HttpSession;
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

    // Simple Dummy User Model for In-Memory Testing without DB
    public static class SimpleUser {
        final String firstName;
        final String lastName;
        final String cnic;

        public SimpleUser(String firstName, String lastName, String cnic) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.cnic = cnic;
        }

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getCnic() { return cnic; }
    }

    // 0. Home Page Landing Route (Required for redirect:/)
    @GetMapping("/")
    public String home() {
        return "home"; // loads home.html
    }

    // 1. Dashboard Page
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        SimpleUser user = (SimpleUser) session.getAttribute("loggedInUser");

        // If not logged in, redirect to login page
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", user.getFirstName() + " " + user.getLastName());

        // Check if user has already voted during session
        Boolean hasVoted = (Boolean) session.getAttribute("hasVoted");
        model.addAttribute("hasVoted", hasVoted != null ? hasVoted : false);

        // Candidates list
        List<Map<String, String>> candidates = new ArrayList<>();

        Map<String, String> c1 = new HashMap<>();
        c1.put("id", "1");
        c1.put("name", "Ahmed Loru");
        c1.put("party", "Pakistan Peoples Party");
        c1.put("description", "Focusing on Development.");
        candidates.add(c1);

        Map<String, String> c2 = new HashMap<>();
        c2.put("id", "2");
        c2.put("name", "Haseeb Shamaraiz");
        c2.put("party", "PTI");
        c2.put("description", "Focusing on good work.");
        candidates.add(c2);

        model.addAttribute("candidates", candidates);
        return "dashboard"; // loads dashboard.html
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
            @RequestParam("cnic") String cnic,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("role") String role,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "signup";
        }

        System.out.println("Registered User CNIC: " + cnic);
        return "redirect:/login?success";
    }

    // 5. Login Action (POST) - Saves dummy user into session
    @PostMapping("/login")
    public String loginUser(
            @RequestParam("cnic") String cnic,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        // Store user in session so voting pages can access identity without DB
        SimpleUser user = new SimpleUser("John", "Doe", cnic);
        session.setAttribute("loggedInUser", user);

        return "redirect:/dashboard";
    }

    // 6. Logout Action
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Destroys session
        return "logout"; // loads logout.html (or use "redirect:/" for home)
    }

    // 7. SHOW VOTE CONFIRMATION PAGE (GET)
    @GetMapping("/vote")
    public String showVotePage(@RequestParam("candidateId") String candidateId, HttpSession session, Model model) {
        SimpleUser user = (SimpleUser) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        // Mock candidates database lookup by candidateId
        Map<String, String> candidate = new HashMap<>();
        if ("2".equals(candidateId)) {
            candidate.put("id", "2");
            candidate.put("name", "Haseeb Shamaraiz");
            candidate.put("party", "PTI");
            candidate.put("description", "Focusing on good work.");
        } else {
            candidate.put("id", "1");
            candidate.put("name", "Ahmed zakir");
            candidate.put("party", "Pakistan Peoples Party");
            candidate.put("description", "Focusing on Development.");
        }

        model.addAttribute("username", user.getFirstName() + " " + user.getLastName());
        model.addAttribute("candidate", candidate);

        return "vote"; // loads vote.html
    }

    // 8. PROCESS SUBMITTED VOTE (POST)
    @PostMapping("/vote")
    public String processVote(@RequestParam("candidateId") String candidateId, HttpSession session) {
        SimpleUser user = (SimpleUser) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        // Mark that the user has voted in the current session
        session.setAttribute("hasVoted", true);
        System.out.println("User CNIC " + user.getCnic() + " voted for candidate ID: " + candidateId);

        return "redirect:/dashboard?voted=true";
    }
}