package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Album;

public class AlbumDAO {
	private Connection connection;
	
	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void createAlbum(String title, String userEmail) throws SQLException {
		String query = "INSERT into Album (Title, Creator) VALUES (?, ?)";
		try (PreparedStatement pstatment = connection.prepareStatement(query);) {
			pstatment.setString(1, title);
			pstatment.setString(2, userEmail);
			pstatment.executeUpdate();
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
