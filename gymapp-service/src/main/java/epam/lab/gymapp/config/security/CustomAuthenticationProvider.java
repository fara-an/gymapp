package epam.lab.gymapp.config.security;

import epam.lab.gymapp.service.implementation.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final LoginAttemptService loginAttemptService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;



    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String name = authentication.getName();

        if (loginAttemptService.isBlocked(name)) {
            throw  new LockedException("User temporarily blocked due to many failed attempts. Try again later");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(name);
        String rawPassword  = authentication.getCredentials().toString();

        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())){
            loginAttemptService.loginFailed(name);
            throw  new BadCredentialsException("Invalid credentials");
        }

        loginAttemptService.loginSucceeded(name);
       return new UsernamePasswordAuthenticationToken(userDetails,rawPassword, userDetails.getAuthorities());

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
