package uz.pdp.wrapperLists;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.pdp.model.User;

import java.util.List;

@JacksonXmlRootElement(localName = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserListWrapper {
    @JacksonXmlProperty(localName = "user")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<User> categoryList;
}
