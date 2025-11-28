package Ecommerce.usermanagement.document;

import Ecommerce.usermanagement.dto.cart.CartLinkUserDto;
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


import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDate;
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
    private ObjectId id;

    @Field(name = "uuid")
    @Indexed(unique = true)
    private String uuid;

    @Field(name = "username")
    @Indexed(unique = true)
    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String lastname;

    @Field(name = "phone")
    @Indexed(unique = true)
    private String phone;

    @NotBlank
    @Field(name = "email")
    @Indexed(unique = true)
    private String email;

    @JsonIgnore
    @NotBlank
    @Field(name = "password")
    @Indexed(unique = true)
    private String password;

    @Field(name = "total_purchase")
    private Long totalPurchase;

    @Field(name = "total_spent")
    private int totalSpent;

    @Field(name = "login_date")
    private LocalDate loginDate;

    @Field(name = "latest_access")
    private Instant latestAccess;

    @Field(name = "roles")
    @JsonManagedReference // manejar serializacion de los usuarios
    private Set<Roles> roles;

    @Field(name = "carts")
    private List<Long> cartIds;

    //Properties from userDetails
    @Field(name = "is_account_non_expired")
    private boolean isAccountNonExpired;

    @Field(name = "is_account_non_locked")
    private boolean isAccountNonLocked;

    @Field(name = "is_enabled")
    private boolean isEnabled;

    //AÑADIR RELACION CON PEDIDOS (Order) FALTA CREAR MICROSERVICIO DE PEDIDOS

    //posible ampliacion, ultima conexion etc

    //operaciones y funciones, añadir producto al carro, eliminar producto del carro, comprar, etc
    //de dinero??

    public void addCartId(Long cartId) {

        if (cartIds == null) {
            cartIds = new ArrayList<>();
        }
        cartIds.add(cartId);
    }

    public void addRoleUser() {

        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(Roles.ROLE_USER);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }



}
