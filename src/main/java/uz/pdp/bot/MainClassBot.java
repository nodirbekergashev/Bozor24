package uz.pdp.bot;

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
import uz.pdp.bot.factory.ReplyKeyboardFactory;
import uz.pdp.bot.factory.inline.CategoryInlineKeyboardMarkup;
import uz.pdp.bot.service.*;
import uz.pdp.bot.util.PhotoUtil;
import uz.pdp.enums.PhotosEnum;
import uz.pdp.enums.UserRole;
import uz.pdp.itemClasses.CartItem;
import uz.pdp.model.Cart;
import uz.pdp.model.Category;
import uz.pdp.model.Order;
import uz.pdp.model.Product;

import java.text.SimpleDateFormat;
import java.util.*;

import static uz.pdp.bot.factory.ReplyKeyboardFactory.createReplyKeyboard;
import static uz.pdp.enums.CallBackQueryStarting.*;
import static uz.pdp.model.Category.ROOT_CATEGORY_ID;

public class MainClassBot extends TelegramLongPollingBot {
    private static final String BOT_USERNAME = "bazar_24_bot";
    private static final String BOT_TOKEN = "7916545438:AAFvuOgnucYZdfFIrILAmygu7-DxL6vFujo";
    static CategoryServiceBot categoryServiceBot = new CategoryServiceBot();
    static ProductServiceBot productServiceBot = new ProductServiceBot();
    static UserServiceBot userServiceBot = new UserServiceBot();
    static CartServiceBot cartServiceBot = new CartServiceBot();
    static OrderServiceBot orderServiceBot = new OrderServiceBot();

    private final Map<Long, Map<UUID, Integer>> userProductQuantities = new HashMap<>();
    private final Map<Long, Contact> botUsers = new HashMap<>();
    private static final String BACK_COMMAND = "🔙 Back";
    private static final String CUSTOMER_ROLE = "CUSTOMER";
    private static final String SELLER_ROLE = "SELLER";

    // State management
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

        if (message.hasContact() && !botUsers.containsKey(chatId)) {
            handleContactMessage(message, chatId);
            return;
        }

        // Handle text messages
        if (message.hasText()) {
            String text = message.getText();
            switch (text) {
                case "/start" -> handleStartCommand(chatId);
                case "/help", "Help" -> sendHelpMessage(chatId);
                case "Products" -> browseCategories(chatId, ROOT_CATEGORY_ID);
                case "Cart" -> cartMenage(message);
                case "Orders" -> manageOrders(chatId, message.getMessageId());
                case "Back" -> sendMenuButtons(chatId);
                case "Confirm" -> createNewOrder(chatId);
                case "Cancel" -> clearCart(chatId);
                case CUSTOMER_ROLE, SELLER_ROLE -> handleRoleSelection(chatId, text, message);
                default -> sendMessage(chatId, "Sorry, unknown command click /start");
            }
        }
    }

    private void clearCart(Long chatId) {
        userProductQuantities.remove(chatId);
        UUID userId = userServiceBot.getUserIdByTgUser(chatId);
        Cart cartByUserId = cartServiceBot.getCartByUserId(userId);
        if (cartByUserId != null) {
            cartServiceBot.clearCart(cartByUserId.getId());
        }
    }

    private void handleContactMessage(Message message, Long chatId) throws TelegramApiException {
        // Store contact and ask for role
        botUsers.put(chatId, message.getContact());
        userStates.put(chatId, UserState.AWAITING_ROLE);
        askForUserRole(chatId);
    }

    private void handleStartCommand(Long chatId) throws TelegramApiException {
        if (botUsers.get(chatId) == null) {
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
        message.setText("Please select your role");
        message.setReplyMarkup(chooseUserRole());
        execute(message);
        userStates.put(chatId, UserState.AWAITING_ROLE);
    }

    private void handleRoleSelection(Long chatId, String role, Message message) throws TelegramApiException {
        Contact contact = botUsers.get(chatId);
        if (contact == null) {
            sendMessage(chatId, "Please share your contact first");
            return;
        }

        // Save user with selected role
        userServiceBot.add(
                contact.getPhoneNumber(),
                contact.getFirstName() + " " + contact.getLastName(),
                contact.getUserId(),
                UserRole.valueOf(role),
                chatId
        );

        // Clean up and show menu
        execute(new DeleteMessage(chatId.toString(), message.getMessageId()));
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
            editCategories(chatId, UUID.fromString(categoryIdString));
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
        userProductQuantities.putIfAbsent(chatId, new HashMap<>());
        Map<UUID, Integer> productMap = userProductQuantities.get(chatId);
        productMap.putIfAbsent(productId, 1);

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

        userProductQuantities.putIfAbsent(chatId, new HashMap<>());
        Map<UUID, Integer> productMap = userProductQuantities.get(chatId);
        productMap.putIfAbsent(productId, 1);
        int chosenQuantity = productMap.get(productId);

        updateProductMessage(chatId, messageId, product, chosenQuantity);
    }

    private void handleProductPagination(CallbackQuery callbackQuery) throws TelegramApiException {
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
                            "💰 Total: %s\n" +
                            "🛒 Items: %d\n" +
                            "📦 Status: %s\n\n",

                    formattedDate,
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

        double random = Math.random();

        String str = String.valueOf(random);
        String orderId = str.substring(2);
        Order newOrder = orderServiceBot.createOrderFromCart(userId, cart);
        sendMessage(chatId, String.format(
                "✅ Order #%s created successfully!\n" +
                        "Total: %s\n" +
                        "Thank you for your purchase!",
                orderId,
                newOrder.getTotalPrice()
        ));


        // Clear the cart after order creation
        clearCart(chatId);
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
        message.setText("Please share your phone number:");


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
        Map<UUID, Integer> productCounts = userProductQuantities.get(chatId);
        Integer productCount = productCounts.get(productId);
        if (!found) {
            CartItem newItem = new CartItem(product.getId(), productCount);
            cartItems.add(newItem);
        }

        cartServiceBot.saveCart(cart);
        ReplyKeyboardMarkup markup = viewCartMenuKeyboard();
        markup.setOneTimeKeyboard(true);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("HTML");
        sendMessage.setText("✅ <b>" + product.getName() + "</b> added to your cart.");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(markup);


        try {
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
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Confirm"));
        rows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Cancel"));
        rows.add(row2);
        KeyboardRow row3 = new KeyboardRow();
        row2.add(new KeyboardButton("Back"));
        rows.add(row3);
        markup.setKeyboard(rows);
        return markup;
    }

    public static ReplyKeyboardMarkup viewCartMenuKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Cart"));
        rows.add(row1);

        KeyboardRow row2 = new KeyboardRow(); // todo: Back to Main Menu
        row2.add(new KeyboardButton("Back"));
        rows.add(row2);

        markup.setKeyboard(rows);
        return markup;
    }


    //The buttons that should appear when the user presses start
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