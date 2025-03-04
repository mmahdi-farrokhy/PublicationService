package mmf.publication.app.controller;

import mmf.publication.app.dto.AppUserDTO;
import mmf.publication.app.service.AppUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AppUserService appUserService;

    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AppUserDTO userDTO) {
        appUserService.registerUser(userDTO);
        return ResponseEntity.ok("User registered successfully!");
    }
}
