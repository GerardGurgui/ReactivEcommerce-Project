package Ecommerce.usermanagement.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    //COMPROBAR CONSTRASEÑA? INTRODUCIR 2 VECES?

    @Id
    @JsonIgnore
    private ObjectId id;

    @Field(name = "uuid")
    @Indexed(unique = true)
    private String uuid;

    @Field(name = "username")
    @NotBlank
    @Size(min = 3, max = 30)
    @Indexed(unique = true)
    private String username;

    @Field(name = "name")
    private String name;

    @Field(name = "lastname")
    private String lastname;

    @Field(name = "phone")
    @Indexed(unique = true)
    private String phone;

    @Field(name = "email")
    @NotBlank
    @Email
    @Indexed(unique = true)
    private String email;

    @JsonIgnore
    @Field(name = "password")
    private String password;

    //ACTUALIZAR DATOS CUANDO COMPELTE EL SISTEMA DE PAGOS
    @Field(name = "total_purchase")
    private Long totalPurchase;

    @Field(name = "total_spent")
    private int totalSpent;

    @Field(name = "register_date")
    private Instant registeredAt;

    @Field(name = "latest_access")
    private Instant latestAccess;

    @JsonIgnore
    @Field(name = "registration_ip")
    private String registrationIp;

    @Field(name = "roles")
    @JsonManagedReference // manejar serializacion de los usuarios
    private List<String> roles;

    //Properties from userDetails
    @JsonIgnore
    @Field(name = "is_account_non_expired")
    private boolean isAccountNonExpired;

    @JsonIgnore
    @Field(name = "is_account_non_locked")
    private boolean isAccountNonLocked;

    @JsonIgnore
    @Field(name = "is_enabled")
    private boolean isEnabled;

    public void addRoleUser() {

        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add("ROLE_USER");
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }



}
