# Sistem de Gestiune a Rezervarilor Feroviare

Acesta este un sistem avansat de ticketing feroviar, dezvoltat ca o solutie modulara, scalabila si performanta. Proiectul demonstreaza bunele practici in ingineria software, utilizand o arhitectura bazata pe microservicii si principii solide de design.

## Arhitectura Sistemului

Aplicatia este impartita in trei microservicii principale care comunica sincron (prin Feign Clients) si asincron (prin RabbitMQ):

1. Routing Service (Port 8080): Gestioneaza statiile, rutele, orarele si logica de cautare a legaturilor.
2. Booking Service (Port 8081): Gestioneaza rezervarile de bilete si inventarul de locuri, asigurand integritatea datelor si prevenind overbooking-ul.
3. Notification Service: Un serviciu care preia mesajele din cozile RabbitMQ si simuleaza trimiterea emailurilor de confirmare sau de alerta.

Avantajele arhitecturii pe microservicii in acest context:
* Scalabilitate independenta (ex. scalarea doar a serviciului de routing in perioade cu volum mare de cautari).
* Extensibilitate ridicata pentru adaugarea de noi functionalitati.
* Rezilienta si izolarea defectelor.

## Algoritmul de Cautare

Sistemul utilizeaza o abordare adaptata a algoritmului lui Dijkstra pentru a gasi rute directe si conexiuni cu escala (changeover).
* Sistemul verifica fezabilitatea escalei asigurandu-se ca timpul de plecare al trenului de legatura este strict ulterior timpului de sosire al primului tren.
* Rutele gasite sunt ordonate cronologic dupa timpul de plecare al primei curse.

## Principii de Design (SOLID & GRASP)

Dezvoltarea a respectat principiile programarii orientate pe obiecte:

* Single Responsibility Principle (SRP): Clasele au responsabilitati bine definite (ex. `AdminService` se ocupa exclusiv de logica de business, `SearchController` de expunerea rutelor HTTP).
* Information Expert (GRASP): Logica de gestionare a locurilor disponibile este responsabilitatea entitatii `TrainInventory`, care detine datele necesare pentru validare.
* Low Coupling: Microserviciile sunt decuplate, comunicand exclusiv prin interfete REST (Feign) si mesagerie asincrona (RabbitMQ).

## Instructiuni de Testare si Exemple (Input/Output)

Aceste exemple ilustreaza fluxul de testare al aplicatiei utilizand Postman. Toate request-urile ce implica date temporale utilizeaza formatul ISO-8601 (`yyyy-MM-dd'T'HH:mm:ss`).

### Faza 1: Crearea Datelor (Rol: Administrator)

1. Adaugare Statii
* URL: `POST http://localhost:8080/api/v1/admin/stations`
* Input:
```json
{"name": "Cluj"}
```
* Output Asteptat: `200 OK` si detaliile statiei create.

2. Adaugare Rute
* URL: `POST http://localhost:8080/api/v1/admin/routes`
* Input (Cluj -> Brasov):
```json
{"sourceStationId": 1, "destinationStationId": 2, "durationMinutes": 120}
```
* Output Asteptat: `200 OK` si detaliile rutei create.

3. Adaugare Orare Tren
* URL: `POST http://localhost:8080/api/v1/admin/schedules`
* Input (IR-100):
```json
{
  "scheduleId": "IR-100",
  "routeId": 1,
  "departureTime": "2026-05-15T08:00:00",
  "arrivalTime": "2026-05-15T10:00:00",
  "totalSeats": 50
}
```
* Output Asteptat: `200 OK` si detaliile programului creat.

### Faza 2: Functionalitati Publice (Rol: Client)

1. Cautarea unei calatorii cu escala
* URL: `GET http://localhost:8080/api/v1/search?from=Cluj&to=Bucuresti`
* Output Asteptat (`200 OK`):
```json
[
  {
    "journeyType": "ESCALA",
    "legs": [
      {
        "trainId": "IR-100",
        "source": "Cluj",
        "destination": "Brasov",
        "departureTime": "2026-05-15T08:00:00",
        "arrivalTime": "2026-05-15T10:00:00"
      },
      {
        "trainId": "IR-200",
        "source": "Brasov",
        "destination": "Bucuresti",
        "departureTime": "2026-05-15T11:00:00",
        "arrivalTime": "2026-05-15T13:30:00"
      }
    ]
  }
]
```

2. Rezervarea unui bilet (Anti-Overbooking)
* URL: `POST http://localhost:8081/api/v1/bookings`
* Input:
```json
{
  "customerEmail": "tobias.mirauta@student.utcn.ro",
  "scheduleId": "IR-100",
  "quantity": 1
}
```
* Output Asteptat: `200 OK`. In terminalul Notification Service se va inregistra confirmarea preluata prin RabbitMQ. Daca parametrul `quantity` depaseste locurile disponibile, se returneaza o eroare relevanta.

### Faza 3: Management Avansat (Rol: Administrator)

1. Declararea intarzierilor si alertarea clientilor
* URL: `PATCH http://localhost:8080/api/v1/admin/schedules/IR-100/delay?minutes=45`
* Output Asteptat: `200 OK`. Routing Service va interoga Booking Service, va prelua adresele de email si va publica un mesaj in RabbitMQ. Terminalul Notification Service va afisa alerta trimisa.

2. Vizualizarea rezervarilor per tren
* URL: `GET http://localhost:8081/api/v1/bookings/train/IR-100`
* Output Asteptat: `200 OK` si o lista JSON continand pasagerii ce au rezervat bilete pentru cursa `IR-100`.

## Tehnologii Utilizate
* Java 17 & Spring Boot 3
* Spring Cloud OpenFeign
* RabbitMQ
* PostgreSQL
* Docker & Docker Compose
* Jackson Datatype JSR310