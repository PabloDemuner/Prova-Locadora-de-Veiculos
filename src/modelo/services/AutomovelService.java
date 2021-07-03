package modelo.services;

import java.util.List;

import model.dao.AutomovelDao;
import model.dao.DaoFactory;
import modelo.entidades.Automovel;

public class AutomovelService {
	
	private AutomovelDao automovelDao = DaoFactory.createAutomovelDao();

	//Busca de todos os automoveis
	public List<Automovel> findAll() {
		return automovelDao.findAll();
	}
	
	//Metodo para inserir ou atualizar um automovel
	public void saveOrUpdate(Automovel obj) {
		if (obj.getId() == null) {
			automovelDao.insert(obj);
		}
		else {
			automovelDao.update(obj);
		}
	}
	
	//Deleta Automovel do BD
	public void delete(Automovel obj) {
		automovelDao.deleteById(obj.getId());
	}
}
