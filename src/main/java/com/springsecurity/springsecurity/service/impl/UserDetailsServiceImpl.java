package com.springsecurity.springsecurity.service.impl;

import com.springsecurity.springsecurity.dto.CreateUserRequestDto;
import com.springsecurity.springsecurity.entity.CustomUserDetails;
import com.springsecurity.springsecurity.entity.Role;
import com.springsecurity.springsecurity.entity.User;
import com.springsecurity.springsecurity.models.EmailDetails;
import com.springsecurity.springsecurity.repository.UserRepository;
import com.springsecurity.springsecurity.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Component
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserRoleRepository roleRepo;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FlywayService flywayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;

    @Value("${rabbitmq.binding.email.name}")
    private String emailRoutingKey;

    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        logger.debug("Entering in loadUserByUsername Method...");
        User user = userRepo.findByUsername(username);
        if (user == null) {
            logger.error("Username not found: {}", username);
            throw new UsernameNotFoundException("could not found user..!!");
        }
        logger.info("User Authenticated Successfully..!!!");
        return new CustomUserDetails(user);
    }

    public User saveUser(CreateUserRequestDto createUserRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepo.findByUsername(currentUsername);

        if (currentUser == null || currentUser.getRoles().stream().noneMatch(role -> role.getName().equals("ADMIN"))) {
            throw new AccessDeniedException("Only users with ADMIN role can add new users.");
        }

        User user = new User();
        user.setEmail(createUserRequestDto.getEmail());
        user.setUsername(createUserRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(createUserRequestDto.getPassword()));
        user.setSchemaName(createUserRequestDto.getSchemaName());
        user.setIsActive(false);
        user.setIsDeleted(false);

        Set<Role> roles = user.getRoles();

        if (createUserRequestDto.getRoleName() != null) {
            Role role = roleRepo.findByName(createUserRequestDto.getRoleName().toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Role not found."));
            roles.add(role);
        }

        user.setRoles(roles);

        User userToSend = userRepo.save(user);
        if (createUserRequestDto.getId() == null) {
            flywayService.createTenantDatabase(user.getSchemaName());
        }
        try {
            emailService.sendActivationEmail(EmailDetails.builder()
                    .messageBody("Registration Successful with mail id: " + createUserRequestDto.getEmail())
                    .recipient(createUserRequestDto.getEmail())
                    .subject("REGISTRATION SUCCESS")
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userToSend;
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public boolean activateUser(String email, String token, String expiresAt) {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            return false;
        }

        LocalDateTime expirationTime = LocalDateTime.parse(expiresAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if (expirationTime.isBefore(LocalDateTime.now())) {
            return false;
        }

        user.setIsActive(true);
        userRepo.save(user);
        return true;
    }
}