package uz.pdp.bot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.bot.botModel.BotUser;
import uz.pdp.bot.factory.inline.CategoryInlineKeyboardMarkup;
import uz.pdp.bot.service.*;
import uz.pdp.bot.util.PhotoUtil;
import uz.pdp.enums.PhotosEnum;
import uz.pdp.enums.UserRole;
import uz.pdp.itemClasses.CartItem;
import uz.pdp.itemClasses.OrderItem;
import uz.pdp.model.Cart;
import uz.pdp.model.Category;
import uz.pdp.model.Order;
import uz.pdp.model.Product;
import uz.pdp.service.UserService;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static uz.pdp.bot.factory.ReplyKeyboardFactory.createReplyKeyboard;
import static uz.pdp.db.Lists.BOT_USERS;
import static uz.pdp.enums.CallBackQueryStarting.*;
import static uz.pdp.model.Category.ROOT_CATEGORY_ID;
import static uz.pdp.utils.FileUtil.writeToJson;

public class MainClassBot extends TelegramLongPollingBot {
    private static final String BOT_USERNAME = "onlinemarket24_7bot";
    private static final String BOT_TOKEN = "***REMOVED***";
    static CategoryServiceBot categoryServiceBot = new CategoryServiceBot();
    static ProductServiceBot productServiceBot = new ProductServiceBot();
    static UserServiceBot userServiceBot = new UserServiceBot();
    static CartServiceBot cartServiceBot = new CartServiceBot();
    static OrderServiceBot orderServiceBot = new OrderServiceBot();
    static UserService userService = new UserService();

    private static final Map<Long, Map<UUID, Integer>> USER_PRODUCT_QUANTITIES;
    private static final Long ADMIN_CHAT_ID = null;
    private static final Map<Long, Contact> BOT_USERS_MAP;
    private static final String BACK_COMMAND = "🔙 Back";
    private static final String CUSTOMER_ROLE = "CUSTOMER";
    private static final String SELLER_ROLE = "SELLER";

    private enum UserState {
        START, AWAITING_PHONE, AWAITING_ROLE, READY
    }

    private final Map<Long, UserState> userStates = new HashMap<>();

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
        commands.add(new BotCommand("/help", "Get help"));
        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to set commands", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handleCallbackQuery(update.getCallbackQuery());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(Message message) throws TelegramApiException {
        Long chatId = message.getChatId();

        if (message.hasContact() && !userServiceBot.isUseBefore(chatId)) {
            handleContactMessage(message, chatId);
            return;
        }


