package learnbpm.example.einfacherprozess;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

@WebServlet("/download/hzb")
public class HzbDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private static final String DB_URL = "jdbc:mysql://camunda-mysql:3306/immatrikulation";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");

        if (idParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'id' fehlt.");
            return;
        }

        int antragId;
        try {
            antragId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ungültige ID.");
            return;
        }

        String sql = "SELECT hzb_zeugnis FROM immatrikulationsantrag WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, antragId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                byte[] data = rs.getBytes("hzb_zeugnis");

                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "attachment; filename=\"HZB_Zeugnis.pdf\"");
                resp.setContentLength(data.length);

                OutputStream out = resp.getOutputStream();
                out.write(data);
                out.flush();
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Antrag nicht gefunden.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Fehler beim Datenbankzugriff.");
        }
    }
}
