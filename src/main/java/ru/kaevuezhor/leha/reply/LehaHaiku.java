package ru.kaevuezhor.leha.reply;

import java.util.Random;

/**
 * Коллекция тематических хокку для финальных сообщений
 */
public enum LehaHaiku {
    VERSION_1("Лёха ест в ночи —\nТени тают на столе.\nЛуна тоже пуста.\n[1/9]"),
    VERSION_2("Листья падают в миску.\nГолодный ветер воет —\nЛёха все сожрал.\n[2/9]"),
    VERSION_3("Снег растаял в чашке.\nРисовые поля пусты —\nЛёха не дремлет.\n[3/9]"),
    VERSION_4("Весенний дождь стучит.\nНа подоконнике крошки —\nЛёха спит, сытый.\n[4/9]"),
    VERSION_5("Лето. Муха жужжит\nНад пустой тарелкой. Лёха\nСъел даже солнце.\n[5/9]"),
    VERSION_6("Осенний ветер воет.\nНет зерна в амбарах —\nЛёха все сожрал.\n[9/9]"),
    VERSION_7("Луна в облаках скрылась.\nМыши шепчут в кладовке:\n'Лёха все сожрал!'\n[7/9]"),
    VERSION_8("Роса на траве блестит.\nЛёха, сонный, икает —\nВ животе гроза.\n[8/9]");

    private final String text;      // Текст хокку
    private static final Random RANDOM = new Random();

    LehaHaiku(String text) {
        this.text = text;
    }

    /**
     * @return Случайно выбранное хокку
     */
    public static LehaHaiku getRandom() {
        LehaHaiku[] all = values();
        return all[RANDOM.nextInt(all.length)];
    }

    /**
     * @return Форматированный текст хокку
     */
    public String getText() {
        return text;
    }
}