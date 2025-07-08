package uz.pdp.bot;

import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.bot.factory.inline.CategoryInlineKeyboardMarkup;
import uz.pdp.bot.service.CartServiceBot;
import uz.pdp.bot.service.CategoryServiceBot;
import uz.pdp.bot.service.ProductServiceBot;
import uz.pdp.bot.service.UserServiceBot;
import uz.pdp.bot.util.PhotoUtil;
import uz.pdp.enums.PhotosEnum;
import uz.pdp.enums.UserRole;
import uz.pdp.itemClasses.CartItem;
import uz.pdp.model.Cart;
import uz.pdp.model.Category;
import uz.pdp.model.Product;

import java.util.*;

import static uz.pdp.enums.CallBackQueryStarting.*;
import static uz.pdp.model.Category.ROOT_CATEGORY_ID;

public class MainClassBot extends TelegramLongPollingBot {
    private static final String BOT_USERNAME = "bazar_24_bot";
    private static final String BOT_TOKEN = "7916545438:AAFvuOgnucYZdfFIrILAmygu7-DxL6vFujo";
    static CategoryServiceBot categoryServiceBot = new CategoryServiceBot();
    static ProductServiceBot productServiceBot = new ProductServiceBot();
    static UserServiceBot userServiceBot = new UserServiceBot();
    static CartServiceBot cartServiceBot = new CartServiceBot();
    private final Map<Long, Map<UUID, Integer>> userProductQuantities = new HashMap<>();
    private static final String BACK_COMMAND = "🔙 Back";

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onRegister() {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Start the bot"));
        commands.add(new BotCommand("/about", "About this bot"));

        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to set commands", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            switch (text) {
                case "/start" -> {
                    sendMenuButtons(chatId);

                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>();
                    List<InlineKeyboardButton> inlineRow = new ArrayList<>();
                    InlineKeyboardButton btn = new InlineKeyboardButton();
                    btn.setText("CUSTOMER");
                    btn.setCallbackData(USER_ROLE.getValue() + "CUSTOMER");
                    inlineRow.add(btn);
                    btn = new InlineKeyboardButton();
                    btn.setText("SELLER");
                    btn.setCallbackData(USER_ROLE.getValue() + "SELLER");
                    inlineRow.add(btn);
                    inlineRows.add(inlineRow);
                    markup.setKeyboard(inlineRows);


                    sendMessage.setReplyMarkup(markup);
                    sendMessage.setText("Who You Are?");
                    sendMessage.setChatId(chatId);

                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "Help" -> sendMessage(chatId, "salom");

                case "🗂 Categories" -> browseCategories(chatId, ROOT_CATEGORY_ID);

                case "🛒 My Cart" -> cartMenage(update);

                case "📦 My Orders" -> sendMessage(chatId, "You have not orders");
            }
        } else if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();
            User user = callbackQuery.getFrom();
            Long id = user.getId();
            String fullName = user.getFirstName() + " " + user.getLastName();
            Long chatId = callbackQuery.getMessage().getChatId();
            String data = callbackQuery.getData();
            Integer messageId = callbackQuery.getMessage().getMessageId();

