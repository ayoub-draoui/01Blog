package _blog.demo.security;
import _blog.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
  private User  user;
  //  public CustomUserDetails(User user){
  //   this.user = user;
  //  }
   public Long getId() {
        return user.getId();
    }

   @Override
   public Collection< ? extends GrantedAuthority> getAuthorities(){
        System.out.println("am failing here1");
    return List.of(new SimpleGrantedAuthority(user.getRole().name()));

   } 
   @Override
   public String getPassword(){
    return user.getPassword();
   }
   @Override
   public String getUsername(){
    return user.getUsername();
   }
   @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
