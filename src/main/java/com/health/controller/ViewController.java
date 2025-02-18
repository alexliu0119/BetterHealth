package com.health.controller;

import com.health.model.User;
import com.health.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ViewController {

    @Autowired
    private UserService userService;  
    
    private static final Logger logger = LoggerFactory.getLogger(ViewController.class);  
   

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
   
    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            model.addAttribute("userId", user.getUserId());
            model.addAttribute("user", user);  
            return "profile"; 
        }
        return "redirect:/login";
    }

  
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            
           
            if (!isProfileComplete(user)) {
                return "redirect:/profile";
            }
            return "dashboard";
        }
        return "redirect:/login";
    }

  
    @GetMapping("/daily-record")
    public String showDailyRecord(Model model, HttpSession session, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            try {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                User user = userService.findByUsername(userDetails.getUsername());
                
                if (!isProfileComplete(user)) {
                    return "redirect:/profile";
                }
                
                model.addAttribute("user", user);
                session.setAttribute("userId", user.getUserId());
                return "daily-record";
            } catch (Exception e) {
                logger.error("訪問每日紀錄時發生錯誤", e);
                return "redirect:/login";
            }
        }
        return "redirect:/login";
    }


    @GetMapping("/activity-sleep")
    public String activitySleep(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            try {
                String username = auth.getName();
                User user = userService.findByUsername(username);
                
                if (!isProfileComplete(user)) {
                    return "redirect:/profile";
                }
                
                model.addAttribute("userId", user.getUserId());
                return "activity-sleep";
            } catch (Exception e) {
                logger.error("訪問運動睡眠頁面時發生錯誤", e);
                return "redirect:/login";
            }
        }
        return "redirect:/login";
    }

   
    @GetMapping("/health-score")
    public String healthScore(HttpSession session, Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            try {
                UserDetails userDetails = (UserDetails) auth.getPrincipal();
                User user = userService.findByUsername(userDetails.getUsername());
                
                if (!isProfileComplete(user)) {
                    return "redirect:/profile";
                }
                
                model.addAttribute("userId", user.getUserId());
                session.setAttribute("userId", user.getUserId());
                return "health-score";
            } catch (Exception e) {
                logger.error("訪問健康評分頁面時發生錯誤", e);
                return "redirect:/login";
            }
        }
        return "redirect:/login";
    }
    @GetMapping("/checkProfile")
    public String checkProfile(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            
            
            if (!isProfileComplete(user)) {
                return "redirect:/profile"; 
            }
            return "redirect:/dashboard";    
        }
        return "redirect:/login";
    }
    private boolean isProfileComplete(User user) {
        return user != null && 
               user.getHeight() != null && 
               user.getWeight() != null && 
               user.getAge() != null && 
               user.getGender() != null && 
               user.getActivityLevel() != null;
    }
}