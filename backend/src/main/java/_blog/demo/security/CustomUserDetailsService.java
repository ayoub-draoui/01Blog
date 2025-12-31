package _blog.demo.security;


import _blog.demo.model.User;
import _blog.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class CustomUserDetailsService implements UserDetailsService {
   @Autowired
    private UserRepository userRepository;
     @Override
   public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException{
       User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
       .orElseThrow(()-> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
       return new CustomUserDetails(user);
    }
    
}
