package ru.joutak.template.commands;

import java.util.ArrayList;

/**
 * Интерфейс для команд, требующих аргументы.
 * <p>
 * Расширяет базовый интерфейс {@link Command}, добавляя возможность
 * передачи аргументов командной строки. Команды, реализующие этот интерфейс,
 * могут принимать дополнительные параметры при вызове.
 * </p>
 */
public interface Modable extends Command {
    /**
     * Устанавливает аргументы командной строки для выполнения команды.
     *
     * @param args список строковых аргументов, переданных команде
     */
    public void setArguments(ArrayList<String> args);
}
