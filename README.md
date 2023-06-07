# Action Tracker Service
### Build the project:
$ gradle clean build -x test
<br />
$ gradle flywayMigrate -i
<br />
$ gradle bootRun
<br />

Then execute the initial sql INSERT STATEMENTS  from USER, ROLE, PRIVILEGE, AND ROLE_PRIVILEGE_MAPPING entity classes 

Login Path: http://localhost:8080/action-tracker-api/api/auth/login
Refresh Token Path: http://localhost:8080/action-tracker-api/api/auth/refresh

API TEST PATH:
http://localhost:8080/action-tracker-api/api/test/demo/create
http://localhost:8080/action-tracker-api/api/test/demo/update
http://localhost:8080/action-tracker-api/api/test/demo/read
http://localhost:8080/action-tracker-api/api/test/demo/delete


###Technology:
spring boot:2.1.13
<br>
gradle-6.3

#Swagger-UI
http://localhost:8080/action-tracker-api/swagger-ui.html
