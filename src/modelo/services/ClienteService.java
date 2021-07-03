package modelo.services;

import java.util.List;

import model.dao.ClienteDao;
import model.dao.DaoFactory;
import modelo.entidades.Cliente;

public class ClienteService {
	
	private ClienteDao clienteDao = DaoFactory.createClienteDao();

	//Efetua a busca de todos os clientes
	public List<Cliente> findAll() {
		return clienteDao.findAll();
	}
	
	//Insere ou atualizar um cliente
	public void saveOrUpdate(Cliente obj) {
		if (obj.getId() == null) {
			clienteDao.insert(obj);
		}
		else {
			clienteDao.update(obj);
		}
	}
	
	//Deleta Cliente do BD
	public void delete(Cliente obj) {
		clienteDao.deleteById(obj.getId());
	}
}
