package systems.common;

import java.io.File;
import java.io.Serializable;

public class Message implements Serializable {

    private String command;
    private File file;
    private byte[] data;

    public Message(String command, File file, byte[] data) {
        this.command = command;
        this.file = file;
        this.data = data;
    }

    public String getCommand() {
        return command;
    }

    public File getFile() {
        return file;
    }

    public byte[] getData() {
        return data;
    }
}
