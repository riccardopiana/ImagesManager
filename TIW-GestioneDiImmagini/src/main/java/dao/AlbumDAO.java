package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.Album;

public class AlbumDAO {
	private Connection connection;
	
	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void createAlbum(String title, String userEmail, int[] imageIds) throws SQLException {
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
			
			query = "INSERT ImageOfAlbum (Album, Image) VALUES ";
			for (int imageId : imageIds) {
				query += "(@last_album_id, " + imageId + "), ";
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
		String query = "SELECT * FROM Album WHERE Creator = ?";
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
		String query = "SELECT * FROM Album WHERE Creator != ?";
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
}
