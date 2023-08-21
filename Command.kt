package gitinternals

enum class Command(val string: String) {
    LIST_BRANCHES("list-branches"),
    CAT_FILE("cat-file"),
    LOG("log")
    ;

    companion object {
        fun getCommand(input: String): Command {
            for (command in Command.values()) {
                if (command.string == input) return command
            }

            throw RuntimeException("Wrong command: $input")
        }
    }
}