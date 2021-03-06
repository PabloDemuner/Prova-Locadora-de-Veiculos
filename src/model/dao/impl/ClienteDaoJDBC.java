package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.ClienteDao;
import modelo.entidades.Automovel;
import modelo.entidades.Cliente;

public class ClienteDaoJDBC implements ClienteDao {

	private Connection conn;
	
	public ClienteDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Cliente obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO cliente "
					+ "(Nome, Email, DataNasc) "
					+ "VALUES "
					+ "(?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getDataNasc().getTime()));
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Erro inesperado! Sem filas afetadas!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Cliente obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE cliente "
							+ "SET Nome = ?, Email = ?, DataNasc = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getNome());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getDataNasc().getTime()));
			st.setInt(4, obj.getId());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM cliente WHERE Id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Cliente findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT cliente.*,automovel.Nome as AutNome "
					+ "FROM cliente INNER JOIN automovel "
					+ "ON cliente.AutomovelId = automovel.Id "
					+ "WHERE cliente.Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Automovel aut = instantiateAutomovel(rs);
				Cliente obj = instantiateCliente(rs, aut);
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Cliente instantiateCliente(ResultSet rs, Automovel aut) throws SQLException {
		Cliente obj = new Cliente();
		obj.setId(rs.getInt("Id"));
		obj.setNome(rs.getString("Nome"));
		obj.setEmail(rs.getString("Email"));
		obj.setDataNasc(new java.util.Date(rs.getTimestamp("DataNasc").getTime())); 
		//obj.setDataNasc(rs.getDate("DataNasc"));
		obj.setAutomovel(aut);
		return obj;
	}

	private Automovel instantiateAutomovel(ResultSet rs) throws SQLException {
		Automovel aut = new Automovel();
		aut.setId(rs.getInt("AutomovelId"));
		aut.setNome(rs.getString("AutNome"));
		return aut;
	}

	@Override
	public List<Cliente> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT * FROM cliente ORDER BY Nome");
			rs = st.executeQuery();

			List<Cliente> list = new ArrayList<>();

			while (rs.next()) {
				Cliente obj = new Cliente();
				obj.setId(rs.getInt("Id"));
				obj.setNome(rs.getString("Nome"));
				obj.setEmail(rs.getString("Email"));
				obj.setDataNasc(new java.util.Date(rs.getTimestamp("DataNasc").getTime())); 
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Cliente> findByAutomovel(Automovel automovel) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT cliente.*,automovel.Nome as AutoNome "
					+ "FROM cliente INNER JOIN automovel "
					+ "ON cliente.AutomovelId = automovel.Id "
					+ "WHERE AutomovelId = ? "
					+ "ORDER BY Nome");
			
			st.setInt(1, automovel.getId());
			
			rs = st.executeQuery();
			
			List<Cliente> list = new ArrayList<>();
			Map<Integer, Automovel> map = new HashMap<>();
			
			while (rs.next()) {
				
				Automovel aut = map.get(rs.getInt("AutomovelId"));
				
				if (aut == null) {
					aut = instantiateAutomovel(rs);
					map.put(rs.getInt("AutomovelId"), aut);
				}
				
				Cliente obj = instantiateCliente(rs, aut);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}
