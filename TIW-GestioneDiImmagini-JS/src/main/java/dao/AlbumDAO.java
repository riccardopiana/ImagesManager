package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import beans.Album;
import beans.Image;
import utils.ImageComparator;

public class AlbumDAO {
	private Connection connection;
	
	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void createAlbum(String title, String userEmail, List<Image> images) throws SQLException {
		try {
			connection.setAutoCommit(false);
			String query = "";
			
			query = "INSERT into Album (Title, Creator) VALUES (?, ?)";
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setString(1, title);
				pstatement.setString(2, userEmail);
				pstatement.executeUpdate();
			}
			Statement statement = connection.createStatement();
			
			query = "SET @last_album_id = LAST_INSERT_ID()";
			statement.executeUpdate(query);
			
			images.sort(new ImageComparator());
			
			query = "INSERT ImageOfAlbum (Album, Image, Position, User) VALUES ";
			for (Image image : images) {
				query += "(@last_album_id, " + image.getId() + ", '" + images.indexOf(image) + "', 'default'), ";
			}
			query = query.substring(0, query.length()-2);
			statement.executeUpdate(query);
			
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw new SQLException();
		} finally {
			connection.setAutoCommit(true);
		}
	}
	
	public List<Album> findByUser(String userEmail) throws SQLException {
		List<Album> albums = new ArrayList<Album>();
		String query = "SELECT * FROM Album WHERE Creator = ? ORDER BY CreationDate desc";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, userEmail);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Album album = new Album();
					album = new Album();
					album.setId(result.getInt("Id"));
					album.setTitle(result.getString("title"));
					album.setCreationDate(result.getDate("creationdate"));
					album.setCreator(result.getString("creator"));
					albums.add(album);
				}
			}
		}
		return albums;
	}
	
	public List<Album> findOthers(String userEmail) throws SQLException {
		List<Album> albums = new ArrayList<Album>();
		String query = "SELECT * FROM Album WHERE Creator != ? ORDER BY CreationDate desc";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, userEmail);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Album album = new Album();
					album = new Album();
					album.setId(result.getInt("Id"));
					album.setTitle(result.getString("title"));
					album.setCreationDate(result.getDate("creationdate"));
					album.setCreator(result.getString("creator"));
					albums.add(album);
				}
			}
		}
		return albums;
	}
	
	public Album findById(int albumId) throws SQLException {
		Album album = null;
		String query = "SELECT * FROM Album WHERE id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					album = new Album();
					album.setId(result.getInt("Id"));
					album.setTitle(result.getString("title"));
					album.setCreationDate(result.getDate("creationdate"));
					album.setCreator(result.getString("creator"));
				}
			}
		}
		return album;
	}

	public int numImagesAlbum(Integer albumId) throws SQLException{
		int totalPages = 0;
		int totalImages = 0;
		String query = "SELECT * FROM ImageOfAlbum WHERE Album = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					totalImages++;
				}
			}
		}
		if (totalImages % 5 == 0) {
			totalPages = totalImages/5;
		} else {
			totalPages = (totalImages/5) + 1;
		}
		return totalPages;
	}
	

	public void reorderAlbum(Integer albumId, Map<Integer, Image> imagesOrder, String userEmail) throws SQLException{
		boolean found = true;
		String query = "SELECT * FROM ImageOfAlbum WHERE User = ? AND Album = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, userEmail);
			pstatement.setLong(2, albumId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.next()) {
					found = false;
				}
			}
		}
		try {
			connection.setAutoCommit(false);
			if (found) {
				query = "";
				for (Integer position : imagesOrder.keySet()) {
					query = "UPDATE ImageOfAlbum SET position = ? WHERE album = " + albumId + " AND image = ? AND user = '" + userEmail + "'";
					try (PreparedStatement pstatement = connection.prepareStatement(query);) {
						pstatement.setLong(1, position);
						pstatement.setLong(2, imagesOrder.get(position).getId());
						pstatement.executeUpdate();
					}
				}
			} else {
				Statement statement = connection.createStatement();
				query = "";
				query = "INSERT ImageOfAlbum (Album, Image, Position, User) VALUES ";
				for (Integer position : imagesOrder.keySet()) {
					query += "(" + albumId + ", " + imagesOrder.get(position).getId() + ", '" + position + "', '" + userEmail + "'), ";
				}
				query = query.substring(0, query.length()-2);
				statement.executeUpdate(query);
			}
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw new SQLException();
		} finally {
			connection.setAutoCommit(true);
		}
	}
}
