package modelo.services;

import java.util.ArrayList;
import java.util.List;

import modelo.entidades.Automovel;

public class AutomovelService {

	public List<Automovel> findAll() {
		List<Automovel> list = new ArrayList<>();
		list.add(new Automovel( 1, "March", "Nissan"));
		list.add(new Automovel( 2, "Voyage", "Vowksvagen"));
		list.add(new Automovel( 3, "Toro", "Fiat"));
		return list;
	}
}
