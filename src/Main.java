import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class Main {
    static String fileName = "russian_nouns_with_definition_5_and_more_letters.txt";
    static List<String> wrongLetters = new ArrayList<>(Arrays.asList(" ", " "));
    static List<String> rightLetters = new ArrayList<>(Arrays.asList(" ", " "));
    public static void main(String[] args) throws IOException {
        if (!greetUser()) return;
        do {
            doOneGame();
        } while (takeUserAnswer("[И]грать еще раз - клавиша И + Enter. [В]ыйти - клавиша В + Enter. Регистр и язык en/ru не имеет значения."));
        return;
    }
    private static boolean greetUser() {
        System.out.println("Приветствую Вас в игре Виселица. Вы должны угадать слово, вводя по одной букве за шаг.");
        System.out.println("Это имя существительное, нарицательное, в именительном падеже. Разрешается 7 неправильных попыток.");
        System.out.println("После пятой неправильной попытки выдается подсказка из словаря.");
        return takeUserAnswer("Начать [и]гру - клавиша И + Enter. [В]ыйти - клавиша В + Enter. Регистр и язык en/ru не имеет значения.");
    }
    private static void doOneGame() throws IOException {
        int wrongLettersMaxNumber = 7;
        boolean solved = false;
        String[] sourceWord;
        if (!wrongLetters.isEmpty()) wrongLetters.clear();
        if (!rightLetters.isEmpty()) rightLetters.clear();
        sourceWord = getRandomLineFromFile();
        while (wrongLetters.size() < wrongLettersMaxNumber && !solved) {
            printAll(sourceWord);
            solved = checkLetter(takeLetter(), sourceWord);
        }
        if (solved) {
            System.out.println("Поздравляем! Вы угадали слово!");
            System.out.println(sourceWord[0] + " - " + sourceWord[1]);

        }
    }
    private static boolean takeUserAnswer(String msg) {
        while (true) {
            System.out.println(msg);
            Scanner in = new Scanner(System.in);
            String beginOrExit = in.next().toLowerCase();
            if (beginOrExit.matches("b|и")) return true;
            if (beginOrExit.matches("d|в")) return false;
        }
    }
    private static String[] getRandomLineFromFile() throws IOException {
        Stream<String> fileLines = Files.lines(Paths.get(fileName));
        long randomLineNumber = ThreadLocalRandom.current().nextLong(0, fileLines.count());
        fileLines = Files.lines(Paths.get(fileName));
        return fileLines.skip(randomLineNumber).findFirst().get().split("\t", 2);
    }
    private static void printAll(String[] sourceWord) {
        String wordToPrint;
        String[] line = new String[7];
        wordToPrint= updateWordToPrint(sourceWord);
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println();
        line[0] = " ╓────┐" + (wrongLetters.size() > 4 ? "    Подсказка: " + sourceWord[1] : "");
        line[1] = " ║    " + (wrongLetters.size() > 0 ? "│" : " ");
        line[2] = " ║    " + (wrongLetters.size() > 1 ? "0" : " ") + "        Слово: " + String.join(" ", wordToPrint.split(""));
        line[3] = " ║  " + (wrongLetters.size() > 3 ? "/" : " ") + (wrongLetters.size() > 2 ? "( )" + (wrongLetters.size() > 4 ? "\\" : "") : " ");
        line[4] = " ║   " + (wrongLetters.size() > 5 ? "/ " : "  ") + (wrongLetters.size() > 6 ? "\\" : " ") + "     Ошибки"
                + "(" + wrongLetters.size() + ") " + String.join(", ", wrongLetters);
        line[5] = " ║";
        line[6] = "═╩═══════     Введите букву: ";
        for (int i = 0; i < 6; i++) System.out.println(line[i]);
        System.out.print(line[6]);
    }
    private static String updateWordToPrint(String[] sourceWord) {
        String result = "";
        for (char ch: sourceWord[0].toCharArray()) {
            result += rightLetters.contains(String.valueOf(ch)) ? ch : "_";
        }
        return result;
    }
    private static String takeLetter() {
        while (true) {
            Scanner in = new Scanner(System.in);
            String letter = in.next().toLowerCase();
            if (letter.length() != 1 || !letter.matches("[а-я]")) {
                System.out.print(" Введите одну букву на кириллице: ");
            } else if (wrongLetters.contains(letter)) {
                System.out.print(" Вы уже вводили эту букву, ее нет в слове, введите другую букву: ");
            } else if (rightLetters.contains(letter)) {
                System.out.print(" Вы уже вводили эту букву, она есть в слове, введите другую букву: ");
            } else {
                return letter;
            }
        }
    }
    private static boolean checkLetter(String letter, String[] sourceWord) {
        if (sourceWord[0].indexOf(letter.charAt(0)) == -1) {
            wrongLetters.add(letter);
            return false;
        }
        else {
            rightLetters.add(letter);
            return sourceWord[0].equals(updateWordToPrint(sourceWord));
        }
    }
}
