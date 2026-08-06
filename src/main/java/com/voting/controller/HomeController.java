package com.voting.controller;

import jakarta.servlet.RequestDispatcher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class HomeController {

    // ==========================================
    // IN-MEMORY DUMMY STORAGE (NO DB / NO DTOS)
    // ==========================================

    // Dynamic candidate counter for generating new IDs
    private final AtomicLong candidateIdCounter = new AtomicLong(3);

    // List of candidates stored as Maps to avoid needing a Candidate Class/DTO
    private final List<Map<String, Object>> candidateList = new ArrayList<>();

    // Map of users: Key = CNIC, Value = User details Map
    private final Map<String, Map<String, Object>> userMap = new ConcurrentHashMap<>();

    // Track currently logged-in user (Defaulted for testing)
    private String currentLoggedInCnic = "42101-1234567-1";

    public HomeController() {
        // --- 1. DUMMY USERS SETUP ---
        Map<String, Object> user1 = new HashMap<>();
        user1.put("fullName", "Alex Morgan");
        user1.put("cnic", "42101-1234567-1");
        user1.put("password", "password123");
        user1.put("role", "VOTER");
        user1.put("hasVoted", false);
        userMap.put("42101-1234567-1", user1);

        Map<String, Object> adminUser = new HashMap<>();
        adminUser.put("fullName", "System Admin");
        adminUser.put("cnic", "00000-0000000-0");
        adminUser.put("password", "admin123");
        adminUser.put("role", "ADMIN");
        adminUser.put("hasVoted", false);
        userMap.put("00000-0000000-0", adminUser);

        // --- 2. DUMMY CANDIDATES SETUP ---
        addDummyCandidate(1L, "John Smith", "Justice Party", "★", "Focusing on economic growth and education reform.", 12);
        addDummyCandidate(2L, "Sarah Jenkins", "Alliance Party", "🦁", "Building sustainable public transport and green energy.", 18);
        addDummyCandidate(3L, "David Miller", "Progressive Front", "⚡", "Modernizing healthcare and digital infrastructure.", 8);
    }

    private void addDummyCandidate(Long id, String name, String party, String symbol, String manifesto, int initialVotes) {
        Map<String, Object> candidate = new HashMap<>();
        candidate.put("id", id);
        candidate.put("name", name);
        candidate.put("partyName", party);
        candidate.put("symbol", symbol);
        candidate.put("manifesto", manifesto);
        candidate.put("voteCount", new AtomicInteger(initialVotes));
        candidateList.add(candidate);
    }

    // ==========================================
    // 1. LANDING & AUTHENTICATION ROUTING
    // ==========================================

    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("totalCandidates", candidateList.size());
        model.addAttribute("totalVotes", getTotalVotesCount());
        return "home";
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid CNIC or Password.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("cnic") String cnic,
                               @RequestParam("password") String password,
                               RedirectAttributes redirectAttributes) {
        Map<String, Object> user = userMap.get(cnic);
        if (user != null && user.get("password").equals(password)) {
            this.currentLoggedInCnic = cnic;
            if ("ADMIN".equals(user.get("role"))) {
                return "redirect:/admin";
            }
            return "redirect:/dashboard";
        }
        redirectAttributes.addAttribute("error", "true");
        return "redirect:/login";
    }

    @GetMapping({"/signup", "/register"})
    public String showSignupPage() {
        return "signup"; // Or "register" depending on your HTML file name
    }

    @PostMapping({"/signup", "/register"})
    public String processRegistration(@RequestParam Map<String, String> formData,
                                      RedirectAttributes redirectAttributes) {

        // Extract values safely regardless of whether HTML input is named 'fullName', 'name', or 'full_name'
        String fullName = formData.getOrDefault("fullName",
                formData.getOrDefault("name",
                        formData.getOrDefault("full_name", "Anonymous Voter")));

        String cnic = formData.get("cnic");
        String password = formData.get("password");

        // Validate inputs
        if (cnic == null || cnic.isBlank() || password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Please provide both CNIC and Password!");
            return "redirect:/signup";
        }

        // Save user to dummy in-memory storage
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("fullName", fullName);
        newUser.put("cnic", cnic);
        newUser.put("password", password);
        newUser.put("role", "VOTER");
        newUser.put("hasVoted", false);

        userMap.put(cnic, newUser);

        redirectAttributes.addFlashAttribute("message", "Registration successful! Please log in.");
        return "redirect:/login";
    }


    @GetMapping("/logout")
    public String processLogout(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully.");
        return "redirect:/login?logout=true";
    }

    // ==========================================
    // 2. VOTER DASHBOARD & VOTING ROUTING
    // ==========================================

    @GetMapping({"/dashboard", "/voter"})
    public String showDashboard(Model model) {
        Map<String, Object> currentUser = userMap.getOrDefault(currentLoggedInCnic, userMap.get("42101-1234567-1"));

        model.addAttribute("user", currentUser);
        model.addAttribute("candidates", candidateList);
        model.addAttribute("hasVoted", currentUser.get("hasVoted"));

        return "dashboard";
    }

    @PostMapping("/vote")
    public String processVote(@RequestParam("candidateId") Long candidateId, RedirectAttributes redirectAttributes) {
        Map<String, Object> currentUser = userMap.get(currentLoggedInCnic);

        if (currentUser != null && (Boolean) currentUser.get("hasVoted")) {
            redirectAttributes.addFlashAttribute("error", "You have already cast your vote!");
            return "redirect:/dashboard";
        }

        // Increment candidate vote count
        for (Map<String, Object> candidate : candidateList) {
            if (candidate.get("id").equals(candidateId)) {
                ((AtomicInteger) candidate.get("voteCount")).incrementAndGet();
                break;
            }
        }

        // Mark user as voted
        if (currentUser != null) {
            currentUser.put("hasVoted", true);
        }

        redirectAttributes.addFlashAttribute("success", "Your vote was cast successfully!");
        return "redirect:/results";
    }

    // ==========================================
    // 3. LIVE RESULTS ROUTING
    // ==========================================

    @GetMapping("/results")
    public String showResults(Model model) {
        int totalVotes = getTotalVotesCount();

        // Build result objects on the fly with percentages
        List<Map<String, Object>> results = new ArrayList<>();
        for (Map<String, Object> c : candidateList) {
            int votes = ((AtomicInteger) c.get("voteCount")).get();
            int percentage = totalVotes > 0 ? (int) Math.round(((double) votes / totalVotes) * 100) : 0;

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("candidate", c);
            resultMap.put("percentage", percentage);
            resultMap.put("voteCount", votes);
            results.add(resultMap);
        }

        // Sort descending by votes
        results.sort((a, b) -> Integer.compare((int) b.get("voteCount"), (int) a.get("voteCount")));

        String leadingParty = results.isEmpty() ? "N/A" :
                ((Map<String, Object>) results.get(0).get("candidate")).get("partyName").toString();

        model.addAttribute("results", results);
        model.addAttribute("totalVotes", totalVotes);
        model.addAttribute("totalCandidates", candidateList.size());
        model.addAttribute("leadingParty", leadingParty);

        return "results";
    }

    // ==========================================
    // 4. ADMIN PANEL ROUTING & ACTIONS
    // ==========================================

    @GetMapping("/admin")
    public String showAdminDashboard(Model model) {
        int totalVoters = userMap.size();
        int totalVotes = getTotalVotesCount();
        double turnout = totalVoters > 0 ? ((double) totalVotes / totalVoters) * 100 : 0.0;

        model.addAttribute("candidates", candidateList);
        model.addAttribute("totalVoters", totalVoters);
        model.addAttribute("totalVotes", totalVotes);
        model.addAttribute("turnoutPercentage", String.format("%.1f", turnout));

        return "admin";
    }

    @PostMapping("/admin/candidate/add")
    public String addCandidate(@RequestParam("name") String name,
                               @RequestParam("partyName") String partyName,
                               @RequestParam("symbol") String symbol,
                               @RequestParam("manifesto") String manifesto,
                               RedirectAttributes redirectAttributes) {

        Long newId = candidateIdCounter.incrementAndGet();
        addDummyCandidate(newId, name, partyName, symbol, manifesto, 0);

        redirectAttributes.addFlashAttribute("message", "Candidate added successfully!");
        return "redirect:/admin";
    }

    @PostMapping("/admin/candidate/delete/{id}")
    public String deleteCandidate(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        candidateList.removeIf(c -> c.get("id").equals(id));
        redirectAttributes.addFlashAttribute("message", "Candidate removed!");
        return "redirect:/admin";
    }

    // ==========================================
    // HELPER METHOD
    // ==========================================

    private int getTotalVotesCount() {
        int total = 0;
        for (Map<String, Object> c : candidateList) {
            total += ((AtomicInteger) c.get("voteCount")).get();
        }
        return total;


    }
}