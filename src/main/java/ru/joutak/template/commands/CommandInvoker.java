package ru.joutak.template.commands;

import org.bukkit.command.defaults.HelpCommand;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Класс для управления и выполнения команд приложения.
 * <p>
 * Служит центральным диспетчером команд, предоставляя возможность
 * их регистрации, вызова и выполнения. Поддерживает интерактивный режим
 * работы с консолью и выполнение команд из скриптов.
 * </p>
 * <p>
 * При запуске автоматически регистрирует все доступные команды
 * и предоставляет интерактивный интерфейс для их вызова.
 * </p>
 */
public class CommandInvoker {
    /** Сканер для чтения пользовательского ввода из консоли */
    private static Scanner sc = new Scanner(System.in);
    private static HashMap<String, Command> commands = new HashMap<String, Command>();

    /**
     * Создаёт новый invoking и регистрирует все доступные команды.
     * <p>
     * В конструкторе выполняется регистрация следующих команд:
     * <ul>
     *   <li>help — вывод справки по командам</li>
     *   <li>clear — очистка коллекции</li>
     *   <li>info — информация о коллекции</li>
     *   <li>show — отображение всех элементов</li>
     *   <li>exit — завершение работы</li>
     *   <li>execute_file — выполнение команд из файла</li>
     *   <li>insert — добавление элемента</li>
     *   <li>update — обновление элемента по id</li>
     *   <li>print_unique_author — вывод уникальных авторов</li>
     *   <li>save — сохранение коллекции в файл</li>
     *   <li>remove_key — удаление элемента по ключу</li>
     *   <li>print_field_descending_difficulty — вывод сложности по убыванию</li>
     *   <li>group_counting_by_maximum_point — группировка по максимальному баллу</li>
     *   <li>remove_lower_key — удаление элементов с меньшими ключами</li>
     *   <li>replace_if_greater — замена при большем значении</li>
     *   <li>replace_if_lower — замена при меньшем значении</li>
     * </ul>
     */
    public CommandInvoker(Player p) {
        commands.put("help", new HelpCommand());

    }

    /**
     * Запускает интерактивный режим работы приложения.
     * <p>
     * Читает команды из стандартного ввода построчно и передаёт их
     * на выполнение до завершения работы программы.
     * </p>
     */
    public void run() {
        while (sc.hasNext()) {
            String line = sc.nextLine();
            runParticularCommand(line);
        }
    }

    /**
     * Выполняет конкретную команду с заданными аргументами.
     * <p>
     * Парсит строку команды, разделяя её на имя и аргументы.
     * Проверяет существование команды и корректность количества аргументов.
     * Для команд, реализующих {@link Modable}, устанавливает аргументы перед выполнением.
     * </p>
     *
     * @param line строка с именем команды и её аргументами
     */
    private static void runParticularCommand(String line){
        String[] tokens = line.trim().replace("\n", "").split(" ");

        String commandName = tokens[0];
        ArrayList<String> args = new ArrayList<>();
        if (tokens.length > 1) {
            for (int i = 1; i < tokens.length; i++) {
                args.add(tokens[i]);
            }
        }

        Command command = commands.get(commandName);
        if (command == null) {
            System.out.println("Неизвестная команда: " + commandName);
            return;
        }



        if (command instanceof Modable) {
            if(args.size()==command.numberArgsRequired())
                ((Modable) command).setArguments(args);
            else {
                System.out.println("Слишком мало/много аргументов. Нужно " + command.numberArgsRequired() + " аргументов.");
                return;
            }
        }

        if(tokens[0] == ""){
            return;
        }

        command.execute();
    }
}
