package com.bitbites.bitbites2;

import com.bitbites.bitbites2.backend.RecipeDatabase;
import com.bitbites.bitbites2.backend.recipes.Recipe;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}
