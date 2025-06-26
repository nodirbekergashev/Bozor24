package uz.pdp.model;

import lombok.*;
import uz.pdp.baseAbs.BaseModel;

import javax.management.relation.Role;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseModel {

    private String fullName;
    private String userName;
    private String password;
    private Role role;

    @Override
    public UUID getId() {
        return super.getId();
    }

    @Override
    public boolean isActive() {
        return super.isActive();
    }

    @Override
    public Date getCreatedAt() {
        return super.getCreatedAt();
    }

    @Override
    public Date getUpdatedAt() {
        return super.getUpdatedAt();
    }

    @Override
    public void setActive(boolean isActive) {
        super.setActive(isActive);
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        super.setCreatedAt(createdAt);
    }

    @Override
    public void setUpdatedAt(Date updatedAt) {
        super.setUpdatedAt(updatedAt);
    }
}
