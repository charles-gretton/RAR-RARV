import Params._

trait Solver {
	val inst: Instance
	val source = inst.source

	def solve: List[List[Customer]] = {
		//println("=============================================")
		val source = inst.source
		var disponibles = inst.customers - source
		var nn = List[List[Customer]]()

		while (!disponibles.isEmpty && nn.length < inst.vehiculos) {
			// mando un camion
			val visitas = camion(disponibles)
			nn = nn ::: List(visitas)

			// saco los visitados por ese camion
			disponibles = disponibles -- visitas
		}
		
		//inst.globalEvaporate()
		
		//println(nn.map(_.map(_.num)))

		nn
	}

	private def camion(nodos: List[Customer]): List[Customer] = {
		var hora:Double = 0
		var vecinos = nodos
		var actual = inst.source
		var capacidad = inst.capacidad
		var nn = actual :: Nil

		var prox:Customer = proximo(actual, vecinos, hora, capacidad)
		
		while (prox != null && !vecinos.isEmpty) {
			nn = nn ::: List(prox)

			// si llego antes que abra, a esperar				
			hora += prox.service + inst.tiempo(actual, prox, hora)
			capacidad -= prox.demand
			vecinos = vecinos - prox
			actual = prox

			prox = proximo(actual, vecinos, hora, capacidad)
		}

		// actualizar tau local
		inst.localTau(nn)

		nn
	}

	def insertable(nodo: Customer, vecino: Customer, hora: Double, capacidad: Int): Boolean = {
		val d = inst.distancia(nodo, vecino)
		hora + d < vecino.due && vecino.demand <= capacidad
	}

	def proximo(nodo: Customer, vecinos:List[Customer], hora: Double, capacidad: Int): Customer
}
