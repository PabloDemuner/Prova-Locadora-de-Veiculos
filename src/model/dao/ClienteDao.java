package model.dao;

import java.util.List;

import modelo.entidades.Automovel;
import modelo.entidades.Cliente;

public interface ClienteDao {

	void insert(Cliente obj);
	void update(Cliente obj);
	void deleteById(Integer id);
	Cliente findById(Integer id);
	List<Cliente> findAll();
	List<Cliente> findByAutomovel(Automovel automovel);
}
