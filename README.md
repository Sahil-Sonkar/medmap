# Medmap

### Clone the Repository

1. **Open IntelliJ IDEA**.
2. **Go to VCS > Get from Version Control**.
3. **Enter the URL of the GitHub repository** (`https://github.com/Sahil-Sonkar/medmap`) in the URL field and click `Clone`.
4. **Choose a directory** where you want to save the project and click `Clone`.

### Open the Project in IntelliJ

1. After cloning, IntelliJ might automatically open the project. If not:
2. **Go to File > Open**, and navigate to the cloned repository directory.
3. Select the `pom.xml` file and click `Open as Project`.
4. IntelliJ will then import the project dependencies and set up the project.

### Configure the MySQL Database

1. **Install MySQL** if you haven't already. You can download it from the [official MySQL website](https://dev.mysql.com/downloads/mysql/).
2. **Start the MySQL server** and ensure it is running.

### Set Up Database and User

1. **Open a terminal** or command prompt.
2. **Log into MySQL**:
   ```bash
   mysql -u root -p
   ```
   
### Create the database:

```sql
CREATE DATABASE medmapdb;
```

Grant privileges to the user:

```sql
GRANT ALL PRIVILEGES ON medmapdb.* TO 'root'@'localhost' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
```

### Build and Run the Spring Boot Application

   1. Build the project: Go to Build > Build Project.
   2. Run the application: Go to Run > Run 'MedmapApplication'.

### Import Postman Collection

1. Open Postman
2. Click on Import in the upper left corner.
3. Select Upload Files.
4. Navigate to the `src/main/resources` folder in your cloned repository.
5. Select the Postman collection file, `Medmap.postman_collection.json`.
6. Click `Open` to import the collection.

The collection should now be available in your Postman under Collections.

## Verify the Application

Check the console for any errors related to the database connection.
Access the application via the browser or any REST client (like Postman) to ensure it is running correctly.

