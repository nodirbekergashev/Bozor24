package uz.pdp.wrapperLists;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;
import uz.pdp.bot.botModel.BotUser;
import uz.pdp.model.User;

import java.util.List;

@JacksonXmlRootElement(localName = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BotUsersListWrapper {
    @JacksonXmlProperty(localName = "user")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<BotUser> categoryList;
}
