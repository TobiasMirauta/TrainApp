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

* Single Responsibility Principle (SRP): Clasele au responsabilitati bine definite (ex. AdminService se ocupa exclusiv de logica de business, SearchController de expunerea rutelor HTTP).
* Information Expert (GRASP): Logica de gestionare a locurilor disponibile este responsabilitatea entitatii TrainInventory, care detine datele necesare pentru validare.
* Low Coupling: Microserviciile sunt decuplate, comunicand exclusiv prin interfete REST (Feign) si mesagerie asincrona (RabbitMQ).

## Instructiuni de Testare si Exemple (Input/Output)

Aceste exemple ilustreaza fluxul de testare al aplicatiei utilizand Postman sau interfata grafica integrata. Toate request-urile ce implica date temporale utilizeaza formatul ISO-8601 (yyyy-MM-dd'T'HH:mm:ss).

### Faza 1: Crearea Datelor (Rol: Administrator)

1. Adaugare Statii
* URL: POST http://localhost:8080/api/v1/admin/stations
* Input: {"name": "Cluj"}
* Output Asteptat: 200 OK si detaliile statiei create.

2. Adaugare Rute
* URL: POST http://localhost:8080/api/v1/admin/routes
* Input (Cluj -> Brasov): {"sourceStationId": 1, "destinationStationId": 2, "durationMinutes": 120}
* Output Asteptat: 200 OK si detaliile rutei create.

### Faza 2: Functionalitati Publice (Rol: Client)

1. Cautarea unei calatorii cu escala
* URL: GET http://localhost:8080/api/v1/search?from=Cluj&to=Bucuresti
* Output Asteptat (200 OK): Un obiect de tip ESCALA continand cele doua segmente de drum (legs) formatate corespunzator.

2. Rezervarea unui bilet (Anti-Overbooking)
* URL: POST http://localhost:8081/api/v1/bookings
* Input: {"customerEmail": "student@example.com", "scheduleId": "IR-100", "quantity": 1}
* Output Asteptat: 200 OK. Notificarea de confirmare este procesata asincron.

## Perspective de Viitor (Future Work)

Daca as fi avut mai mult timp la dispozitie, as fi extins proiectul prin urmatoarele directii:

1. Modernizarea Interfetei Grafice: Desi sistemul dispune de un modul frontend functional bazat pe HTML/JS, o etapa ulterioara ar presupune migrarea catre un framework modern de tip Single Page Application (React sau Angular). Acest lucru ar permite o gestionare mai dinamica a starii si o experienta de utilizare mult mai fluida.
2. Serviciu de Notificari Real: Inlocuirea simularilor din Notification Service cu o integrare reala cu un server SMTP sau un API de mailing (precum SendGrid), pentru a livra email-uri veritabile in inbox-ul clientilor.
3. Securitate Avansata: Implementarea Spring Security cu protocolul OAuth2 sau JWT pentru a securiza rutele administrative si pentru a permite utilizatorilor sa isi creeze conturi si sa isi acceseze istoricul rezervarilor.
4. Optimizarea Algoritmului de Rute: Desi implementarea actuala este stabila, am explorat teoretic posibilitatea de a adauga filtre avansate (ex. cautarea celei mai ieftine rute, a celei mai rapide sau a celei cu numar minim de schimbari).
5. Observabilitate: Integrarea unor instrumente de monitorizare precum Prometheus si Grafana pentru a urmari performanta fiecarui microserviciu si sanatatea cozilor de mesaje din RabbitMQ in timp real.

## Tehnologii Utilizate
* Java 17 & Spring Boot 3
* Spring Cloud OpenFeign (Comunicare Sincrona)
* RabbitMQ (Comunicare Asincrona)
* PostgreSQL (Persistenta)
* Docker & Docker Compose (Containerizare)
* Jackson Datatype JSR310 (Formatare date)
