object Params {
	//////////////
	// Hormigas //
	//////////////
	
	//val α = 1
	//val β = 1 // .. 5
	val q0 = 0.85
	val p = 0.1
	val ξ = 0.1
	
	// el peso al vector nonVisited
	val ω = 1
	
	// valor dummy
	var τ0: Double = 0
	
	///////////
	// R & R //
	///////////
	
	// prob de hacer arruine espacial o aleatorio
	val δ:Double = 1
	// prob de arruine de cada cliente
	val π:Double = 0
//	val πv:Double = 0.9
}
