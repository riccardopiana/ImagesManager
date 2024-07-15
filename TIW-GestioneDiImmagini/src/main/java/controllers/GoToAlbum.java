package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import dao.*;
import utils.ConnectionHandler;
import beans.*;

@WebServlet("/GoToAlbum")
public class GoToAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
    public GoToAlbum() {
        super();
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		Integer albumId = null;
		try {
			albumId = Integer.parseInt(request.getParameter("albumId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect albumId");
			return;
		}
		
		int page = 1;
		if (request.getParameter("page") != null) {
			try {
	            page = Integer.parseInt(request.getParameter("page"));
	        } catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect page value");
			return;
	        }
		}
		
		AlbumDAO albumDAO = new AlbumDAO(connection);
		ImageDAO imageDAO = new ImageDAO(connection);
		Album album = new Album();
		List<Image> images = new ArrayList<Image>();
		int totalPages = 0;
		
		try {
			album = albumDAO.findById(albumId);
			if (album == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Album not found");
				return;
			}
			Album currentAlbum = (Album) session.getAttribute("album");
			if (currentAlbum != null && album.getId() != currentAlbum.getId()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values 1");
				return;
			} else {
				session.setAttribute("album", album);
				images = imageDAO.findFiveImages(albumId, page);
				totalPages = albumDAO.numImagesAlbum(albumId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover album 2");
			return;
		}
		
		String path = "/WEB-INF/AlbumPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("album", album);
		ctx.setVariable("creationDate", album.getCreationDate().toString());
		ctx.setVariable("images", images);
		ctx.setVariable("currentPage", page);
		ctx.setVariable("totalPages", totalPages);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
