package uz.pdp.bot.botModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Data
public class BotUser {
    private final String phoneNumber;
    private final String fullName;
    private final Long chatId;
    private final Long userId;
    private final UUID userBaseId;
    private boolean isActive = true;

    @JsonCreator
    public BotUser(
            @JsonProperty("phoneNumber") String phoneNumber,
            @JsonProperty("fullName") String fullName,
            @JsonProperty("chatId") Long chatId,
            @JsonProperty("userId") Long userId,
            @JsonProperty("userBaseId") UUID userBaseId
    ) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.chatId = chatId;
        this.userId = userId;
        this.userBaseId = userBaseId;
    }
}
