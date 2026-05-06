# BILABONNEMENT

## Deployment
- URL: https://wheelsup.dk

### Login
- ADMIN:
  - Email: `admin@biludlejning.dk`
  - Password: `password_placeholder`
- DATAREGISTRERING:
  - Email: `maria@biludlejning.dk`
  - Password: `password_placeholder`
- SKADE/UDBEDRING:
  - Email: `thomas@biludlejning.dk`
  - Password: `password_placeholder`
- FORETNINGSUDVIKLING:
  - Email: `simon@biludlejning.dk`
  - Password: `password_placeholder`

### Github Repository
> https://github.com/natfresco-DK/Biludlejning


## Forudsætninger
*Der tages forbehold for at følgende programmer er installeret.*
> - Java 17+
> - Maven
> - MySQL
> - En IDE (f.eks. IntelliJ, Eclipse, VSCode)

## Installation & Opsætning

### Gennemgang
> 1. Oprettelse af MySQL database og bruger
> 2. Konfiguration af miljøvariabler
> 3. Kørsel af applikationen

### Information
- "<>" = Står for required og skal erstattes med de relevante oplysninger.
- Eksempel: ```<username>``` skal erstattes med det ønskede brugernavn. f.eks. ```biludlejning_admin```.



### Opret en MySQL Database

**1. Opret en MySQL database ved at køre følgende kommando i MySQL terminalen:**
```sql
CREATE DATABASE biludlejning;
```

**2. Opret en MySQL bruger og giv den adgang til databasen ved at køre følgende kommando i MySQL terminalen:**
```sql
CREATE USER '<username>'@'localhost' IDENTIFIED BY '<password>';
GRANT ALL PRIVILEGES ON biludlejning.* TO '<username>'@'localhost';
FLUSH PRIVILEGES;
```

### Opsætning af miljøvariabler
**Opret environment variables med følgende:**
```
DB_URL=jdbc:mysql://localhost:3306/biludlejning
DB_USER=<username>
DB_PASSWORD=<password>
```

### Kør applikationen
> Kør applikationen ved at starte den i din IDE.
