import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static volatile AtomicInteger counterThree = new AtomicInteger();
    public static volatile AtomicInteger counterFour = new AtomicInteger();
    public static volatile AtomicInteger counterFive = new AtomicInteger();

    public static void main(String[] args) {

        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        //сгенерированное слово является палиндромом (abba)
        new Thread(() -> {
            final int numberOfPalindromeCharacters = 4;
            final int zero = 0;

            for (String valve : texts) {
                StringBuffer buffer = new StringBuffer(valve);
                buffer.reverse();
                String data = buffer.toString();

                // убрал варианты вида ababa, bab - хотя тоже палиндромы. Т.е строка только из 4-х символов.
                if (valve.equals(data) && valve.length() == numberOfPalindromeCharacters) {
                    if (!valve.substring(zero, zero + 1).equals(valve.substring(zero + 2, zero + 3))) {
                        counterFour.addAndGet(1);
                    }
                }
            }
        }).start();

        //сгенерированное слово состоит из одной и той же буквы (aaa)
        new Thread(() -> {
            for (String valve : texts) {
                char[] chars = valve.toCharArray();
                Arrays.sort(chars); // сортировка для удобства поиска с двух сторон
                String s = String.valueOf(chars);

                for (int i = 0; i < chars.length; ) {
                    // встречаемость символа в строке
                    int charNum = s.lastIndexOf(chars[i]) - s.indexOf(chars[i]) + 1;
                    // если встретился столько раз, сколько символов в строке - Наш клиент
                    if (charNum == valve.length()) {
                        switch (valve.length()) {
                            case 3:
                                counterThree.addAndGet(1);
                                break;
                            case 4:
                                counterFour.addAndGet(1);
                                break;
                            case 5:
                                counterFive.addAndGet(1);
                                break;
                        }
                    }
                    i += (s.lastIndexOf(chars[i]) - s.indexOf(chars[i]) + 1);
                }
            }
        }).start();

        //буквы в слове идут по Алфавиту
        new Thread(() -> {
            for (String valve : texts) {
                char[] chars = valve.toCharArray();
                Arrays.sort(chars);
                String valveSort = String.valueOf(chars);

                if (valve.equals(valveSort)) {
                    switch (valve.length()) {
                        case 3:
                            counterThree.addAndGet(1);
                            break;
                        case 4:
                            counterFour.addAndGet(1);
                            break;
                        case 5:
                            counterFive.addAndGet(1);
                            break;
                    }
                }
            }
        }).start();

        System.out.println("Красивых слов с длиной 3: " + counterThree + " шт");
        System.out.println("Красивых слов с длиной 4: " + counterFour + " шт");
        System.out.println("Красивых слов с длиной 5: " + counterFive + " шт");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}