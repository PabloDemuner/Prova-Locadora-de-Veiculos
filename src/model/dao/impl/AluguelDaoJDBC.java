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
import model.dao.AluguelDao;
import modelo.entidades.Aluguel;
import modelo.entidades.Automovel;
import modelo.entidades.Cliente;

public class AluguelDaoJDBC implements AluguelDao {

	private Connection conn;

	public AluguelDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Aluguel obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO aluguel " + "(DataInicio, DataFim) " + "VALUES " + "(?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setDate(1, new java.sql.Date(obj.getDataInicio().getTime()));
			st.setDate(2, new java.sql.Date(obj.getDataFim().getTime()));

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Erro inesperado! Sem filas afetadas!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Aluguel obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE aluguel " + "SET DataInicio = ?, DataFim = ? " + "WHERE Id = ?");

			st.setDate(1, new java.sql.Date(obj.getDataInicio().getTime()));
			st.setDate(2, new java.sql.Date(obj.getDataFim().getTime()));
			st.setInt(3, obj.getId());

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM aluguel WHERE Id = ?");

			st.setInt(1, id);

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Aluguel findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT aluguel.*,automovel.Nome as AutNome "
					+ "FROM aluguel INNER JOIN automovel " 
					+ "FROM aluguel INNER JOIN cliente "
					+ "ON aluguel.AutomovelId = automovel.Id " 
					+ "WHERE aluguel.Id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Automovel aut = instantiateAutomovel(rs);
				Cliente cli = instantiateCliente(rs);
				Aluguel obj = instantiateAluguel(rs, aut, cli);
				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Aluguel instantiateAluguel(ResultSet rs, Automovel aut, Cliente cli) throws SQLException {
		Aluguel obj = new Aluguel();
		obj.setId(rs.getInt("Id"));
		obj.setDataInicio(new java.util.Date(rs.getTimestamp("DataInicio").getTime()));
		obj.setDataFim(new java.util.Date(rs.getTimestamp("DataFim").getTime()));
		// obj.setDataInicio(rs.getDate("DataInicio"));
		obj.setAutomovel(aut);
		obj.setCliente(cli);
		return obj;
	}

	private Automovel instantiateAutomovel(ResultSet rs) throws SQLException {
		Automovel aut = new Automovel();
		aut.setId(rs.getInt("AutomovelId"));
		aut.setNome(rs.getString("AutNome"));
		aut.setMarca(rs.getString("AutMarca"));
		return aut;
	}

	private Cliente instantiateCliente(ResultSet rs) throws SQLException {
		Cliente cli = new Cliente();
		cli.setId(rs.getInt("ClienteId"));
		cli.setNome(rs.getString("CliNome"));
		cli.setEmail(rs.getString("CliEmail"));
		cli.setDataNasc(rs.getDate("CliDataNasc"));
		return cli;
	}

	@Override
	public List<Aluguel> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT * FROM aluguel ORDER BY Id");
			rs = st.executeQuery();

			List<Aluguel> list = new ArrayList<>();

			while (rs.next()) {
				Aluguel obj = new Aluguel();
				obj.setId(rs.getInt("Id"));
				obj.setDataInicio(new java.util.Date(rs.getTimestamp("DataInicio").getTime()));
				obj.setDataFim(new java.util.Date(rs.getTimestamp("DataFim").getTime()));
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Aluguel> findByAutomovel(Automovel automovel) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT aluguel.*,automovel.Nome as AutoNome " 
			+ "FROM aluguel INNER JOIN automovel "						
			+ "ON aluguel.AutomovelId = automovel.Id " 
			+ "WHERE AutomovelId = ? " + "ORDER BY Nome");

			st.setInt(1, automovel.getId());

			rs = st.executeQuery();

			List<Aluguel> list = new ArrayList<>();
			Map<Integer, Automovel> map = new HashMap<>();

			while (rs.next()) {

				Automovel aut = map.get(rs.getInt("AutomovelId"));

				if (aut == null) {
					aut = instantiateAutomovel(rs);
					map.put(rs.getInt("AutomovelId"), aut);
				}

				Aluguel obj = instantiateAluguel(rs, aut, null);
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Aluguel> findByCliente(Cliente cliente) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT aluguel.*,cliente.Nome as CliNome " 
			+ "FROM aluguel INNER JOIN cliente "				
			+ "ON aluguel.ClienteId = cliente.Id " 
			+ "WHERE ClienteId = ? " + "ORDER BY Nome");

			st.setInt(1, cliente.getId());

			rs = st.executeQuery();

			List<Aluguel> list = new ArrayList<>();
			Map<Integer, Cliente> map = new HashMap<>();

			while (rs.next()) {

				Cliente cli = map.get(rs.getInt("ClienteId"));

				if (cli == null) {
					cli = instantiateCliente(rs);
					map.put(rs.getInt("ClienteId"), cli);
				}

				Aluguel obj = instantiateAluguel2(rs, cli, null);
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Aluguel instantiateAluguel2(ResultSet rs, Cliente cli, Object cli2) {
		// TODO Auto-generated method stub
		return null;
	}
}


