package uz.pdp.bot.botModel;

import lombok.*;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class BotUser {
    private final Long chatId;
    private final Long userId;
    private final UUID userBaseId;
    private boolean isActive = true;
}
