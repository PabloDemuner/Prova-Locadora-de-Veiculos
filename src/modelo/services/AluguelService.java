package modelo.services;

import java.util.List;

import model.dao.AluguelDao;
import model.dao.DaoFactory;
import modelo.entidades.Aluguel;

public class AluguelService {
	
	private AluguelDao aluguelDao = DaoFactory.createAluguelDao();

	//Efetua a busca de todos os aluguels
	public List<Aluguel> findAll() {
		return aluguelDao.findAll();
	}
	
	//Insere ou atualizar um aluguel
	public void saveOrUpdate(Aluguel obj) {
		if (obj.getId() == null) {
			aluguelDao.insert(obj);
		}
		else {
			aluguelDao.update(obj);
		}
	}
	
	//Deleta Aluguel do BD
	public void delete(Aluguel obj) {
		aluguelDao.deleteById(obj.getId());
	}
}
