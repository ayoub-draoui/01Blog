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
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
    System.out.println("ana hnaaa");
       User user = userRepository.findByUsername(username)
       .orElseThrow(()-> new UsernameNotFoundException("username not found :" + username));
       return new CustomUserDetails(user);
    }
    
}
