# luka-test-bank
Luka - Spring test project


Spring Boot project with Spring Data, in-mem db H2, SpringSecurity, 
Mustache template engine, client web appilcation and REST endpoints.

Summary - 
H2 in memory database 
Domain objects User, UserRole, MoneyTransaction -
Repositories for these domain objects - no implementations of interfaces just the interfaces because app is using only basic CRUD operations.
For the same reasons no tests were written for repository interfaces, no custom code has been added to it.


Only home ["/"] page and api calls ["/api/**"] are permitted to be called without authentication, every other uri is checked for spring security authentication.
I also disabled csrf for API calls so that the rest service can be tested and played with without any browser and authentication.


If you want to see the app, start the jar and go to: 
http://localhost:8080/
There should be a welcome page that only presents the login button.
During app startup I am creating users with ADMIN and USER roles:

email- luka.matovic 
pass-1234
ROLE_ADMIN

email- user
pass-1234
ROLE_USER

If you login with USER you can only see your own balance, transactions, make a deposit and withdrawal, the rest is denied by checking currentUser authorities.
If you login with ADMIN, you will be presented with home page, the list of the users with appropriate balances. When you select a row in table it will take you to User Details page (same as the one that USER can view for himself), with transactions and balance, and ADMIN user can also withdraw and deposit money for every user.
