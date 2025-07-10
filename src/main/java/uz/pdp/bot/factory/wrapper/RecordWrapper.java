package uz.pdp.bot.factory.wrapper;

import java.util.UUID;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class RecordWrapper {
    private UUID id;
    private String name;
    private String command;
}