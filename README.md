# Mínim 1 DSA (05/11/2026)

He partit del rest-example del professor i al final he esborrat les classes de Tracks.

# Primera part 
Fet en paper. Estructures: HashMap per a drons i pilots, Stack per al magatzem (es repara el de la porta, LIFO), LinkedList de plans a cada dron i pilot, i ArrayList de posicions a cada pla.

# Segona part 
DroneManager + DroneManagerImpl (Singleton, logs INFO i ERROR), 5 excepcions i DroneManagerTest amb 4 tests.
Funciona: sí, passen els 4 tests.

# Tercera part 
Operacions (ruta base /dsaApp/drones):
- POST /drones -> afegir dron (201, 400)
- POST /drones/pilots -> afegir pilot (201, 400)
- GET /drones/ranking -> drons per hores (200)
- GET /drones/pilots/ranking -> pilots per hores (200)
- PUT /drones/storage/{droneId} -> guardar al magatzem (200, 404, 409)
- PUT /drones/storage/repair -> reparar (200, 404)
- POST /drones/flightplans -> crear pla de vol (201, 400, 404, 409)
- GET /drones/pilots/{pilotId}/flightplans -> plans d'un pilot (200, 404)
- GET /drones/{droneId}/flightplans -> plans d'un dron (200, 404)

DroneService amb les 9 operacions --> Swagger a http://localhost:8080/swagger/
Faig servir DroneInfo i PilotInfo com a objectes de transferència.
Funciona: sí (provat a Swagger)

# Notes
- Hi ha 2 drons, 2 pilots i 1 pla precarregats per provar.

# Problemes/ Pendent
- Les classes de Tracks donaven errors i les he esborrat.
- No he fet tests del servei REST, només del DroneManager.