trait Solver {
	val inst: Instance
	val source = inst.source

	def solve: List[List[Customer]] = {
		val source = inst.source
		var disponibles = inst.customers - source
		var nn = List[List[Customer]]()

		while (!disponibles.isEmpty) {
			// mando un camion
			val visitas = camion(disponibles)
			nn = nn ::: List(visitas)

			// saco los visitados por ese camion
			disponibles = disponibles -- visitas
		}

		nn
	}

	private def camion(nodos: List[Customer]): List[Customer] = {
		var nn = List[Customer]()
		var hora:Double = 0
		var vecinos = nodos
		var actual = inst.source
		var capacidad = inst.capacidad

		var prox:Customer = proximo(actual, vecinos, hora, capacidad)

		while (prox != null && !vecinos.isEmpty) {
			nn = nn ::: List(prox)

			// si llego antes que abra, a esperar				
			val tiempoEspera = Math.max(0, prox.ready - (hora + inst.distancia(actual, prox)))
			hora += tiempoEspera + prox.service + inst.distancia(actual, prox)
			capacidad -= prox.demand
			vecinos = vecinos - prox
			actual = prox

			prox = proximo(actual, vecinos, hora, capacidad)
		}

		nn
	}

	def tiempo(actual: Customer, prox: Customer, hora: Double): Double = {
		val tiempoEspera = Math.max(0, prox.ready - (hora + inst.distancia(actual, prox)))

		tiempoEspera + inst.distancia(actual, prox)
	}

	def factible(nodo: Customer, vecino: Customer, hora: Double, capacidad: Int): Boolean = {
		val d = inst.distancia(nodo, vecino)
		hora + d < vecino.due && vecino.demand <= capacidad
	}

	def proximo(nodo: Customer, vecinos:List[Customer], hora: Double, capacidad: Int): Customer
}