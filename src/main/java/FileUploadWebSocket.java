import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@ServerEndpoint("/FileUploadWebSocket")
public class FileUploadWebSocket {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employee";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private String filename;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection established: " + session.getId());
    }

    @OnMessage
    public void processFileName(String filename, Session session) {
        this.filename = filename;
        System.out.println("Receiving file: " + filename);
    }
    @OnMessage
    public void processFileData(ByteBuffer fileData, Session session) {
        System.out.println("File data received: " + fileData.remaining() + " bytes");
        byte[] fileBytes = new byte[fileData.remaining()];
        fileData.get(fileBytes);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 PreparedStatement stmt = con.prepareStatement(
                         "INSERT INTO file (filename, filedata, uploadtime) VALUES (?, ?, NOW())")) {
                stmt.setString(1, filename);
                stmt.setBlob(2, new ByteArrayInputStream(fileBytes));
                stmt.executeUpdate();
                session.getBasicRemote().sendText("File upload successful via WebSocket.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.getBasicRemote().sendText("File upload failed.");
            } catch (Exception io) {
                io.printStackTrace();
            }
        }
    }
}