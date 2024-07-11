package controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.User;
import dao.*;
import utils.ConnectionHandler;

@WebServlet("/UploadImage")
@MultipartConfig
public class UploadImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	String folderPath = "";

	public void init() throws ServletException {
		folderPath = getServletContext().getInitParameter("uploadLocation");
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
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
			// TODO: WHAT HAPPENS IF A FILE WITH THE SAME NAME ALREADY EXISTS?
			// you could override it, send an error or 
			// rename it, for example, if I need to upload images to an album, and for each image I also save other data, I could save the image as {image_id}.jpg using the id of the db
			
			Files.copy(fileContent, file.toPath());
			
			String loginpath = getServletContext().getContextPath()+ "/index.html";
			HttpSession session = request.getSession();
			if (session.isNew() || session.getAttribute("user") == null) {
				response.sendRedirect(loginpath);
				return;
			}
			
			User user = (User) session.getAttribute("user");
			
			ImageDAO imageDAO = new ImageDAO(connection);
			imageDAO.addImage(title, description, outputPath, user.getEmail());
			
			request.getSession().setAttribute("user", user);
			response.sendRedirect(getServletContext().getContextPath() + "/GoToHome");
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving file");
		}

	}

}