            if (data.startsWith("USER_ROLE")) {
                String role = data.substring(USER_ROLE.getLength());
                userServiceBot.add(fullName, id, UserRole.valueOf(role), chatId);
                DeleteMessage deleteMessage = new DeleteMessage(chatId.toString(), messageId);
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (data.startsWith(CATEGORY.getValue() + PRODUCT.getValue())) {//select show products
                String categoryIdString = data.substring(CATEGORY.getLength() + PRODUCT.getLength());
                UUID categoryId = UUID.fromString(categoryIdString);
                editProducts(chatId, messageId, categoryId, 0);

            }
            else if (data.startsWith(CATEGORY.getValue())) {//select category
                String categoryIdString = data.substring(CATEGORY.getLength());
                UUID categoryId = UUID.fromString(categoryIdString);
                editCategories(chatId, categoryId);
            }
            else if (data.startsWith(PRODUCT.getValue() + ":")) {//increment or decrement
                String[] parts = data.split(":");
                if (parts.length == 3) {
                    String action = parts[1]; // INC yoki DEC
                    UUID productId = UUID.fromString(parts[2]);
                    Product product = productServiceBot.getProductById(productId);

                    userProductQuantities.putIfAbsent(chatId, new HashMap<>());
                    Map<UUID, Integer> productMap = userProductQuantities.get(chatId);
                    productMap.putIfAbsent(productId, 1);
                    int chosenQuantity = productMap.get(productId);

                    if (action.equals("INC") && chosenQuantity < product.getQuantity()) {
                        chosenQuantity++;
                    } else if (action.equals("DEC") && chosenQuantity > 1) {
                        chosenQuantity--;
                    } else {
                        return;
                    }

                    productMap.put(productId, chosenQuantity);

                    StringBuilder productPrint = new StringBuilder();
                    productPrint.append("Name:").append(product.getName()).append("\n");
                    productPrint.append(" Total price:").append(product.getPrice() * chosenQuantity).append("\n");
                    productPrint.append("Count:").append(product.getQuantity()).append("\n");

                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                    rows.add(getIncOrDecInlineKeyboardButtons(chosenQuantity, productId));

                    List<InlineKeyboardButton> inlineRow = new ArrayList<>();

                    InlineKeyboardButton backBtn = new InlineKeyboardButton();
                    backBtn.setText(BACK_COMMAND);
                    backBtn.setCallbackData(CATEGORY.getValue() + product.getCategoryId());
                    inlineRow.add(backBtn);


                    InlineKeyboardButton addToCartBtn = new InlineKeyboardButton();
                    addToCartBtn.setText("Add to Cart");
                    addToCartBtn.setCallbackData(CART.getValue() + product.getId());
                    inlineRow.add(addToCartBtn);

                    rows.add(inlineRow);
                    markup.setKeyboard(rows);

                    EditMessageText message = new EditMessageText();
                    message.setChatId(chatId.toString());
                    message.setMessageId(messageId);
                    message.setText(productPrint.toString());
                    message.setParseMode("HTML");
                    message.setReplyMarkup(markup);

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        if (!e.getMessage().contains("message is not modified")) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            else if (data.startsWith(PRODUCT.getValue())) {
                String productIdString = data.substring(PRODUCT.getLength());
                UUID productId = UUID.fromString(productIdString);
                Product product = productServiceBot.getProductById(productId);

                userProductQuantities.putIfAbsent(chatId, new HashMap<>());
                Map<UUID, Integer> productMap = userProductQuantities.get(chatId);
                productMap.putIfAbsent(productId, 1);
                int chosenQuantity = productMap.get(productId);
                StringBuilder productPrint = new StringBuilder();
                productPrint.append("Name:").append(product.getName()).append("\n");
                productPrint.append("Price:").append(product.getPrice()).append("\n");
                productPrint.append("Count:").append(product.getQuantity()).append("\n");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                rows.add(getIncOrDecInlineKeyboardButtons(chosenQuantity, productId));

                List<InlineKeyboardButton> inlineRow = new ArrayList<>();


                InlineKeyboardButton backBtn = new InlineKeyboardButton();
                backBtn.setText(BACK_COMMAND); // masalan, "🔙 Back"
                backBtn.setCallbackData(CATEGORY.getValue() + product.getCategoryId());
                inlineRow.add(backBtn);


                InlineKeyboardButton addToCartBtn = new InlineKeyboardButton();
                addToCartBtn.setText("Add to Cart");
                addToCartBtn.setCallbackData(CART.getValue() + product.getId());
                inlineRow.add(addToCartBtn);

                rows.add(inlineRow);
                markup.setKeyboard(rows);

                EditMessageText message = new EditMessageText();
                message.setChatId(chatId.toString());
                message.setMessageId(messageId);
                message.setText(productPrint.toString());
                message.setReplyMarkup(markup);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    if (!e.getMessage().contains("message is not modified")) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else if (data.startsWith(CART.getValue())) {
                addToCart(callbackQuery);
            }
        }
    }

    private void addToCart(CallbackQuery query) {
        User from = query.getFrom();
        Long chatId = query.getMessage().getChatId();
        Long telegramUserId =  from.getId();
        UUID userId = userServiceBot.getUserIdByTgUser(telegramUserId);

        String data = query.getData();
        String productIdStr = data.substring(CART.getLength());
        UUID productId = UUID.fromString(productIdStr);

        Product product = productServiceBot.getProductById(productId);
        if (product == null) {
            sendMessage(chatId, "❌ Product not found.");
            return;
        }

        Cart cart = cartServiceBot.getCartByUserId(userId);
        if (cart == null) {
            cart = new Cart(userId, new ArrayList<>()); // yangi cart
            cartServiceBot.saveCart(cart);
        }

        List<CartItem> cartItems = cart.getProducts();
        boolean found = false;

        for (CartItem item : cartItems) {
            if (productServiceBot.getProductById(item.getProductId()).getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            CartItem newItem = new CartItem(product.getId(), 1);
            cartItems.add(newItem);
        }

        cartServiceBot.saveCart(cart);

        sendMessage(chatId, "✅ <b>" + product.getName() + "</b> added to your cart.");
    }


    private void cartMenage(Update update) {
        User user = update.getMessage().getFrom();
        Long chatId = update.getMessage().getChatId();

        UUID userId = userServiceBot.getUserIdByTgUser(user.getId());
        Cart cart = cartServiceBot.getCartByUserId(userId);

        if (cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()) {
            sendMessage(chatId, "🛒 Your cart is empty.");
            return;
        }

        StringBuilder printCart = new StringBuilder();
        double totalCartPrice = 0;

        printCart.append("<b>🛒 Your Cart:</b>\n\n");

        int index = 1;
        for (CartItem item : cart.getProducts()) {
            Product product = productServiceBot.getProductById(item.getProductId()); // CartItemda mahsulot bo‘lishi kerak
            int count = item.getQuantity();
            double totalPrice = product.getPrice() * count;
            totalCartPrice += totalPrice;

            printCart.append("🔹 <b>Product #").append(index++).append("</b>\n");
            printCart.append("Name:     ").append(product.getName()).append("\n");
            printCart.append("Price:    ").append(product.getPrice()).append("\n");
            printCart.append("Count:    ").append(count).append("\n");
            printCart.append("Total:    ").append(totalPrice).append("\n\n");
        }

        printCart.append("<b>🧾 Total Cart Price: ").append(totalCartPrice).append("</b>");

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(printCart.toString());
        message.setParseMode("HTML");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendMenuButtons(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        ReplyKeyboardMarkup r = new ReplyKeyboardMarkup();
        r.setResizeKeyboard(true);
        r.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        r.setKeyboard(rows);
        KeyboardRow row = new KeyboardRow();
        row.add("🗂 Categories");
        rows.add(row);
        row = new KeyboardRow();
        row.add("🛒 My Cart");
        row.add("📦 My Orders");
        rows.add(row);
        row = new KeyboardRow();
        row.add("Help");
        rows.add(row);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(r);
        sendMessage.setText("Please select a button");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private List<InlineKeyboardButton> getIncOrDecInlineKeyboardButtons(int chosenQuantity, UUID productId) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton minus = new InlineKeyboardButton("-");
        minus.setCallbackData(PRODUCT.getValue() + ":DEC:" + productId);
        row.add(minus);

        InlineKeyboardButton count = new InlineKeyboardButton(String.valueOf(chosenQuantity));
        count.setCallbackData("IGNORE");
        row.add(count);

        InlineKeyboardButton plus = new InlineKeyboardButton("+");
        plus.setCallbackData(PRODUCT.getValue() + ":INC:" + productId);
        row.add(plus);
        InlineKeyboardButton cart = new InlineKeyboardButton("Add to cart");

        return row;
    }

    public void editProducts(Long chatId, Integer messageId, UUID categoryId, int page) {
        List<Product> allProducts = productServiceBot.getProductsByCategoryId(categoryId);

        int pageSize = 7;
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allProducts.size());

        if (allProducts.isEmpty()) {
            sendMessage(chatId, "Sorry. There are no products yet!");
            return;
        }

        if (fromIndex >= allProducts.size()) {
            sendMessage(chatId, "No products on this page.");
            return;
        }

        List<Product> pageProducts = allProducts.subList(fromIndex, toIndex);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < pageProducts.size(); i += 2) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            Product p1 = pageProducts.get(i);
            InlineKeyboardButton b1 = new InlineKeyboardButton(p1.getName());
            b1.setCallbackData(PRODUCT.getValue() + p1.getId());
            row.add(b1);

            if (i + 1 < pageProducts.size()) {
                Product p2 = pageProducts.get(i + 1);
                InlineKeyboardButton b2 = new InlineKeyboardButton(p2.getName());
                b2.setCallbackData(PRODUCT.getValue() + p2.getId());
                row.add(b2);
            }
            keyboard.add(row);
        }


        List<InlineKeyboardButton> navRow = new ArrayList<>();

        if (page >= 0) {
            InlineKeyboardButton prev = new InlineKeyboardButton("⬅️");
            if (page == 0) {
                prev.setCallbackData(CATEGORY.getValue() + categoryId);
                navRow.add(prev);
            } else {
                prev.setCallbackData("PRODUCT_PAGE:" + categoryId + ":" + (page - 1));
                navRow.add(prev);
            }
        }
        if (toIndex < allProducts.size()) {
            InlineKeyboardButton next = new InlineKeyboardButton("➡️");
            next.setCallbackData("PRODUCT_PAGE:" + categoryId + ":" + (page + 1));
            navRow.add(next);
        }
        if (!navRow.isEmpty()) {
            keyboard.add(navRow);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText("🛍️ Select a product:");
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to edit message", e);
        }
    }

    public void editCategories(Long chatId, UUID id) {
        Category categoryById = null;
        if (!id.equals(ROOT_CATEGORY_ID)) {
            categoryById = categoryServiceBot.getCategoryById(id);
        }

        List<Category> nextCategories = categoryServiceBot.getCategoriesByLevel(id);


        if (categoryById != null) {
            sendCategoryPhoto(chatId, "🖼 " + categoryById.getName(), PhotosEnum.valueOf(categoryById.getName().toUpperCase()));
        }


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        if (nextCategories.isEmpty()) {
            InlineKeyboardButton backBtn = new InlineKeyboardButton(BACK_COMMAND);
            backBtn.setCallbackData(CATEGORY.getValue() + (categoryById != null ? categoryById.getParentId() : ROOT_CATEGORY_ID));

            InlineKeyboardButton showProductsBtn = new InlineKeyboardButton("🛍 Products");
            assert categoryById != null;
            showProductsBtn.setCallbackData(CATEGORY.getValue() + PRODUCT.getValue() + categoryById.getId());

            keyboard.add(List.of(showProductsBtn));
            keyboard.add(List.of(backBtn));
        } else {
            // 👇 Child kategoriyalar mavjud
            CategoryInlineKeyboardMarkup customMarkup = new CategoryInlineKeyboardMarkup(nextCategories, 3);
            List<List<InlineKeyboardButton>> categoryButtons = customMarkup.createInlineKeyboardMarkup().getKeyboard();

            InlineKeyboardButton backBtn = new InlineKeyboardButton(BACK_COMMAND);
            backBtn.setCallbackData(CATEGORY.getValue() + (categoryById != null ? categoryById.getParentId() : ROOT_CATEGORY_ID));

            categoryButtons.add(List.of(backBtn));
            keyboard = categoryButtons;
        }

        markup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("📂 Please select a category:");
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("HTML");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCategoryPhoto(Long chatId, String text, PhotosEnum photo) {
        if (photo != null) {
            SendPhoto message = new SendPhoto();
            message.setChatId(chatId);
            message.setCaption(photo.getName());
            message.setPhoto(new InputFile(PhotoUtil.getPhoto(photo.getUrl())));
            message.setParseMode("HTML");
            try {
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else {
            sendMessage(chatId, text);
        }
    }

    public void browseCategories(Long chatId, UUID id) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        Category categoryById = null;
        if (!id.equals(ROOT_CATEGORY_ID)) {
            categoryById = categoryServiceBot.getCategoryById(id);
            String categoryName = categoryById.getName().toUpperCase();
            sendCategoryPhoto(chatId, categoryName, PhotosEnum.valueOf(categoryName));
        }
        List<Category> nextCategories = categoryServiceBot.getCategoriesByLevel(id);

        CategoryInlineKeyboardMarkup categoryInlineKeyboardMarkup = new
                CategoryInlineKeyboardMarkup(nextCategories, 3);

        InlineKeyboardMarkup markup = categoryInlineKeyboardMarkup.createInlineKeyboardMarkup();

        message.setChatId(chatId);
        message.setText("Please select a category");
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}