package ru.joutak.template.commands;

import java.util.Hashtable;

public class HelpCommand implements Command{
    @Override
    public void execute() {
        Hashtable<String, Command> commands = new Hashtable<>();
        commands.put("help", new HelpCommand());

    }

    @Override
    public String describe() {
        return "/help - помощь (вывести все команды)";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
