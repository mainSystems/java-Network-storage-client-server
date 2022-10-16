package systems.common;

import java.io.File;
import java.io.Serializable;

public class Message implements Serializable {

    private final CommandType commandType;
    private StorageCommands storageCommand;
    private SqlCommands sqlCommands;
    private String username;
    private String password;
    private File file;
    private byte[] data;
    private String filePath;


    public Message(StorageCommands command, String username, File file, byte[] data) {
        commandType = CommandType.STORAGE;
        this.storageCommand = command;
        this.file = file;
        this.data = data;
        this.username = username;
    }

    public Message(StorageCommands command, String filePath) {
        commandType = CommandType.STORAGE;
        this.storageCommand = command;
        this.filePath = filePath;
    }

    public Message(SqlCommands command, String username, String password) {
        commandType = CommandType.SQL;
        this.sqlCommands = command;
        this.username = username;
        this.password = password;
    }

    public Message(SqlCommands command, String username) {
        commandType = CommandType.SQL;
        this.sqlCommands = command;
        this.username = username;
    }

    public StorageCommands getStorageCommand() {
        return storageCommand;
    }

    public File getFile() {
        return file;
    }

    public byte[] getData() {
        return data;
    }

    public SqlCommands getSqlCommands() {
        return sqlCommands;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getFilePath() {
        return filePath;
    }
}
