package uz.pdp.model;

import lombok.*;
import uz.pdp.baseAbs.BaseModel;


import uz.pdp.enums.UserRole;
import java.util.Date;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User extends BaseModel {
    private String fullName;
    private String userName;
    private String password;
    private UserRole role;
}
