package controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;

import beans.User;
import dao.*;
import utils.ConnectionHandler;

@WebServlet("/UploadImage")
@MultipartConfig
public class UploadImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	String folderPath = "";

	public void init() throws ServletException {
		folderPath = getServletContext().getInitParameter("uploadLocation");
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		Part filePart = null;
		String title = null;
		String description = null;
		try {
			filePart = request.getPart("file");
			title = StringEscapeUtils.escapeJava(request.getParameter("title"));
			description = StringEscapeUtils.escapeJava(request.getParameter("description"));
			if (filePart == null || filePart.getSize() <= 0) {
				throw new Exception("Missing file in the request");
			}
			if (title == null || title.isEmpty()) {
				throw new Exception("Missing or empty title");
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file or title");
			return;
		}

		String contentType = filePart.getContentType();
		if (!contentType.startsWith("image")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File format not permitted");
			return;
		}

		String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		String outputPath = folderPath + fileName;
		
		File file = new File(outputPath);

		try (InputStream fileContent = filePart.getInputStream()) {
			Files.copy(fileContent, file.toPath());

			User user = (User) session.getAttribute("user");
			ImageDAO imageDAO = new ImageDAO(connection);
			try {
				imageDAO.addImage(title, description, fileName, user.getEmail());
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to upload image");
				return;
			}
			request.getSession().setAttribute("user", user);
			response.sendRedirect(getServletContext().getContextPath() + "/GoToHome");
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving file");
		}

	}

}