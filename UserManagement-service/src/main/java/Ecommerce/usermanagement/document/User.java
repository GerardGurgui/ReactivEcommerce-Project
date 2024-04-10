package Ecommerce.usermanagement.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private ObjectId id;

    @Field(name = "uuid")
    private String uuid;

    @Field(name = "username")
    @Indexed(unique = true)
    @NotNull
    private String username;

    @NotNull
    @Field(name = "firstname")
    private String firstname;

    @NotNull
    @Field(name = "lastname")
    private String lastname;

    @Field(name = "phone")
    @Indexed(unique = true)
    private String phone;

    @NotNull
    @Field(name = "email")
    @Indexed(unique = true)
    private String email;

    @JsonIgnore
    @NotNull
    @Field(name = "password")
    @Indexed(unique = true)
    private String password;

    @Field(name = "total_purchase")
    private Long totalPurchase;

    @Field(name = "total_spent")
    private int totalSpent;

    @Field(name = "is_active")
    private boolean isActive;

    @Field(name = "active_cart")
    private boolean activeCart;

    @Field(name = "login_date")
    private LocalDate loginDate;

    @Field(name = "latest_access")
    private String latestAccess;

    @Field(name = "roles")
    @JsonManagedReference // manejar serializacion de los usuarios
    private Set<Roles> roles;

    //falta relaciones, carritos etc
    //posible ampliacion, ultima conexion etc

    //operaciones y funciones, añadir producto al carro, eliminar producto del carro, comprar, etc
    //de dinero??

    public void addRoleUser() {

        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(Roles.USER);
    }

    public boolean isAdmin() {

        return roles.stream()
                .anyMatch(role -> role.equals(Roles.ADMIN));

    }
}
