package com.health.controller;

import com.health.dto.UserProfileDTO;
import com.health.model.User;
import com.health.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @PostMapping("users/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");
            
            User user = userService.authenticateUser(username, password);
            
        
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(username, password);
            
       
            Authentication authentication = 
                authenticationManager.authenticate(authToken);
            
       
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
         
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
                SecurityContextHolder.getContext());
            
            return ResponseEntity.ok().body(Map.of(
                "message", "登入成功",
                "userId", user.getUserId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            if(user == null) {
                return ResponseEntity.notFound().build();
            }
            UserProfileDTO profileDTO = new UserProfileDTO(user);
            return ResponseEntity.ok(profileDTO);
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            if(user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch(Exception e) {
            logger.error("獲取用戶資料失敗", e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "獲取用戶資料失敗",
                "message", e.getMessage()
            ));
        }
    }
    @PostMapping("/users/update-all-tdee")
    public ResponseEntity<?> updateUserTDEE() {
        try {
            userService.updateAllUsersTDEE();
            return ResponseEntity.ok().body(Map.of("message", "所有用戶的 TDEE 已更新"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



    @PostMapping("/profile/dto")
    public ResponseEntity<User> updateProfileWithDTO(@RequestBody UserProfileDTO profileDTO) {
        try {
            User user = userService.updateProfile(profileDTO);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("更新用戶資料失敗", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
   
    @PutMapping("/profile/{userId}")
    public ResponseEntity<User> updateProfile(
            @PathVariable Long userId,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Double height,
            @RequestParam(required = false) Double weight,
            @RequestParam(required = false) String activityLevel) {
        try {
            User user = userService.updateProfile(userId, gender, age, height, weight, activityLevel);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("更新用戶資料失敗", e);
            return ResponseEntity.badRequest().build();
        }
    }

  
    @PostMapping("user/profile")  
    public ResponseEntity<?> updateProfile(@RequestBody UserProfileDTO profileDTO) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User updatedUser = userService.updateProfile(username, profileDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "更新失敗",
                "message", e.getMessage()
            ));
        }
    }

   
    @PostMapping("users/register") 
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registerData) {
        try {
            String username = registerData.get("username");
            String password = registerData.get("password");
            
            User user = userService.registerUser(username, password, password);
            return ResponseEntity.ok().body(Map.of("message", "註冊成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


    
    @PostMapping("/profile/metrics")
    public ResponseEntity<?> updateMetrics(@RequestBody Map<String, Object> metrics) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.findByUsername(username);

            if (metrics.containsKey("gender")) user.setGender((String) metrics.get("gender"));
            if (metrics.containsKey("age")) user.setAge((Integer) metrics.get("age"));
            if (metrics.containsKey("height")) user.setHeight((Double) metrics.get("height"));
            if (metrics.containsKey("weight")) user.setWeight((Double) metrics.get("weight"));
            if (metrics.containsKey("activityLevel")) user.setActivityLevel((String) metrics.get("activityLevel"));

            User updatedUser = userService.updateProfile(
                    user.getUserId(),
                    user.getGender(),
                    user.getAge(),
                    user.getHeight(),
                    user.getWeight(),
                    user.getActivityLevel()
            );

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("更新健康指標失敗", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "更新健康指標失敗",
                    "message", e.getMessage()
            ));
        }
    }

  
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<?> getUserDashboard(@PathVariable Long userId) {
        try {
            Map<String, Object> dashboard = userService.getUserDashboard(userId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("獲取儀表板失敗", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "獲取儀表板失敗",
                    "message", e.getMessage()
            ));
        }
    }
}
