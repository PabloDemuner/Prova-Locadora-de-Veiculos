package model.dao;

import java.util.List;

import modelo.entidades.Aluguel;
import modelo.entidades.Automovel;
import modelo.entidades.Cliente;

public interface AluguelDao {

	void insert(Aluguel obj);
	void update(Aluguel obj);
	void deleteById(Integer id);
	Aluguel findById(Integer id);
	List<Aluguel> findAll();
	List<Aluguel> findByAutomovel(Automovel automovel);
	List<Aluguel> findByCliente(Cliente cliente);
}