        if (message.hasText()) {
            String text = message.getText();
            switch (text) {
                case "/start" -> handleStartCommand(chatId);
                case "/help" -> sendHelpMessage(chatId);
                case "Products" -> giveRootCategories(chatId, ROOT_CATEGORY_ID);
                case "Cart" -> cartMenage(message);
                case "Orders" -> manageOrders(chatId, message.getMessageId());
                case "Send a request to the admin" ->
                        sendRequestToAdminByUser(message.getFrom().getId(), message.getText());
                case "Send a request to  admin" ->
                        sendRequestToAdminBySeller(message.getFrom().getId(), message.getText());
                case "Back" -> sendMenuButtons(chatId);
                case "Confirm" -> createNewOrder(chatId);
                case "Clear cart" -> clearCart(chatId);
                case "Edit" -> editCart(message);
                case CUSTOMER_ROLE, SELLER_ROLE -> handleRoleSelection(chatId, text, message);
                default -> sendMessage(chatId, "Sorry, unknown command click /start");
            }
        }
    }

    private void editCart(Message message) {
        Long id = message.getFrom().getId();
        UUID userId = userServiceBot.getUserIdByTgUser(id);
        Cart editedCart = cartServiceBot.getCartByUserId(userId);
        List<CartItem> products = editedCart.getProducts();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        markup.setKeyboard(rows);
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton btn = new InlineKeyboardButton();
        Product product;
        int colCount = 2;
        int index = 0;
        for (CartItem p : products) {
            index++;
            product = productServiceBot.getProductById(p.getProductId());
            btn.setText(product.getName());
            btn.setCallbackData(PRODUCT.getValue() + product.getId());
            row.add(btn);
            if (index % colCount == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Select product");
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyMarkup(markup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void editChosenProduct(CallbackQuery query) throws TelegramApiException {
        String data = query.getData();
        User user = query.getFrom();
        UUID id = userServiceBot.getUserIdByTgUser(user.getId());
        Cart cartByUserId = cartServiceBot.getCartByUserId(id);
        UUID editedProductId = UUID.fromString(data.substring(PRODUCT.getLength()));
        Product product = productServiceBot.getProductById(editedProductId); // CartItemda mahsulot bo‘lishi kerak
        CartItem cartItem = cartByUserId.getProducts().stream().filter(item -> item.getProductId().equals(editedProductId))
                .findFirst()
                .orElse(null);

        StringBuilder printCart = new StringBuilder();
        if (cartItem != null) {
            int quantity = cartItem.getQuantity();
            printCart.append("\t\t\t\t\tChoose product").append("\n");
            printCart.append("Name:     ").append(product.getName()).append("\n");
            printCart.append("Price:    ").append(product.getPrice()).append("\n");
            printCart.append("Count:    ").append(quantity).append("\n");
            printCart.append("Total:    $").append(product.getPrice() * quantity).append("\n\n");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> inlineRow = getEditedProductInlineKeyboardButtons(product);
            markup.setKeyboard(List.of(
                    getIncOrDecInlineKeyboardButtons(quantity, product.getId()),
                    inlineRow
            ));
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Select product");
            sendMessage.setChatId(query.getMessage().getChatId());
            sendMessage.setReplyMarkup(markup);
            execute(sendMessage);
        }
    }

    private List<InlineKeyboardButton> getEditedProductInlineKeyboardButtons(Product product) {
        List<InlineKeyboardButton> inlineRow = new ArrayList<>();

        InlineKeyboardButton backBtn = new InlineKeyboardButton();
        backBtn.setText(BACK_COMMAND);
        backBtn.setCallbackData(CATEGORY.getValue() + product.getCategoryId());
        inlineRow.add(backBtn);


        InlineKeyboardButton addToCartBtn = new InlineKeyboardButton();
        addToCartBtn.setText("Save Changes");
        addToCartBtn.setCallbackData(CART.getValue() + product.getId());
        inlineRow.add(addToCartBtn);
        return inlineRow;
    }

    private void sendRequestToAdminBySeller(Long userId, String text) {
        UUID userId1 = userServiceBot.getUserIdByTgUser(userId);
        uz.pdp.model.User user;
        if (userId1 == null) {
            return;
        }
        user = userService.getById(userId1);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ADMIN_CHAT_ID);
        sendMessage.setText(text + "\n Request from seller  " + user.getPhoneNumber());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRequestToAdminByUser(Long userId, String text) {
        UUID userId1 = userServiceBot.getUserIdByTgUser(userId);
        uz.pdp.model.User user;
        if (userId1 == null) {
            return;
        }
        user = userService.getById(userId1);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ADMIN_CHAT_ID);
        sendMessage.setText(text + "\n Request from customer  " + user.getPhoneNumber());
    }

    private void clearCart(Long chatId) {
        USER_PRODUCT_QUANTITIES.remove(chatId);
        UUID userId = userServiceBot.getUserIdByTgUser(chatId);
        Cart cartByUserId = cartServiceBot.getCartByUserId(userId);
        if (cartByUserId != null) {
            cartServiceBot.clearCart(cartByUserId.getId());
        }
        saveToFile();
    }

    private void handleContactMessage(Message message, Long chatId) throws TelegramApiException {
        // Store contact and ask for role
        BOT_USERS_MAP.put(chatId, message.getContact());
        userStates.put(chatId, UserState.AWAITING_ROLE);
        askForUserRole(chatId);
    }

    private void handleStartCommand(Long chatId) throws TelegramApiException {
        if (BOT_USERS_MAP.get(chatId) == null) {
            userStates.put(chatId, UserState.AWAITING_PHONE);
            sendRequestPhoneNumber(chatId);
        } else if (!userServiceBot.isUseBefore(chatId)) {
            askForUserRole(chatId);
        } else {
            userStates.put(chatId, UserState.READY);
            sendMenuButtons(chatId);
        }
    }

    private void askForUserRole(Long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Select your role");
        message.setReplyMarkup(chooseUserRole());
        execute(message);
        userStates.put(chatId, UserState.AWAITING_ROLE);
    }

    private void handleRoleSelection(Long chatId, String role, Message message) {
        Contact contact = BOT_USERS_MAP.get(chatId);
        if (contact == null) {
            sendMessage(chatId, "Share your contact first");
            return;
        }

        String userName = message.getFrom().getUserName();
        if (userName == null) {
            userName = " ";
        }

        // Save user with selected role
        userServiceBot.add(contact,
                userName,
                UserRole.valueOf(role),
                chatId
        );

        // Clean up and show menu
        userStates.put(chatId, UserState.READY);
        sendMenuButtons(chatId);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        if (data.startsWith(CATEGORY.getValue() + PRODUCT.getValue())) {
            String categoryIdString = data.substring(CATEGORY.getLength() + PRODUCT.getLength());
            editProducts(chatId, messageId, UUID.fromString(categoryIdString), 0);
        } else if (data.startsWith(CATEGORY.getValue())) {
            String categoryIdString = data.substring(CATEGORY.getLength());
            editCategories(chatId, UUID.fromString(categoryIdString), messageId);
        } else if (data.startsWith(PRODUCT.getValue() + ":")) {
            handleProductQuantityChange(callbackQuery);
        } else if (data.startsWith("PRODUCT_PAGE:")) {
            handleProductPagination(callbackQuery);
        } else if (data.startsWith(PRODUCT.getValue())) {
            showProductDetails(callbackQuery);
        } else if (data.startsWith(CART.getValue())) {
            addToCart(callbackQuery);
        } else if (data.startsWith(ORDER.getValue() + "add")) {
            createNewOrder(chatId);
        } else if (data.startsWith(ORDER.getValue())) {
            manageOrders(chatId, messageId);
        }
    }

    private void handleProductQuantityChange(CallbackQuery callbackQuery) throws TelegramApiException {
        String[] parts = callbackQuery.getData().split(":");
        if (parts.length != 3) return;

        String action = parts[1];
        UUID productId = UUID.fromString(parts[2]);
        Long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        // Get or initialize product quantities
        USER_PRODUCT_QUANTITIES.putIfAbsent(chatId, new HashMap<>());
        Map<UUID, Integer> productMap = USER_PRODUCT_QUANTITIES.get(chatId);
        productMap.putIfAbsent(productId, 1);
        saveToFile();

        int chosenQuantity = productMap.get(productId);
        Product product = productServiceBot.getProductById(productId);

        // Update quantity based on action
        if ("INC".equals(action) && chosenQuantity < product.getQuantity()) {
            chosenQuantity++;
        } else if ("DEC".equals(action) && chosenQuantity > 1) {
            chosenQuantity--;
        } else {
            return;
        }

        productMap.put(productId, chosenQuantity);
        updateProductMessage(chatId, messageId, product, chosenQuantity);
    }

    private void updateProductMessage(Long chatId, int messageId, Product product, int quantity) throws TelegramApiException {
        String productText = String.format(
                "Name: %s\nTotal price: %s\nCount: %s\nDescription: %s",
                product.getName(),
                product.getPrice() * quantity,
                product.getQuantity(),
                product.getDescription()
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(
                getIncOrDecInlineKeyboardButtons(quantity, product.getId()),
                chooseCommandButtons(product)
        ));

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText(productText);
        editMessage.setReplyMarkup(markup);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            if (!e.getMessage().contains("message is not modified")) {
                throw e;
            }
        }
    }

    private void showProductDetails(CallbackQuery callbackQuery) throws TelegramApiException {
        String productIdString = callbackQuery.getData().substring(PRODUCT.getLength());
        UUID productId = UUID.fromString(productIdString);
        Product product = productServiceBot.getProductById(productId);
        Long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        USER_PRODUCT_QUANTITIES.putIfAbsent(chatId, new HashMap<>());
        Map<UUID, Integer> productMap = USER_PRODUCT_QUANTITIES.get(chatId);
        productMap.putIfAbsent(productId, 1);
        int chosenQuantity = productMap.get(productId);
        saveToFile();
        updateProductMessage(chatId, messageId, product, chosenQuantity);
    }

    private void handleProductPagination(CallbackQuery callbackQuery) {
        String[] parts = callbackQuery.getData().split(":");
        if (parts.length != 3) return;

        UUID categoryId = UUID.fromString(parts[1]);
        int page = Integer.parseInt(parts[2]);
        editProducts(
                callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId(),
                categoryId,
                page
        );
    }

    private void manageOrders(Long chatId, Integer messageId) throws TelegramApiException {
        UUID userId = userServiceBot.getUserIdByTgUser(chatId);
        List<Order> orders = orderServiceBot.getUserOrders(userId);

        if (orders.isEmpty()) {
            sendMessage(chatId, "You have no orders yet");
            return;
        }

        StringBuilder ordersText = new StringBuilder("📦 Your Orders:\n\n");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy  HH:mm");

        for (Order order : orders) {
            String formattedDate = dateFormat.format(order.getCreatedAt());

            ordersText.append(String.format(
                    "📅 Date: %s\n" +
                            "💰 Order number: %s\n" +
                            "💰 Total: %s\n" +
                            "🛒 Items: %d\n" +
                            "📦 Status: %s\n\n",

                    formattedDate,
                    order.getOrderNumber(),
                    order.getTotalPrice(),
                    order.getOrdersByUser().size(),
                    order.getStatus()
            ));
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(ordersText.toString());
        execute(message);
    }

    public void editOrders(Message message) {
        Long id = message.getFrom().getId();
        UUID userId = userServiceBot.getUserIdByTgUser(id);
        List<Order> orders = orderServiceBot.getOrdersByUserId(userId);
        for (Order order : orders) {

        }
    }

    private List<InlineKeyboardButton> getOrdersButton(Order order) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        List<OrderItem> ordersByUser = order.getOrdersByUser();
        Product productById;
        for (OrderItem orderItem : ordersByUser) {
            productById = productServiceBot.getProductById(orderItem.getProductId());
            int productCount = orderItem.getProductCount();
            
        }
        return buttons;
    }

    private void createNewOrder(Long chatId) {
        UUID userId = userServiceBot.getUserIdByTgUser(chatId);
        Cart cart = cartServiceBot.getCartByUserId(userId);


        if (cart == null || cart.getProducts().isEmpty()) {
            sendMessage(chatId, "Your cart is empty. Add products first.");
            return;
        }

        List<CartItem> products = cart.getProducts();
        for (CartItem item : products) {
            int quantity = item.getQuantity();
            productServiceBot.editProductCount(item.getProductId(), quantity);
        }

        Order newOrder = orderServiceBot.createOrderFromCart(userId, cart);
        String orderId = newOrder.getOrderNumber();
        sendMessage(chatId, String.format(
                "✅ Order #%s created successfully!\n" +
                        "Total: %s\n" +
                        "Thank you for your purchase!",
                orderId,
                newOrder.getTotalPrice()
        ));


        // Clear the cart after order creation
        clearCart(chatId);
        sendMenuButtons(chatId);
    }

    private void sendHelpMessage(Long chatId) throws TelegramApiException {
        String helpText = """
                🤖 <b>Bazar24 Bot Help</b> 🤖
                
                <b>Available Commands:</b>
                /start - Start the bot
                /help - Show this help message
                
                <b>Main Menu:</b>
                🛍️ <b>Products</b> - Browse available products
                🛒 <b>Cart</b> - View your shopping cart
                📦 <b>Orders</b> - View your order history
                
                For any issues, please contact support.
                """;

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(helpText);
        message.setParseMode("HTML");
        execute(message);
    }

    private static ReplyKeyboardMarkup chooseUserRole() {
        ReplyKeyboardMarkup markup = createReplyKeyboard(List.of(CUSTOMER_ROLE, SELLER_ROLE), 2);
        markup.setOneTimeKeyboard(true);
        return markup;
    }

    private void sendRequestPhoneNumber(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Send your phone number:");


        KeyboardButton phoneButton = new KeyboardButton("📱 Share Phone Number");
        phoneButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(phoneButton);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(List.of(row));
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);

        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<InlineKeyboardButton> chooseCommandButtons(Product product) {
        List<InlineKeyboardButton> inlineRow = new ArrayList<>();

        InlineKeyboardButton backBtn = new InlineKeyboardButton();
        backBtn.setText(BACK_COMMAND);
        backBtn.setCallbackData(CATEGORY.getValue() + product.getCategoryId());
        inlineRow.add(backBtn);

        InlineKeyboardButton addToCartBtn = new InlineKeyboardButton();
        addToCartBtn.setText("Add to Cart");
        addToCartBtn.setCallbackData(CART.getValue() + product.getId());
        inlineRow.add(addToCartBtn);
        return inlineRow;
    }

    private void addToCart(CallbackQuery query) {
        User from = query.getFrom();
        Long chatId = query.getMessage().getChatId();
        Long telegramUserId = from.getId();
        Integer messageId = query.getMessage().getMessageId();
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
        Map<UUID, Integer> productCounts = USER_PRODUCT_QUANTITIES.get(chatId);
        Integer productCount = productCounts.get(productId);
        if (!found) {
            CartItem newItem = new CartItem(product.getId(), productCount);
            cartItems.add(newItem);
        }

        cartServiceBot.saveCart(cart);
        ReplyKeyboardMarkup markup = viewCartMenuKeyboard();
        markup.setOneTimeKeyboard(true);
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageId);
        deleteMessage.setChatId(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("HTML");
        sendMessage.setText("✅ <b>" + product.getName() + "</b> added to your cart.");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(markup);

        try {
            execute(deleteMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void cartMenage(Message message) {
        User user = message.getFrom();
        Long chatId = message.getChatId();

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
        ReplyKeyboardMarkup markup = sendButtonsForCart(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(markup);
        sendMessage.setText(printCart.toString());
        sendMessage.setParseMode("HTML");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private ReplyKeyboardMarkup sendButtonsForCart(Long chatId) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Confirm"));
        rows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Clear cart"));
        rows.add(row2);
        KeyboardRow row3 = new KeyboardRow();
        row2.add(new KeyboardButton("Back"));
        row2.add(new KeyboardButton("Edit"));
        rows.add(row3);
        markup.setKeyboard(rows);
        return markup;
    }

    public ReplyKeyboardMarkup viewCartMenuKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Cart"));
        rows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Back"));
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }

    public void sendMenuButtons(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        ReplyKeyboardMarkup r = new ReplyKeyboardMarkup();
        r.setResizeKeyboard(true);
        r.setOneTimeKeyboard(false);
        List<KeyboardRow> rows = new ArrayList<>();
        r.setKeyboard(rows);
        KeyboardRow row = new KeyboardRow();
        row.add("Products");
        rows.add(row);
        row = new KeyboardRow();
        row.add("Cart");
        row.add("Orders");
        rows.add(row);
        row = new KeyboardRow();
        row.add("Send a request to the admin");
        rows.add(row);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(r);
        sendMessage.setText("Select a button");
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
        return row;
    }

    public void editProducts(Long chatId, Integer messageId, UUID categoryId, int page) {
        List<Product> allProducts = productServiceBot.getProductsByCategoryId(categoryId);

        if (allProducts.isEmpty()) {
            sendMessage(chatId, "Sorry. There are no products yet!");
            return;
        }

        int pageSize = 7;
        int fromIndex = page * pageSize;
        if (fromIndex >= allProducts.size()) {
            sendMessage(chatId, "No products on this page.");
            return;
        }

        int toIndex = Math.min(fromIndex + pageSize, allProducts.size());
        List<Product> pageProducts = allProducts.subList(fromIndex, toIndex);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.addAll(createProductButtons(pageProducts));
        List<InlineKeyboardButton> navRow = createNavigationButtons(categoryId, page, toIndex, allProducts.size());
        if (!navRow.isEmpty()) keyboard.add(navRow);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
        sendOrEditMessageForProduct(chatId, messageId, markup, page == 0);
    }

    private List<List<InlineKeyboardButton>> createProductButtons(List<Product> products) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (int i = 0; i < products.size(); i += 2) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            Product p1 = products.get(i);
            row.add(button(p1.getName(), PRODUCT.getValue() + p1.getId()));

            if (i + 1 < products.size()) {
                Product p2 = products.get(i + 1);
                row.add(button(p2.getName(), PRODUCT.getValue() + p2.getId()));
            }

            keyboard.add(row);
        }
        return keyboard;
    }

    private List<InlineKeyboardButton> createNavigationButtons(UUID categoryId, int page, int toIndex, int totalSize) {
        List<InlineKeyboardButton> navRow = new ArrayList<>();

        if (page == 0) {
            navRow.add(button("⬅️", CATEGORY.getValue() + categoryId));
        } else {
            navRow.add(button("⬅️", "PRODUCT_PAGE:" + categoryId + ":" + (page - 1)));
        }

        if (toIndex < totalSize) {
            navRow.add(button("➡️", "PRODUCT_PAGE:" + categoryId + ":" + (page + 1)));
        }

        return navRow;
    }

    private InlineKeyboardButton button(String text, String data) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(data);
        return button;
    }

    private void sendOrEditMessageForProduct(Long chatId, Integer messageId, InlineKeyboardMarkup markup, boolean isNewMessage) {
        try {
            if (isNewMessage) {
                SendMessage message = new SendMessage(chatId.toString(), "🛍️ Select a product:");
                message.setReplyMarkup(markup);
                execute(new DeleteMessage(chatId.toString(), messageId));
                execute(message);
            } else {
                EditMessageText message = new EditMessageText();
                message.setChatId(chatId.toString());
                message.setMessageId(messageId);
                message.setText("🛍️ Select a product:");
                message.setReplyMarkup(markup);
                execute(message);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to send or edit message", e);
        }
    }

    public void editCategories(Long chatId, UUID id, Integer messageId) {
        Category categoryById = null;
        if (!id.equals(ROOT_CATEGORY_ID)) {
            categoryById = categoryServiceBot.getCategoryById(id);
        }

        List<Category> nextCategories = categoryServiceBot.getCategoriesByLevel(id);


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
            CategoryInlineKeyboardMarkup customMarkup = new CategoryInlineKeyboardMarkup(nextCategories, 3);
            List<List<InlineKeyboardButton>> categoryButtons = customMarkup.createInlineKeyboardMarkup().getKeyboard();

            InlineKeyboardButton backBtn = new InlineKeyboardButton(BACK_COMMAND);
            backBtn.setCallbackData(CATEGORY.getValue() + (categoryById != null ? categoryById.getParentId() : ROOT_CATEGORY_ID));

            categoryButtons.add(List.of(backBtn));
            keyboard = categoryButtons;
        }

        markup.setKeyboard(keyboard);

        if (categoryById != null) {
            try {
                execute(new DeleteMessage(chatId.toString(), messageId));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            sendCategoryPhoto(chatId, "🖼 " + categoryById.getName(), PhotosEnum.valueOf(categoryById.getName().toUpperCase()), markup);
        } else {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("📂 Select a category:");
            message.setReplyMarkup(markup);

            try {
                execute(new DeleteMessage(chatId.toString(), messageId));
                execute(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
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

    public void sendCategoryPhoto(Long chatId, String text, PhotosEnum photo, InlineKeyboardMarkup markup) {
        if (photo != null) {
            SendPhoto message = new SendPhoto();
            message.setChatId(chatId);
            message.setReplyMarkup(markup);
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

    public void giveRootCategories(Long chatId, UUID id) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        List<Category> nextCategories = categoryServiceBot.getCategoriesByLevel(id);
        CategoryInlineKeyboardMarkup categoryInlineKeyboardMarkup = new
                CategoryInlineKeyboardMarkup(nextCategories, 3);

        InlineKeyboardMarkup markup = categoryInlineKeyboardMarkup.createInlineKeyboardMarkup();

        message.setChatId(chatId);
        message.setText("Select a category");
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    public void saveToFile() {
        writeToJson("botRecurse/usersInfoOrState.json", USER_PRODUCT_QUANTITIES);
    }

    static {
        if (BOT_USERS == null || BOT_USERS.isEmpty()) {
            BOT_USERS_MAP = new HashMap<>();
        } else {
            BOT_USERS_MAP = BOT_USERS.stream()
                    .collect(Collectors.toMap(
                            BotUser::getChatId,
                            botUser -> new Contact(
                                    botUser.getPhoneNumber(),
                                    botUser.getFullName(),
                                    botUser.getFullName(),
                                    botUser.getUserId(),
                                    ""
                            ),
                            (existing, replacement) -> existing
                    ));
        }

        File file = new File("C:/Java/4 - model/Bazar24/src/main/java/uz/pdp/recurse/botRecurse/usersInfoOrState.json");
        if (file.length() == 0) {
            USER_PRODUCT_QUANTITIES = new HashMap<>();
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                USER_PRODUCT_QUANTITIES = objectMapper.readValue(
                        new File("C:/Java/4 - model/Bazar24/src/main/java/uz/pdp/recurse/botRecurse/usersInfoOrState.json"),
                        new TypeReference<Map<Long, Map<UUID, Integer>>>() {
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}