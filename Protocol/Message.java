package Protocol;

import java.io.EOFException;

public class Message {
    private String header;
    private String content;

    public Message(String mess) throws EOFException {
        this.header = mess.substring(0, 1);
        this.content = mess.substring(1);
        
    }

    public Message(String header, String content) {
        this.header = header;
        this.content = content;
    }

    public String getHeader() {
        return header;
    }

    public String getContent() {
        return content;
    }

    public String getMessage() {
        return header + content;
    }
}
