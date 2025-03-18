import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet("/FileUploadServlet")
@MultipartConfig(maxFileSize = 1024 * 1024 * 10) // 10MB limit
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/employee";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("file");
        String fileName = filePart.getSubmittedFileName();
        byte[] fileBytes = filePart.getInputStream().readAllBytes();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = con.prepareStatement(
                         "INSERT INTO file (filename, filedata, uploadtime) VALUES (?, ?, NOW())")) {
                stmt.setString(1, fileName);
                stmt.setBlob(2, new ByteArrayInputStream(fileBytes));
                stmt.executeUpdate();
            }

            response.getWriter().write("File uploaded successfully via Servlet.");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("File upload failed.");
        }
    }
}