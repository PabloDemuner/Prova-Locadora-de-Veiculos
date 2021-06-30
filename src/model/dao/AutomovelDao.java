package model.dao;

import java.util.List;

import modelo.entidades.Automovel;

public interface AutomovelDao {

	void insert(Automovel obj);
	void update(Automovel obj);
	void deleteById(Integer id);
	Automovel findById(Integer id);
	List<Automovel> findAll();
}
