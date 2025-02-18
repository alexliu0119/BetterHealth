package com.health.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.health.config.UsdaApiConfig;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Slf4j
public class UsdaFoodService {
	@Value("${usda.api.key}")
    private String apiKey;
    
	@Value("${usda.api.base-url}")  
    private String baseUrl;
    private static final Logger logger = LoggerFactory.getLogger(UsdaFoodService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UsdaApiConfig usdaApiConfig; 

    private final String API_KEY = "W1jAy9pJXg3F7uRKuJcvSQ0g1BzfOhhrPJyhHDYd"; 
    private final String BASE_URL = "https://api.nal.usda.gov/fdc/v1";

 
    
 
      
        
    public List<Map<String, Object>> searchFood(String query) {
        String url = baseUrl + "/foods/search?api_key=" + apiKey + "&query=" + query;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        if (response.getBody() != null && response.getBody().containsKey("foods")) {
            return (List<Map<String, Object>>) response.getBody().get("foods");
        }
        return new ArrayList<>();
    }

        
    


    private Map<String, Object> extractNutrients(Map<String, Object> food) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", food.get("description")); 
        
        List<Map<String, Object>> nutrients = (List<Map<String, Object>>) food.get("foodNutrients");
        if (nutrients != null) {
            for (Map<String, Object> nutrient : nutrients) {
                String nutrientName = (String) nutrient.get("nutrientName");
                Double value = ((Number) nutrient.get("value")).doubleValue();

               
                switch (nutrientName) {
                    case "Energy":
                        result.put("calories", value);
                        break;
                    case "Protein":
                        result.put("protein", value);
                        break;
                    case "Total lipid (fat)":
                        result.put("fat", value);
                        break;
                    case "Carbohydrate, by difference":
                        result.put("carbs", value);
                        break;
                }
            }
        }

        result.put("servingSize", 100); 
        return result;
    }

   
    public Map<String, Object> transformFoodData(Map<String, Object> food) {
        Map<String, Object> transformed = new HashMap<>();
        transformed.put("name", food.get("description"));
        transformed.put("calories", getNutrientValue(food, "Energy"));
        transformed.put("protein", getNutrientValue(food, "Protein"));
        transformed.put("fat", getNutrientValue(food, "Total lipid (fat)"));
        transformed.put("carbs", getNutrientValue(food, "Carbohydrate, by difference"));
        transformed.put("servingSize", 100);
        return transformed;
    }

    
    private double getNutrientValue(Map<String, Object> food, String nutrientName) {
        try {
            List<Map<String, Object>> nutrients = (List<Map<String, Object>>) food.get("foodNutrients");
            if (nutrients == null) return 0.0;
            
            return nutrients.stream()
                .filter(n -> nutrientName.equals(n.get("nutrientName")))
                .findFirst()
                .map(n -> {
                    Object value = n.get("value");
                    if (value instanceof Number) {
                        return ((Number) value).doubleValue();
                    }
                    return 0.0;
                })
                .orElse(0.0);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
